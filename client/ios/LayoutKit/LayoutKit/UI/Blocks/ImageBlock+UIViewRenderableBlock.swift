import Foundation
import UIKit

import CommonCore

extension ImageBlock {
  public static func makeBlockView() -> BlockView { RemoteImageView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let remoteImageView = view as! RemoteImageView
    remoteImageView.appearanceAnimation = appearanceAnimation?.cast()
    if remoteImageView.imageHolder !== imageHolder {
      remoteImageView.imageHolder = imageHolder
    }
    remoteImageView.imageContentMode = contentMode
    remoteImageView.imageRedrawingColor = tintColor
    remoteImageView.isUserInteractionEnabled = false
    remoteImageView.applyAccessibility(accessibilityElement)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is RemoteImageView
  }
}

extension RemoteImageView: BlockViewProtocol, VisibleBoundsTrackingLeaf {
  public var effectiveBackgroundColor: UIColor? { nil }
}

extension TransitioningAnimation {
  fileprivate func cast() -> RemoteImageView.Animation {
    RemoteImageView.Animation(
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
