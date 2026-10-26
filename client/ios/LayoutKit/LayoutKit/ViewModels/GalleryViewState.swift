import CoreGraphics
import Foundation
import VGSL

public struct GalleryViewState: ElementState, Equatable {
  @frozen
  public enum Position: Equatable {
    case offset(_ value: CGFloat, firstVisibleItemIndex: Int = 0)
    case paging(index: CGFloat)

    static let zeroOffset: Self = .offset(0, firstVisibleItemIndex: 0)
    static let zeroPaging: Self = .paging(index: 0)

    public var offset: CGFloat? {
      switch self {
      case let .offset(value, _):
        value
      case .paging:
        nil
      }
    }

    public var pageIndex: CGFloat? {
      switch self {
      case .offset:
        nil
      case let .paging(index: index):
        index
      }
    }

    public var isPaging: Bool {
      switch self {
      case .paging:
        true
      case .offset:
        false
      }
    }

    var zero: Self {
      switch self {
      case .offset: .zeroOffset
      case .paging: .zeroPaging
      }
    }

    public static func ==(_ lhs: Position, _ rhs: Position) -> Bool {
      let accuracy = CGFloat(1e-4)
      switch (lhs, rhs) {
      case let (
        .offset(lhsValue, lhsFirstVisibleItemIndex),
        .offset(rhsValue, rhsFirstVisibleItemIndex)
      ):
        return lhsFirstVisibleItemIndex == rhsFirstVisibleItemIndex
          && lhsValue.isApproximatelyEqualTo(rhsValue, withAccuracy: accuracy)
      case let (.paging(index: lhs), .paging(index: rhs)):
        return lhs.isApproximatelyEqualTo(rhs, withAccuracy: accuracy)
      case (.paging, .offset):
        return false
      case (.offset, .paging):
        return false
      }
    }
  }

  public let contentPosition: Position
  public let itemsCount: Int
  public let isScrolling: Bool
  public let scrollRange: CGFloat?
  public let animated: Bool
  /// Identity distinguishes commands with the same destination.
  public let scrollRequest: ScrollRequest?

  public init(
    contentOffset: CGFloat,
    itemsCount: Int
  ) {
    self.contentPosition = .offset(contentOffset)
    self.itemsCount = itemsCount
    self.isScrolling = false
    self.scrollRange = nil
    self.animated = true
    self.scrollRequest = nil
  }

  public init(
    contentPageIndex: CGFloat,
    itemsCount: Int,
    animated: Bool,
    scrollRequest: ScrollRequest? = nil
  ) {
    self.contentPosition = .paging(index: contentPageIndex)
    self.itemsCount = itemsCount
    self.isScrolling = false
    self.scrollRange = nil
    self.animated = animated
    self.scrollRequest = scrollRequest
  }

  public init(
    contentPosition: Position,
    itemsCount: Int,
    isScrolling: Bool,
    scrollRange: CGFloat? = nil,
    animated: Bool,
    scrollRequest: ScrollRequest? = nil
  ) {
    self.contentPosition = contentPosition
    self.itemsCount = itemsCount
    self.isScrolling = isScrolling
    self.scrollRange = scrollRange
    self.animated = animated
    self.scrollRequest = scrollRequest
  }

  public static func ==(lhs: GalleryViewState, rhs: GalleryViewState) -> Bool {
    let accuracy = CGFloat(1e-4)

    let areScrollRangesEqual = switch (lhs.scrollRange, rhs.scrollRange) {
    case (.none, .none): true
    case (.some, .none), (.none, .some): false
    case let (.some(lhs), .some(rhs)): lhs.isApproximatelyEqualTo(rhs, withAccuracy: accuracy)
    }

    return lhs.contentPosition == rhs.contentPosition
      && lhs.itemsCount == rhs.itemsCount
      && lhs.isScrolling == rhs.isScrolling
      && areScrollRangesEqual
      && lhs.animated == rhs.animated
      && lhs.scrollRequest == rhs.scrollRequest
  }
}

extension GalleryViewState {
  func withScrollRange(_ range: CGFloat) -> GalleryViewState {
    GalleryViewState(
      contentPosition: contentPosition, itemsCount: itemsCount, isScrolling: isScrolling,
      scrollRange: range, animated: animated, scrollRequest: scrollRequest
    )
  }

  func completingScroll(itemsCount: Int, animated: Bool) -> GalleryViewState {
    GalleryViewState(
      contentPosition: contentPosition, itemsCount: itemsCount, isScrolling: false,
      scrollRange: scrollRange, animated: animated
    )
  }

