import CoreGraphics
import DivKit
import Foundation
import LayoutKit
import VGSL

final class LottieAnimationBlock: SizeForwardingBlock {
  let animatableView: Lazy<AsyncSourceAnimatableView>
  let animationHolder: AnimationHolder
  let sizeProvider: Block
  let scale: DivImageScale
  var isPlaying: Bool

  var debugDescription: String {
    "Animation Block playing animation with view: \(animatableView)"
  }

  init(
    animatableView: Lazy<AsyncSourceAnimatableView>,
    animationHolder: AnimationHolder,
    sizeProvider: Block,
    scale: DivImageScale,
    isPlaying: Bool
  ) {
    self.animatableView = animatableView
    self.animationHolder = animationHolder
    self.sizeProvider = sizeProvider
    self.scale = scale
    self.isPlaying = isPlaying
  }

  func getImageHolders() -> [ImageHolder] { [] }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? LottieAnimationBlock else {
      return false
    }

    return self.sizeProvider == other.sizeProvider
      && self.animationHolder == other.animationHolder
      && self.scale == other.scale
      && self.isPlaying == other.isPlaying
  }
}

extension LottieAnimationBlock: LayoutCachingDefaultImpl {}
extension LottieAnimationBlock: ElementStateUpdatingDefaultImpl {}
