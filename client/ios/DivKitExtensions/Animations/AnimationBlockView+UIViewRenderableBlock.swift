import AVFoundation
import UIKit

import CommonCorePublic
import LayoutKit

extension LottieAnimationBlock {
  static func makeBlockView() -> BlockView {
    AnimationBlockView()
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is AnimationBlockView
  }

  func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let lottieView = view as! AnimationBlockView
    if lottieView.animationHolder !== animationHolder {
      lottieView.animatableView = animatableView.value
      lottieView.animationHolder = animationHolder
    }
  }
}
