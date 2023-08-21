import DivKit
import LayoutKit

typealias ValueUpdater = (Int) -> Void

final class SizeProviderBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public let child: Block
  let widthUpdater: ValueUpdater?
  let heightUpdater: ValueUpdater?

  public init(
    child: Block,
    widthUpdater: ValueUpdater?,
    heightUpdater: ValueUpdater?
  ) {
    self.child = child
    self.widthUpdater = widthUpdater
    self.heightUpdater = heightUpdater
  }

  public func makeCopy(wrapping: Block) -> SizeProviderBlock {
    return SizeProviderBlock(
      child: wrapping,
      widthUpdater: widthUpdater,
      heightUpdater: heightUpdater
    )
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? SizeProviderBlock else {
      return false
    }
    return self == other
  }
}

extension SizeProviderBlock: Equatable {
  public static func ==(lhs: SizeProviderBlock, rhs: SizeProviderBlock) -> Bool {
    lhs.child == rhs.child
  }
}

extension SizeProviderBlock: CustomDebugStringConvertible {
  public var debugDescription: String { "SizeProviderBlock" }
}
