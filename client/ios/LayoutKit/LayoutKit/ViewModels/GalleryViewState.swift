import CoreGraphics
import Foundation
import VGSL

public struct GalleryViewState: ElementState, Equatable {
  @frozen
  public enum Position: Equatable {
    case offset(_ value: CGFloat, firstVisibleItemIndex: Int = 0)
    case paging(index: CGFloat)

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

  public init(
    contentOffset: CGFloat,
    itemsCount: Int
  ) {
    self.contentPosition = .offset(contentOffset)
    self.itemsCount = itemsCount
    self.isScrolling = false
    self.scrollRange = nil
    self.animated = true
  }

  public init(
    contentPageIndex: CGFloat,
    itemsCount: Int,
    animated: Bool
  ) {
    self.contentPosition = .paging(index: contentPageIndex)
    self.itemsCount = itemsCount
    self.isScrolling = false
    self.scrollRange = nil
    self.animated = animated
  }

  public init(
    contentPosition: Position,
    itemsCount: Int,
    isScrolling: Bool,
    scrollRange: CGFloat? = nil,
    animated: Bool
  ) {
    self.contentPosition = contentPosition
    self.itemsCount = itemsCount
    self.isScrolling = isScrolling
    self.scrollRange = scrollRange
    self.animated = animated
  }

  public static func ==(lhs: GalleryViewState, rhs: GalleryViewState) -> Bool {
    let accuracy = CGFloat(1e-4)

    let areScrollRangesEqual = switch (lhs.scrollRange, rhs.scrollRange) {
    case (.none, .none): true
    case (.some, .none), (.none, .some): false
    case let (.some(lhs), .some(rhs)): lhs.isApproximatelyEqualTo(rhs, withAccuracy: accuracy)
    }

    return lhs.contentPosition == rhs.contentPosition
      && lhs.itemsCount == lhs.itemsCount
      && lhs.isScrolling == rhs.isScrolling
      && areScrollRangesEqual
      && lhs.animated == rhs.animated
  }
}

extension GalleryViewState {
  public func resetToModelIfInconsistent(_ model: GalleryViewModel) -> GalleryViewState {
    let newContentPosition: Position
    let itemsCount = model.items.count
    switch contentPosition {
    case let .paging(index: index):
      if index < 0 || index >= CGFloat(itemsCount) {
        newContentPosition = .paging(index: 0)
      } else {
        newContentPosition = .paging(index: index)
      }
    case .offset:
      switch model.scrollMode {
      case .autoPaging, .fixedPaging:
        newContentPosition = .paging(index: 0)
      case .default:
        newContentPosition = contentPosition
      }
    }
    return GalleryViewState(
      contentPosition: newContentPosition,
      itemsCount: itemsCount,
      isScrolling: isScrolling,
      scrollRange: scrollRange,
      animated: animated
    )
  }
}
