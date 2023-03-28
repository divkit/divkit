import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public final class AnimatableImageBlock: ImageBaseBlock {
  public let imageHolder: ImageHolder
  public let widthTrait: LayoutTrait
  public let height: ImageBlockHeight
  public let contentMode: ImageContentMode
  public let accessibilityElement: AccessibilityElement?

  public init(
    imageHolder: ImageHolder,
    widthTrait: LayoutTrait,
    height: ImageBlockHeight,
    contentMode: ImageContentMode = .default,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.imageHolder = imageHolder
    self.widthTrait = widthTrait
    self.height = height
    self.contentMode = contentMode
    self.accessibilityElement = accessibilityElement
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? AnimatableImageBlock else {
      return false
    }

    return self == other
  }
}

public func ==(lhs: AnimatableImageBlock, rhs: AnimatableImageBlock) -> Bool {
  lhs.imageHolder == rhs.imageHolder &&
    lhs.widthTrait == rhs.widthTrait &&
    lhs.height == rhs.height &&
    lhs.contentMode == rhs.contentMode &&
    lhs.accessibilityElement == rhs.accessibilityElement
}

extension AnimatableImageBlock: LayoutCachingDefaultImpl {}
extension AnimatableImageBlock: ElementStateUpdatingDefaultImpl {}
