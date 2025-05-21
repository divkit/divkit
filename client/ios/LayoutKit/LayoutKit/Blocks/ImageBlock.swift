import CoreGraphics
import Foundation
import VGSL

public final class ImageBlock: ImageBaseBlock {
  public let imageHolder: ImageHolder
  public let widthTrait: LayoutTrait
  public let height: ImageBlockHeight
  public let contentMode: ImageContentMode
  public let tintColor: Color?
  public let tintMode: TintMode
  public let effects: [ImageEffect]
  public let filter: AnyEquatableImageFilter?
  public let accessibilityElement: AccessibilityElement?
  public let appearanceAnimation: TransitioningAnimation?
  public let blurUsingMetal: Bool?
  public let tintUsingMetal: Bool?
  public let path: UIElementPath?
  public let state: ImageBaseBlockState

  public init(
    imageHolder: ImageHolder,
    widthTrait: LayoutTrait,
    height: ImageBlockHeight,
    contentMode: ImageContentMode,
    tintColor: Color?,
    tintMode: TintMode,
    effects: [ImageEffect] = [],
    filter: AnyEquatableImageFilter? = nil,
    accessibilityElement: AccessibilityElement? = nil,
    appearanceAnimation: TransitioningAnimation? = nil,
    blurUsingMetal: Bool? = nil,
    tintUsingMetal: Bool? = nil,
    path: UIElementPath? = nil
  ) {
    self.imageHolder = imageHolder
    self.widthTrait = widthTrait
    self.height = height
    self.contentMode = contentMode
    self.tintColor = tintColor
    self.tintMode = tintMode
    self.effects = effects
    self.filter = filter
    self.accessibilityElement = accessibilityElement
    self.appearanceAnimation = appearanceAnimation
    self.blurUsingMetal = blurUsingMetal
    self.tintUsingMetal = tintUsingMetal
    self.path = path
    self.state = ImageBaseBlockState(
      widthTrait: widthTrait,
      height: height,
      imageHolder: imageHolder
    )
  }

  public convenience init(
    imageHolder: ImageHolder,
    widthTrait: LayoutTrait = .intrinsic,
    heightTrait: LayoutTrait = .intrinsic,
    contentMode: ImageContentMode = .default,
    tintColor: Color? = nil,
    tintMode: TintMode = .sourceIn,
    effects: [ImageEffect] = [],
    filter: AnyEquatableImageFilter? = nil,
    accessibilityElement: AccessibilityElement? = nil,
    appearanceAnimation: TransitioningAnimation? = nil,
    blurUsingMetal: Bool? = nil,
    tintUsingMetal: Bool? = nil,
    path: UIElementPath? = nil
  ) {
    self.init(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: .trait(heightTrait),
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      filter: filter,
      accessibilityElement: accessibilityElement,
      appearanceAnimation: appearanceAnimation,
      blurUsingMetal: blurUsingMetal,
      tintUsingMetal: tintUsingMetal,
      path: path
    )
  }

  public convenience init(
    imageHolder: ImageHolder,
    size: CGSize,
    contentMode: ImageContentMode = .default,
    tintColor: Color? = nil,
    tintMode: TintMode = .sourceIn,
    effects: [ImageEffect] = [],
    filter: AnyEquatableImageFilter? = nil,
    accessibilityElement: AccessibilityElement? = nil,
    appearanceAnimation: TransitioningAnimation? = nil,
    blurUsingMetal: Bool? = nil,
    tintUsingMetal: Bool? = nil,
    path: UIElementPath? = nil
  ) {
    self.init(
      imageHolder: imageHolder,
      widthTrait: .fixed(size.width),
      heightTrait: .fixed(size.height),
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      filter: filter,
      accessibilityElement: accessibilityElement,
      appearanceAnimation: appearanceAnimation,
      blurUsingMetal: blurUsingMetal,
      tintUsingMetal: tintUsingMetal,
      path: path
    )
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? ImageBlock else {
      return false
    }

    return self == other
  }
}

public func ==(lhs: ImageBlock, rhs: ImageBlock) -> Bool {
  compare(lhs.imageHolder, rhs.imageHolder) &&
    lhs.widthTrait == rhs.widthTrait &&
    lhs.height == rhs.height &&
    lhs.contentMode == rhs.contentMode &&
    lhs.tintColor == rhs.tintColor &&
    lhs.accessibilityElement == rhs.accessibilityElement &&
    lhs.appearanceAnimation == rhs.appearanceAnimation &&
    lhs.filter == rhs.filter &&
    lhs.path == rhs.path &&
    lhs.state == rhs.state
}

extension ImageBlock: LayoutCachingDefaultImpl {}
