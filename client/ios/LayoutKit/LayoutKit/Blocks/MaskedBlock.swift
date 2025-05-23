import CoreGraphics
import Foundation
import VGSL

public final class MaskedBlock: SizeForwardingBlock {
  public let maskBlock: Block
  public let maskedBlock: Block
  public let allowsUserInteraction: Bool

  public var sizeProvider: Block { maskBlock }

  public init(
    maskBlock: Block,
    maskedBlock: Block,
    allowsUserInteraction: Bool = false
  ) {
    self.maskBlock = maskBlock
    self.maskedBlock = maskedBlock
    self.allowsUserInteraction = allowsUserInteraction
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? MaskedBlock else { return false }
    return self == other
  }

  public func updated(withStates states: BlocksState) throws -> MaskedBlock {
    let newMaskBlock = try maskBlock.updated(withStates: states)
    let newMaskedBlock = try maskedBlock.updated(withStates: states)
    guard newMaskBlock !== maskBlock || newMaskedBlock !== maskedBlock else {
      return self
    }
    return MaskedBlock(maskBlock: newMaskBlock, maskedBlock: newMaskedBlock)
  }
}

extension MaskedBlock: Equatable {
  public static func ==(lhs: MaskedBlock, rhs: MaskedBlock) -> Bool {
    lhs.maskedBlock == rhs.maskedBlock &&
      lhs.maskBlock == rhs.maskBlock &&
      lhs.allowsUserInteraction == rhs.allowsUserInteraction
  }
}

extension MaskedBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    maskBlock.getImageHolders() + maskedBlock.getImageHolders()
  }
}

extension MaskedBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> MaskedBlock {
    let newMaskBlock = try maskBlock.updated(path: path, isFocused: isFocused)
    let newMaskedBlock = try maskedBlock.updated(path: path, isFocused: isFocused)
    guard newMaskBlock !== maskBlock || newMaskedBlock !== maskedBlock else {
      return self
    }
    return MaskedBlock(
      maskBlock: newMaskBlock,
      maskedBlock: newMaskedBlock,
      allowsUserInteraction: allowsUserInteraction
    )
  }
}

extension MaskedBlock: LayoutCachingDefaultImpl {}
