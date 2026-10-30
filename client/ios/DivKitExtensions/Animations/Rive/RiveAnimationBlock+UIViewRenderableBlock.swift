#if os(iOS)
import Foundation
import LayoutKit
import VGSL

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
    guard let animationView = view as? AnimationBlockView else {
      return
    }
    if animationView.animationHolder != animationHolder {
      animationView.animatableView = animatableView.value
      animationView.animationHolder = animationHolder
    }
    animationView.playbackState = nil
    animationView.isPlaying = true
  }
}
#endif
