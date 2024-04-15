import AVFoundation
import UIKit

import CommonCorePublic
import DivKit
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
    if lottieView.animationHolder != animationHolder {
      lottieView.animatableView = animatableView.value
      lottieView.animationContentMode = scale.contentMode
      lottieView.animationHolder = animationHolder
    }
  }
}

extension DivImageScale {
  fileprivate var contentMode: UIView.ContentMode {
    switch self {
    case .fit: return .scaleAspectFit
    case .fill: return .scaleAspectFill
    case .noScale: return .center
    case .stretch: return .scaleToFill
    }
  }
}
