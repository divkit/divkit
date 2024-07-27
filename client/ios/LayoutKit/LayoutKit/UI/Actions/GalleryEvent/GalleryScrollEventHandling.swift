import UIKit

import VGSL

public protocol GalleryScrollEventHandling {
  func handle(galleryScrollEvent: GalleryScrollEvent, from sender: AnyObject)
}

public final class GalleryScrollEvent: AppActionEventProtocol {
  public enum Direction {
    case none
    case forward
    case backward

    public init(from: CGFloat, to: CGFloat) {
      if from < to {
        self = .forward
      } else if from > to {
        self = .backward
      } else {
        self = .none
      }
    }
  }

  public let path: UIElementPath
  public let direction: Direction
  public let firstVisibleItemIndex: Int
  public let lastVisibleItemIndex: Int
  public let itemsCount: Int

  public init(
    path: UIElementPath,
    direction: Direction,
    firstVisibleItemIndex: Int,
    lastVisibleItemIndex: Int,
    itemsCount: Int
  ) {
    self.path = path
    self.direction = direction
    self.firstVisibleItemIndex = firstVisibleItemIndex
    self.lastVisibleItemIndex = lastVisibleItemIndex
    self.itemsCount = itemsCount
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? GalleryScrollEventHandling)?.handle(galleryScrollEvent:from:)
  }
}
