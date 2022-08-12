// Copyright 2018 Yandex LLC. All rights reserved.

import UIKit

import CommonCore

extension LaidOutBlock where T: BlockWithLayout {
  static func makeBlockView() -> BlockView {
    T.makeBlockView()
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    block.configureBlockView(
      view,
      with: layout,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    block.canConfigureBlockView(view)
  }
}
