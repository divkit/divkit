import CoreGraphics
import Foundation

import Base
import CommonCore
import LayoutKit

final class AnimationBlock: BlockWithTraits {
  let animatableView: Lazy<AnimatableView>

  var debugDescription: String {
    return "Animation Block playing animation with view: \(animatableView)"
  }
  let holder: AnimationHolder
  let widthTrait: LayoutTrait
  let heightTrait: LayoutTrait

  init(
    animatableView: Lazy<AnimatableView>,
    animationHolder: AnimationHolder,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) {
    self.animatableView = animatableView
    self.holder = animationHolder
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
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
