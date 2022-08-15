import UIKit

import LayoutKitInterface

public protocol UIActionEventPerforming {
  func perform(uiActionEvents events: [UIActionEvent], from sender: AnyObject)
  func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject)
}

extension UIActionEventPerforming {
  public func perform(uiActionEvents events: [UIActionEvent], from sender: AnyObject) {
    events.forEach { perform(uiActionEvent: $0, from: sender) }
  }
}

public final class UIActionEvent: AppActionEventProtocol {
  public let originalSender: UIResponder
  public let uiAction: UserInterfaceAction

  public init(uiAction: UserInterfaceAction, originalSender: UIResponder) {
    self.originalSender = originalSender
    self.uiAction = uiAction
  }

  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? UIActionEventPerforming)?.perform(uiActionEvent:from:)
  }
}

extension Array: AppActionEventProtocol where Element == UIActionEvent {
  public func makeHandler(responder: UIResponder) -> Handler? {
    (responder as? UIActionEventPerforming)?.perform(uiActionEvents:from:)
  }
}

extension UIActionEvent {
  public var payload: UserInterfaceAction.Payload {
    uiAction.payload
  }
}

extension Array where Element == UIActionEvent {
  public func handlingURLPayload(
    handleEvent: @escaping (UIActionEvent, _ isLast: Bool) -> Void
  ) -> [UIActionEvent] {
    var eventsToHandle = [UIActionEvent]()
    var notHandledEvents = [UIActionEvent]()
    for event in self {
      if event.uiAction.url != nil {
        eventsToHandle.append(event)
        if let newEvent = event.removingURLPayload() {
          notHandledEvents.append(newEvent)
        }
      } else {
        notHandledEvents.append(event)
      }
    }
    for (offset, event) in eventsToHandle.enumerated() {
      let isLast = (offset == eventsToHandle.count - 1)
      handleEvent(event, isLast)
    }
    return notHandledEvents
  }
}

extension UIActionEvent {
  public func removingURLPayload() -> UIActionEvent? {
    guard let uiAction = uiAction.removingURLPayload() else { return nil }
    return UIActionEvent(uiAction: uiAction, originalSender: originalSender)
  }
}

extension UserInterfaceAction {
  public func removingURLPayload() -> UserInterfaceAction? {
    guard let payload = payload.removingURLPayload() else { return nil }
    return UserInterfaceAction(
      payload: payload,
      path: path,
      accessibilityElement: accessibilityElement
    )
  }
}

extension UserInterfaceAction.Payload {
  public func removingURLPayload() -> UserInterfaceAction.Payload? {
    switch self {
    case .url:
      return nil
    case .divAction:
      return nil
    case let .composite(lhs, rhs):
      guard let leftPayload = lhs.removingURLPayload() else {
        return rhs.removingURLPayload()
      }
      guard let rightPayload = rhs.removingURLPayload() else {
        return leftPayload
      }
      return .composite(leftPayload, rightPayload)
    case .empty,
         .menu,
         .json:
      return self
    }
  }
}
