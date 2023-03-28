import UIKit

import CommonCorePublic
import LayoutKitInterface

public protocol PagerEventHandling {
  func handle(event: PagerSelectedPageChangedEvent, from sender: AnyObject)
}

public final class PagerSelectedPageChangedEvent: AppActionEventProtocol {
  public let path: UIElementPath
  public let selectedPageIndex: Int

  public init(
    path: UIElementPath,
    selectedPageIndex: Int
  ) {
    self.path = path
    self.selectedPageIndex = selectedPageIndex
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? PagerEventHandling)?.handle(event:from:)
  }
}
