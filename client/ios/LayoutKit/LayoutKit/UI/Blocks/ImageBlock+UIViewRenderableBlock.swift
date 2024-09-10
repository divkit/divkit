import Foundation
import UIKit

import VGSL

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
    let targetType = suitableTypeOfImageView()
    if !contentView.isKind(of: targetType) {
      contentView = targetType.init()
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

  private func suitableTypeOfImageView() -> any RemoteImageViewContentProtocol.Type {
    if tintMode != .sourceIn {
      return MetalImageView.self
    }
    let blurEffectUsingMetal = blurUsingMetal ?? true
    let tintEffectUsingMetal = tintUsingMetal ?? true
    let (hasBlur, hasTint) = usedEffects()
    let useMetal = (blurEffectUsingMetal && hasBlur || tintEffectUsingMetal && hasTint)
    return useMetal ? MetalImageView.self : RemoteImageView.self
  }

  private func usedEffects() -> (blur: Bool, tint: Bool) {
    var hasBlur = false
    var hasTint = false
    for effect in effects {
      switch effect {
      case .blur:
        hasBlur = true
      case .tint:
        hasTint = true  
      }
    }
    return (blur: hasBlur, tint: hasTint)
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