  func resolvingScrollRequest(
    model: GalleryViewModel,
    layout: GalleryViewLayouting,
    currentOffset: CGFloat?,
    resolveDelta: Bool,
    finishImmediately: Bool,
    interruptedTarget: Position? = nil
  ) -> GalleryViewState {
    let position: Position = if let interruptedTarget {
      interruptedTarget
    } else if resolveDelta, let scrollRequest,
              !scrollRequest.destinations.isEmpty {
      destination(for: scrollRequest, model: model, layout: layout, offset: currentOffset)
    } else {
      contentPosition
    }
    return GalleryViewState(
      contentPosition: position, itemsCount: itemsCount, isScrolling: isScrolling,
      scrollRange: scrollRange, animated: animated && !finishImmediately,
      scrollRequest: scrollRequest
    )
  }

  private func destination(
    for request: ScrollRequest,
    model: GalleryViewModel,
    layout: GalleryViewLayouting,
    offset: CGFloat?
  ) -> Position {
    var position = offset.map { Position.offset($0) } ?? contentPosition
    let range = offset == nil ? scrollRange ?? 0 : layout.maxContentOffset
    for command in request.destinations {
      switch command {
      case let .item(index):
        position = .paging(index: index + CGFloat(model.infiniteCorrection))
      case let .offset(value):
        position = .offset(max(0, min(value, range)))
      case let .relative(delta):
        position = destination(
          for: delta,
          from: position,
          model: model,
          layout: layout,
          range: range
        )
      }
    }
    return position
  }

  private func destination(
    for delta: ScrollDelta,
    from position: Position,
    model: GalleryViewModel,
    layout: GalleryViewLayouting,
    range: CGFloat
  ) -> Position {
    switch delta {
    case .items:
      let pageIndex = position.pageIndex ?? layout.pageIndex(forContentOffset: position.offset ?? 0)
      let correction = CGFloat(model.infiniteCorrection)
      let realPageIndex = pageIndex - correction
      let currentIndex = layout is PagerViewLayout ? realPageIndex.rounded() : realPageIndex
      return .paging(index: delta.destinationIndex(
        from: currentIndex, count: model.itemsCountWithoutInfinite, wraps: model.infiniteScroll
      ) + correction)
    case .offset:
      let offset = position.offset ?? layout.contentOffset(pageIndex: position.pageIndex ?? 0)
      return .offset(delta.destinationOffset(from: offset, range: range))
    }
  }

  public func resetToModelIfInconsistent(
    _ model: GalleryViewModel,
    maxValidScrollRange: CGFloat? = nil
  ) -> GalleryViewState {
    let itemsCount = model.items.count
    let newScrollRange = maxValidScrollRange ?? scrollRange

    guard itemsCount > 0 else {
      return GalleryViewState(
        contentPosition: contentPosition.zero,
        itemsCount: 0,
        isScrolling: isScrolling,
        scrollRange: newScrollRange,
        animated: false
      )
    }

    let (contentPosition, animated) = normalizedPosition(
      for: model, maxValidScrollRange: maxValidScrollRange
    )

    return GalleryViewState(
      contentPosition: contentPosition,
      itemsCount: itemsCount,
      isScrolling: isScrolling,
      scrollRange: newScrollRange,
      animated: animated,
      scrollRequest: scrollRequest
    )
  }

  private func normalizedPosition(
    for model: GalleryViewModel,
    maxValidScrollRange: CGFloat?
  ) -> (Position, Bool) {
    switch contentPosition {
    case let .paging(index):
      if index < 0 || index >= CGFloat(model.items.count) {
        return (contentPosition.zero, false)
      }
      return (contentPosition, animated)
    case let .offset(value, firstVisibleItemIndex):
      switch model.scrollMode {
      case .autoPaging, .fixedPaging:
        return (.zeroPaging, false)
      case .default:
        return normalizedOffset(
          value, firstVisibleItemIndex: firstVisibleItemIndex,
          itemsCount: model.items.count, maxValidScrollRange: maxValidScrollRange
        )
      }
    }
  }

  private func normalizedOffset(
    _ value: CGFloat,
    firstVisibleItemIndex: Int,
    itemsCount: Int,
    maxValidScrollRange: CGFloat?
  ) -> (Position, Bool) {
    let validRange = maxValidScrollRange.map { max(0, $0) }
    let isIndexInvalid = firstVisibleItemIndex < 0 || firstVisibleItemIndex >= itemsCount
    if let validRange,
       isIndexInvalid || value.isApproximatelyGreaterThan(validRange, withAccuracy: 1e-4) {
      // Keep the visible position when content shrinks, clamping it to the trailing edge.
      let clampedOffset = clamp(value, min: 0, max: validRange)
      let clampedIndex = clampedOffset > 0
        ? clamp(firstVisibleItemIndex, min: 0, max: itemsCount - 1)
        : 0
      return (.offset(clampedOffset, firstVisibleItemIndex: clampedIndex), false)
    }
    if isIndexInvalid {
      // Before layout only repair the index; the range-aware pass must retain the offset.
      return (.offset(
        value, firstVisibleItemIndex: clamp(firstVisibleItemIndex, min: 0, max: itemsCount - 1)
      ), false)
    }
    return (contentPosition, animated)
  }
}
