#if os(iOS)
import CoreGraphics
import Foundation
import LayoutKit
import VGSL

final class RasterizeBlock: WrapperBlock, LayoutCachingDefaultImpl {
  let child: Block

  init(child: Block) {
    self.child = child
  }

  func makeCopy(wrapping block: Block) -> RasterizeBlock {
    RasterizeBlock(child: block)
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? RasterizeBlock else {
      return false
    }
    return child == other.child
  }
}

extension RasterizeBlock: CustomDebugStringConvertible {
  var debugDescription: String {
    "RasterizeBlock child: \(child)"
  }
}
#endif
