import CoreGraphics
import Foundation

import BasePublic
import CommonCorePublic
import LayoutKit

final class LottieAnimationBlock: SizeForwardingBlock {
  let animatableView: Lazy<AnimatableView>
  let animationHolder: AnimationHolder
  let sizeProvider: Block

  var debugDescription: String {
    "Animation Block playing animation with view: \(animatableView)"
  }

  init(
    animatableView: Lazy<AnimatableView>,
    animationHolder: AnimationHolder,
    sizeProvider: Block
  ) {
    self.animatableView = animatableView
    self.animationHolder = animationHolder
    self.sizeProvider = sizeProvider
  }

  let intrinsicContentWidth: CGFloat = 0
  func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat { 0 }

  func getImageHolders() -> [ImageHolder] { [] }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? LottieAnimationBlock else {
      return false
    }

    return self.sizeProvider == other.sizeProvider
      && self.animationHolder == other.animationHolder
  }
}

extension LottieAnimationBlock: LayoutCachingDefaultImpl {}
extension LottieAnimationBlock: ElementStateUpdatingDefaultImpl {}
