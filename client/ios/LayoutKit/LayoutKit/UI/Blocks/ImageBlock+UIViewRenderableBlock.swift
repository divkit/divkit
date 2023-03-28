import Foundation
import UIKit

import CommonCorePublic

extension ImageBlock {
  public static func makeBlockView() -> BlockView { RemoteImageViewContainer() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let remoteImageViewContainer = view as! RemoteImageViewContainer
    if metalImageRenderingEnabled && !remoteImageViewContainer.contentView
      .isKind(of: MetalImageView.self) {
      remoteImageViewContainer.contentView = MetalImageView()
    }
    remoteImageViewContainer.contentView.appearanceAnimation = appearanceAnimation?.cast()
    if remoteImageViewContainer.imageHolder !== imageHolder {
      remoteImageViewContainer.imageHolder = imageHolder
    }
    remoteImageViewContainer.contentView.imageContentMode = contentMode
    remoteImageViewContainer.contentView.imageRedrawingStyle = ImageRedrawingStyle(
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects
    )
    remoteImageViewContainer.contentView.isUserInteractionEnabled = false
    remoteImageViewContainer.isUserInteractionEnabled = false
    remoteImageViewContainer.applyAccessibility(accessibilityElement)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is RemoteImageViewContainer
  }
}

extension RemoteImageViewContainer: BlockViewProtocol, VisibleBoundsTrackingLeaf {
  public var effectiveBackgroundColor: UIColor? { nil }
}

extension TransitioningAnimation {
  fileprivate func cast() -> ImageViewAnimation {
    ImageViewAnimation(
      duration: duration.value,
      delay: delay.value,
      startAlpha: start,
      endAlpha: end,
      options: [
        timingFunction.cast(),
        .allowUserInteraction,
        .transitionCrossDissolve,
      ]
    )
  }
}
