import Foundation
import UIKit

import CommonCorePublic

public final class UIActionEventPerformingAdapter<HandledType>: UIActionEventPerforming {
  public typealias Transform = (UIActionEvent) -> HandledType?
  public typealias Handler = (HandledType) -> Void

  private let transform: Transform
  private let handler: Handler

  public init(transform: @escaping Transform, handler: @escaping Handler) {
    self.transform = transform
    self.handler = handler
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from sender: AnyObject) {
    if let intent = transform(event) {
      handler(intent)
    } else {
      guard let sender = sender as? UIResponder else {
        assertionFailure()
        return
      }
      event.sendFrom(sender)
    }
  }
}
