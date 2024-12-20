import Foundation
import LayoutKit
import VGSL

public final class RiveAnimationBlock: BlockWithTraits {
  let animationHolder: AnimationHolder
  let animatableView: Lazy<AnimatableView>
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  public var debugDescription: String {
    "Sized Animation Block is playing animation with \(animationHolder)"
  }

  public init(
    animationHolder: AnimationHolder,
    animatableView: Lazy<AnimatableView>,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) {
    self.animationHolder = animationHolder
    self.animatableView = animatableView
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      value
    case let .intrinsic(_, minSize, _):
      minSize
    case .weighted:
      0
    }
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      value
    case let .intrinsic(_, minSize, _):
      minSize
    case .weighted:
      0
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? RiveAnimationBlock else {
      return false
    }
    return self.widthTrait == other.widthTrait &&
      self.heightTrait == other.heightTrait &&
      self.animationHolder == other.animationHolder
  }

  public func getImageHolders() -> [ImageHolder] { [] }
}

extension RiveAnimationBlock: LayoutCachingDefaultImpl {}
extension RiveAnimationBlock: ElementStateUpdatingDefaultImpl {}
