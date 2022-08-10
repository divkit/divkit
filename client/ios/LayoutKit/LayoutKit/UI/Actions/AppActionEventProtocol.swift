import UIKit

public protocol AppActionEventProtocol {
  typealias Handler = (Self, AnyObject) -> Void
  func makeHandler(responder: UIResponder) -> Handler?
}

extension AppActionEventProtocol {
  public func sendFrom(_ sender: UIResponder) {
    var nextResponder = sender.next
    while nextResponder != nil {
      if let handler = makeHandler(responder: nextResponder!) {
        handler(self, sender)
        return
      }
      nextResponder = nextResponder?.next
    }
  }
}
