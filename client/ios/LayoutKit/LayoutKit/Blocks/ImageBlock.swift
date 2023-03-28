import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public final class ImageBlock: ImageBaseBlock {
  public let imageHolder: ImageHolder
  public let widthTrait: LayoutTrait
  public let height: ImageBlockHeight
  public let contentMode: ImageContentMode
  public let tintColor: Color?
  public let tintMode: TintMode?
  public let effects: [ImageEffect]
  public let accessibilityElement: AccessibilityElement?
  public let appearanceAnimation: TransitioningAnimation?
  public let metalImageRenderingEnabled: Bool

  public init(
    imageHolder: ImageHolder,
    widthTrait: LayoutTrait,
    height: ImageBlockHeight,
    contentMode: ImageContentMode,
    tintColor: Color?,
    tintMode: TintMode?,
    effects: [ImageEffect] = [],
    metalImageRenderingEnabled: Bool = false,
    accessibilityElement: AccessibilityElement? = nil,
    appearanceAnimation: TransitioningAnimation? = nil
  ) {
    self.imageHolder = imageHolder
    self.widthTrait = widthTrait
    self.height = height
    self.contentMode = contentMode
    self.tintColor = tintColor
    self.tintMode = tintMode
    self.effects = effects
    self.metalImageRenderingEnabled = metalImageRenderingEnabled
    self.accessibilityElement = accessibilityElement
    self.appearanceAnimation = appearanceAnimation
  }

  public convenience init(
    imageHolder: ImageHolder,
    widthTrait: LayoutTrait = .intrinsic,
    heightTrait: LayoutTrait = .intrinsic,
    contentMode: ImageContentMode = .default,
    tintColor: Color? = nil,
    tintMode: TintMode? = nil,
    effects: [ImageEffect] = [],
    metalImageRenderingEnabled: Bool = false,
    accessibilityElement: AccessibilityElement? = nil,
    appearanceAnimation: TransitioningAnimation? = nil
  ) {
    self.init(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: .trait(heightTrait),
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      metalImageRenderingEnabled: metalImageRenderingEnabled,
      accessibilityElement: accessibilityElement,
      appearanceAnimation: appearanceAnimation
    )
  }

  public convenience init(
    imageHolder: ImageHolder,
    size: CGSize,
    contentMode: ImageContentMode = .default,
    tintColor: Color? = nil,
    tintMode: TintMode? = nil,
    effects: [ImageEffect] = [],
    metalImageRenderingEnabled: Bool = false,
    accessibilityElement: AccessibilityElement? = nil,
    appearanceAnimation: TransitioningAnimation? = nil
  ) {
    self.init(
      imageHolder: imageHolder,
      widthTrait: .fixed(size.width),
      heightTrait: .fixed(size.height),
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      metalImageRenderingEnabled: metalImageRenderingEnabled,
      accessibilityElement: accessibilityElement,
      appearanceAnimation: appearanceAnimation
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
  lhs.imageHolder == rhs.imageHolder &&
    lhs.widthTrait == rhs.widthTrait &&
    lhs.height == rhs.height &&
    lhs.contentMode == rhs.contentMode &&
    lhs.tintColor == rhs.tintColor &&
    lhs.accessibilityElement == rhs.accessibilityElement &&
    lhs.appearanceAnimation == rhs.appearanceAnimation
}

extension ImageBlock: LayoutCachingDefaultImpl {}
extension ImageBlock: ElementStateUpdatingDefaultImpl {}
