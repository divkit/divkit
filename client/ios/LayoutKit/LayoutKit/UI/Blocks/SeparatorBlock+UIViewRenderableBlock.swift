import Foundation
import UIKit

import CommonCorePublic

extension SeparatorBlock {
  public static func makeBlockView() -> BlockView { SeparatorBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    view.backgroundColor = color.systemColor
    view.isUserInteractionEnabled = false
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is SeparatorBlockView
  }
}

private final class SeparatorBlockView: UIView, BlockViewProtocol, VisibleBoundsTrackingLeaf {
  var effectiveBackgroundColor: UIColor? { backgroundColor }
}
