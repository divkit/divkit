import Foundation
import UIKit

import CommonCorePublic

extension EmptyBlock {
  public static func makeBlockView() -> BlockView { EmptyBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    view.isUserInteractionEnabled = false
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is EmptyBlockView
  }
}

private final class EmptyBlockView: UIView, BlockViewProtocol, VisibleBoundsTrackingLeaf {
  var effectiveBackgroundColor: UIColor? { backgroundColor }
}
