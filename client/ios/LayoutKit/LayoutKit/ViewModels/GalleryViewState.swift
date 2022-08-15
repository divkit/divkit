import CoreGraphics
import Foundation

import CommonCore

public struct GalleryViewState: ElementState, Equatable {
  public enum Position: Equatable {
    case offset(CGFloat)
    case paging(index: CGFloat)

    public var offset: CGFloat? {
      switch self {
      case let .offset(value):
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
      case let (.offset(lhs), .offset(rhs)):
        return lhs.isApproximatelyEqualTo(rhs, withAccuracy: accuracy)
      case let (.paging(index: lhs), .paging(index: rhs)):
        return lhs.isApproximatelyEqualTo(rhs, withAccuracy: accuracy)
      case (.paging, .offset):
        return false
      case (.offset, .paging):
        return false
      }
    }
  }

  public var contentPosition: Position

  public static let `default` = GalleryViewState(contentOffset: 0)

  public init(contentOffset: CGFloat) {
    self.contentPosition = .offset(contentOffset)
  }

  public init(contentPageIndex: CGFloat) {
    self.contentPosition = .paging(index: contentPageIndex)
  }

  public init(contentPosition: Position) {
    self.contentPosition = contentPosition
  }
}

extension GalleryViewState {
  public func resetToModelIfInconsistent(_ model: GalleryViewModel) -> GalleryViewState {
    modified(self) {
      switch contentPosition {
      case let .paging(index: index):
        if index < 0 || index >= CGFloat(model.items.count) {
          $0.contentPosition = .paging(index: 0)
        } else {
          $0.contentPosition = .paging(index: index)
        }
      case .offset:
        switch model.scrollMode {
        case .autoPaging, .fixedPaging:
          $0.contentPosition = .paging(index: 0)
        case .default:
          break
        }
      }
    }
  }
}
