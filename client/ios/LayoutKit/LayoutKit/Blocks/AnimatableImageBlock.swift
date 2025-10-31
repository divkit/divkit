import CoreGraphics
import Foundation
import VGSL

public final class AnimatableImageBlock: ImageBaseBlock {
  public let imageHolder: ImageHolder
  public let widthTrait: LayoutTrait
  public let height: ImageBlockHeight
  public let contentMode: ImageContentMode
  public let accessibilityElement: AccessibilityElement?
  public let path: UIElementPath?
  public let state: ImageBaseBlockState

  public init(
    imageHolder: ImageHolder,
    widthTrait: LayoutTrait,
    height: ImageBlockHeight,
    contentMode: ImageContentMode = .default,
    accessibilityElement: AccessibilityElement? = nil,
    path: UIElementPath? = nil,
    state: ImageBaseBlockState? = nil
  ) {
    self.imageHolder = imageHolder
    self.widthTrait = widthTrait
    self.height = height
    self.contentMode = contentMode
    self.accessibilityElement = accessibilityElement
    self.path = path
    self.state = state ?? ImageBaseBlockState(
      widthTrait: widthTrait,
      height: height,
      imageHolder: imageHolder
    )
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? AnimatableImageBlock else {
      return false
    }

    return self == other
  }
}

public func ==(lhs: AnimatableImageBlock, rhs: AnimatableImageBlock) -> Bool {
  compare(lhs.imageHolder, rhs.imageHolder) &&
    lhs.widthTrait == rhs.widthTrait &&
    lhs.height == rhs.height &&
    lhs.contentMode == rhs.contentMode &&
    lhs.accessibilityElement == rhs.accessibilityElement &&
    lhs.path == rhs.path &&
    lhs.state == rhs.state
}

extension AnimatableImageBlock: LayoutCachingDefaultImpl {}
