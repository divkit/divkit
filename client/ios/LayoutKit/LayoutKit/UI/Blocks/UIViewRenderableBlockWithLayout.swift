#if os(iOS)
import UIKit
import VGSL

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
#endif
