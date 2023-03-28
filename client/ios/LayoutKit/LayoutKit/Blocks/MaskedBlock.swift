import CoreGraphics
import Foundation

import CommonCorePublic

public final class MaskedBlock: SizeForwardingBlock {
  public let maskBlock: Block
  public let maskedBlock: Block

  public var sizeProvider: Block { maskBlock }

  public init(
    maskBlock: Block,
    maskedBlock: Block
  ) {
    self.maskBlock = maskBlock
    self.maskedBlock = maskedBlock
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
      lhs.maskBlock == rhs.maskBlock
  }
}

extension MaskedBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    maskBlock.getImageHolders() + maskedBlock.getImageHolders()
  }
}

extension MaskedBlock: LayoutCachingDefaultImpl {}
