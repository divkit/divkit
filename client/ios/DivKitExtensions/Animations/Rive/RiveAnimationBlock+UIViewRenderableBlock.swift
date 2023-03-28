import Foundation

import CommonCorePublic
import LayoutKit

extension RiveAnimationBlock {
  public static func makeBlockView() -> BlockView {
    AnimationBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is AnimationBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    if let animationView = view as? AnimationBlockView,
       animationView.animationHolder !== animationHolder {
      animationView.animatableView = animatableView.value
      animationView.animationHolder = animationHolder
    }
  }
}
