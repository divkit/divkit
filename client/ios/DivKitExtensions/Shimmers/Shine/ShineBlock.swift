import CoreGraphics
import Foundation

import LayoutKit
import VGSL

final class ShineBlock: WrapperBlock, LayoutCachingDefaultImpl {
  let child: Block
  let params: ShineExtensionParams
  let maskImageHolder: ImageHolder

  init(
    child: Block,
    params: ShineExtensionParams,
    maskImageHolder: ImageHolder
  ) {
    self.child = child
    self.params = params
    self.maskImageHolder = maskImageHolder
  }

  func getImageHolders() -> [ImageHolder] {
    [maskImageHolder]
  }

  func makeCopy(wrapping block: Block) -> ShineBlock {
    ShineBlock(
      child: block,
      params: params,
      maskImageHolder: maskImageHolder
    )
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? ShineBlock else {
      return false
    }

    return self == other
  }
}

extension ShineBlock: Equatable {
  static func ==(lhs: ShineBlock, rhs: ShineBlock) -> Bool {
    lhs.child == rhs.child &&
      lhs.params == rhs.params &&
      compare(lhs.maskImageHolder, rhs.maskImageHolder)
  }
}

extension ShineBlock: CustomDebugStringConvertible {
  var debugDescription: String {
    "ShineBlock child: \(child), " +
      "parameters: \(params)"
  }
}
