import CoreGraphics
import Foundation

import CommonCorePublic

public struct GalleryViewState: ElementState, Equatable {
  @frozen
  public enum Position: Equatable {
    case offset(_ value: CGFloat, firstVisibleItemIndex: Int = 0)
    case paging(index: CGFloat)

    public var offset: CGFloat? {
      switch self {
      case let .offset(value, _):
        return value
      case .paging:
        return nil
      }
    }

    public var pageIndex: CGFloat? {
      switch self {
      case .offset:
        return nil
      case let .paging(index: index):
        return index
      }
    }

    public var isPaging: Bool {
      switch self {
      case .paging:
        return true
      case .offset:
        return false
      }
    }

    public static func ==(_ lhs: Position, _ rhs: Position) -> Bool {
      let accuracy = CGFloat(1e-4)
      switch (lhs, rhs) {
      case let (.offset(lhsValue, lhsFirstVisibleItemIndex), .offset(rhsValue, rhsFirstVisibleItemIndex)):
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

  public init(
    contentOffset: CGFloat,
    itemsCount: Int
  ) {
    self.contentPosition = .offset(contentOffset)
    self.itemsCount = itemsCount
    self.isScrolling = false
  }

  public init(
    contentPageIndex: CGFloat,
    itemsCount: Int
  ) {
    self.contentPosition = .paging(index: contentPageIndex)
    self.itemsCount = itemsCount
    self.isScrolling = false
  }

  public init(
    contentPosition: Position,
    itemsCount: Int,
    isScrolling: Bool
  ) {
    self.contentPosition = contentPosition
    self.itemsCount = itemsCount
    self.isScrolling = isScrolling
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
      isScrolling: isScrolling
    )
  }
}
