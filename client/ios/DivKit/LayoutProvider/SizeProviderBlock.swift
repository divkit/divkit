import Foundation
import LayoutKit

final class SizeProviderBlock: WrapperBlock, LayoutCachingDefaultImpl {
  typealias ValueUpdater = (Int) -> Void

  let child: Block
  let widthUpdater: ValueUpdater?
  let heightUpdater: ValueUpdater?

  init(
    child: Block,
    widthUpdater: ValueUpdater?,
    heightUpdater: ValueUpdater?
  ) {
    self.child = child
    self.widthUpdater = widthUpdater
    self.heightUpdater = heightUpdater
  }

  func makeCopy(wrapping: Block) -> SizeProviderBlock {
    SizeProviderBlock(
      child: wrapping,
      widthUpdater: widthUpdater,
      heightUpdater: heightUpdater
    )
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? SizeProviderBlock else {
      return false
    }
    return self == other
  }
}

extension SizeProviderBlock {
  var childMarginSize: CGSize {
    guard let child = child as? PaddingProvidingBlock else {
      return .zero
    }

    return CGSize(
      width: child.margins(direction: .horizontal),
      height: child.margins(direction: .vertical)
    )
  }
}

extension SizeProviderBlock: Equatable {
  static func ==(lhs: SizeProviderBlock, rhs: SizeProviderBlock) -> Bool {
    lhs.child == rhs.child
  }
}

extension SizeProviderBlock: CustomDebugStringConvertible {
  var debugDescription: String { "SizeProviderBlock" }
}
