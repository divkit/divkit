// Copyright 2020 Yandex LLC. All rights reserved.

import UIKit

import Base

public final class URLActionHandlingView: UIView, UIActionEventPerforming {
  private let handler: UrlOpener

  public init(handler: @escaping UrlOpener) {
    self.handler = handler
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("")
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    guard case let .url(url) = event.payload else {
      event.sendFrom(self)
      return
    }

    handler(url)
  }
}
