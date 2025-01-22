import LayoutKit
import UIKit
import VGSL

extension ShineBlock {
  static func makeBlockView() -> BlockView { ShineView() }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is ShineView
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! ShineView).configure(
      child: child,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      params: params,
      maskImageHolder: maskImageHolder
    )
  }
}
