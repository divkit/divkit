import Foundation
import UIKit

import CommonCore

extension ImageBlock {
  public static func makeBlockView() -> BlockView { RemoteImageViewContainer(contentView: RemoteImageView()) }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let remoteImageViewContainer = view as! RemoteImageViewContainer
    remoteImageViewContainer.contentView.appearanceAnimation = appearanceAnimation?.cast()
    if remoteImageViewContainer.imageHolder !== imageHolder {
      remoteImageViewContainer.imageHolder = imageHolder
    }
    remoteImageViewContainer.contentView.imageContentMode = contentMode
    remoteImageViewContainer.contentView.imageRedrawingColor = tintColor
    remoteImageViewContainer.contentView.isUserInteractionEnabled = false
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
