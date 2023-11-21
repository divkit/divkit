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
    var contentView = remoteImageViewContainer.contentView
    if !effects.isEmpty || tintMode != .sourceIn, !contentView
      .isKind(of: MetalImageView.self) {
      contentView = MetalImageView()
    }
    contentView.appearanceAnimation = appearanceAnimation?.cast()
    contentView.imageContentMode = contentMode
    contentView.imageRedrawingStyle = ImageRedrawingStyle(
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects
    )
    contentView.isUserInteractionEnabled = false
    remoteImageViewContainer.contentView = contentView
    if remoteImageViewContainer.imageHolder !== imageHolder {
      remoteImageViewContainer.imageHolder = imageHolder
    }
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
