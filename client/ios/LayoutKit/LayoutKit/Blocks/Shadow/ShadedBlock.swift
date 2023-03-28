import CoreGraphics

import CommonCorePublic

public final class ShadedBlock {
  public let block: Block
  public let shadow: BlockShadow

  public init(
    block: Block,
    shadow: BlockShadow
  ) {
    self.block = block
    self.shadow = shadow
  }
}

extension ShadedBlock: WrapperBlock {
  public var child: Block {
    block
  }

  public func makeCopy(wrapping block: Block) -> ShadedBlock {
    ShadedBlock(block: block, shadow: shadow)
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? ShadedBlock else {
      return false
    }

    return self == other
  }
}

extension ShadedBlock: Equatable {
  public static func ==(lhs: ShadedBlock, rhs: ShadedBlock) -> Bool {
    lhs.block == rhs.block &&
      lhs.shadow == rhs.shadow
  }
}

extension Block {
  public func shaded(with shadow: BlockShadow?) -> Block {
    guard let shadow = shadow else { return self }
    return ShadedBlock(block: self, shadow: shadow)
  }

  public func shaded(with cornerRadius: CGFloat) -> Block {
    shaded(with: BlockShadow(cornerRadius: cornerRadius))
  }
}

extension ShadedBlock: LayoutCachingDefaultImpl {}
