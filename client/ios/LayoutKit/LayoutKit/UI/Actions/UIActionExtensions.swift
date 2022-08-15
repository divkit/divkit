#if os(iOS)
import UIKit
#endif

import LayoutKitInterface

extension UserInterfaceAction {
  #if os(iOS)
  public typealias ResponderType = UIResponder
  #else
  public typealias ResponderType = AnyObject
  #endif

  public func perform(sendingFrom sender: ResponderType) {
    #if os(iOS)
    AnalyticsTouchEvent(touchType: .click, path: path).sendFrom(sender)
    UIActionEvent(uiAction: self, originalSender: sender).sendFrom(sender)
    #endif
  }

  public func performLongPress(sendingFrom sender: ResponderType) {
    #if os(iOS)
    AnalyticsTouchEvent(touchType: .longPress, path: path).sendFrom(sender)
    UIActionEvent(uiAction: self, originalSender: sender).sendFrom(sender)
    #endif
  }
}

extension Array where Element == UserInterfaceAction {
  public func perform(sendingFrom sender: UserInterfaceAction.ResponderType) {
    #if os(iOS)
    forEach {
      AnalyticsTouchEvent(touchType: .click, path: $0.path)
        .sendFrom(sender)
    }

    map { UIActionEvent(uiAction: $0, originalSender: sender) }
      .sendFrom(sender)
    #endif
  }
}
