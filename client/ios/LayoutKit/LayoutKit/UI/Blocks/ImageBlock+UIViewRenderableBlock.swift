import Foundation
import UIKit
import VGSL

extension ImageBlock {
  public static func makeBlockView() -> BlockView { RemoteImageViewContainer() }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
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
    contentView.filter = filter
    contentView.isUserInteractionEnabled = false
    remoteImageViewContainer.contentView = contentView
    if compare(remoteImageViewContainer.imageHolder, imageHolder) == false {
      remoteImageViewContainer.setImageHolder(imageHolder) { [weak self, weak observer] in
        self?.updateStateIfNeeded(observer: observer)
      }
    }
    remoteImageViewContainer.isUserInteractionEnabled = false
    remoteImageViewContainer.applyAccessibilityFromScratch(accessibilityElement)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is RemoteImageViewContainer
  }

  private func suitableTypeOfImageView() -> any RemoteImageViewContentProtocol.Type {
    let blurEffectUsingMetal = blurUsingMetal ?? true
    let tintEffectUsingMetal = tintUsingMetal ?? true
    let (hasBlur, hasTint) = usedEffects()
    let useMetal = (blurEffectUsingMetal && hasBlur || tintEffectUsingMetal && hasTint)
    return useMetal ? MetalImageView.self : RemoteImageView.self
  }

  private func usedEffects() -> (blur: Bool, tint: Bool) {
    var hasBlur = false
    var hasTint = tintMode != .sourceIn && tintColor != nil
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

extension RemoteImageViewContainer: LayoutKit.BlockViewProtocol {
  public var effectiveBackgroundColor: UIColor? { nil }
}

extension RemoteImageViewContainer: VGSLUI.VisibleBoundsTrackingLeaf {}

extension TransitioningAnimation {
  fileprivate func cast() -> ImageViewAnimation {
    ImageViewAnimation(
      duration: duration,
      delay: delay,
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
