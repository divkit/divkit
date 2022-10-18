import CoreGraphics
import Foundation

import Base
import CommonCore
import LayoutKit

final class AnimationBlock: SizeForwardingBlock {
  let animatableView: Lazy<AnimatableView>

  var sizeProvider: Block
  var debugDescription: String {
    return "Animation Block playing animation with view: \(animatableView)"
  }
  let holder: AnimationHolder

  init(
    animatableView: Lazy<AnimatableView>,
    animationHolder: AnimationHolder,
    sizeProvider: Block
  ) {
    self.animatableView = animatableView
    self.holder = animationHolder
    self.sizeProvider = sizeProvider
  }

  let intrinsicContentWidth: CGFloat = 0
  func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat { 0 }

  func getImageHolders() -> [ImageHolder] { [] }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? AnimationBlock else {
      return false
    }

    return self == other
  }
}

extension AnimationBlock: LayoutCachingDefaultImpl {}
extension AnimationBlock: ElementStateUpdatingDefaultImpl {}
