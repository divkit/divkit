import UIKit

import LayoutKit
import VGSL

extension ShineBlock {
  static func makeBlockView() -> BlockView { ShineView() }

  func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let shineView = view as! ShineView
    shineView.configureShine(
      style: style,
      effectBeginTime: effectBeginTime,
      maskImageHolder: maskImageHolder
    )
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is ShineView
  }
}

extension ShineView: BlockViewProtocol {
  var effectiveBackgroundColor: UIColor? { nil }
}

extension ShineView: VisibleBoundsTrackingLeaf {}
