import UIKit

import CommonCorePublic
import LayoutKitInterface

public protocol AnalyticsTouchEventHandling {
  func handle(touchEvent: AnalyticsTouchEvent, from sender: AnyObject)
}

public final class AnalyticsTouchEvent: AppActionEventProtocol {
  public enum TouchType {
    case click
    case longPress
  }

  public let touchType: TouchType
  public let path: UIElementPath

  public init(touchType: TouchType, path: UIElementPath) {
    self.touchType = touchType
    self.path = path
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? AnalyticsTouchEventHandling)?.handle(touchEvent:from:)
  }
}
