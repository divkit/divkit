import UIKit

import BasePublic

public final class URLActionHandlingView: UIView, UIActionEventPerforming {
  private let handler: UrlOpener

  public init(handler: @escaping UrlOpener) {
    self.handler = handler
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    guard case let .url(url) = event.payload else {
      event.sendFrom(self)
      return
    }

    handler(url)
  }
}
