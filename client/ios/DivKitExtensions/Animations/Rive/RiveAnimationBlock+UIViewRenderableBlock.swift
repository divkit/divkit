import Foundation

import CommonCore
import LayoutKit

extension RiveAnimationBlock {
  public static func makeBlockView() -> BlockView {
    AnimationBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is AnimationBlockView
  }

  public func configureBlockView(_ view: BlockView, observer: ElementStateObserver?, overscrollDelegate: ScrollDelegate?, renderingDelegate: RenderingDelegate?) {
    if let animationView = view as? AnimationBlockView,
       animationView.animationHolder !== animationHolder {
      animationView.animatableView = animatableView.value
      animationView.animationHolder = animationHolder
    }
  }
}
