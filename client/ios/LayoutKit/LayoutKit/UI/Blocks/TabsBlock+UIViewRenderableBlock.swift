import Foundation
import UIKit

import CommonCorePublic

extension TabsBlock {
  public static func makeBlockView() -> BlockView {
    TabbedPagesView(frame: .zero)
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let tabView = view as! TabbedPagesView
    tabView.configure(
      model: model,
      state: state,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TabbedPagesView
  }
}
