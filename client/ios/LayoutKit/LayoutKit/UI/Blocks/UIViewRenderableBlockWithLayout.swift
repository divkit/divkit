// Copyright 2018 Yandex LLC. All rights reserved.

import UIKit

import CommonCore

extension BlockWithLayout {
  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    configureBlockView(
      view,
      with: nil,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}
