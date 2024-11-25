import CoreGraphics
import Foundation

import LayoutKit
import VGSL

final class ShineBlock: Block {
  let style: ShineStyle
  let effectBeginTime: CFTimeInterval
  let maskImageHolder: ImageHolder

  init(
    style: ShineStyle,
    effectBeginTime: CFTimeInterval,
    maskImageHolder: ImageHolder
  ) {
    self.style = style
    self.effectBeginTime = effectBeginTime
    self.maskImageHolder = maskImageHolder
  }

  func getImageHolders() -> [ImageHolder] {
    [maskImageHolder]
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? ShineBlock else {
      return false
    }

    return self == other
  }
}

extension ShineBlock {
  var intrinsicContentWidth: CGFloat { 0 }
  func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat { 0 }

  var isHorizontallyResizable: Bool { true }
  var isVerticallyResizable: Bool { true }

  var isHorizontallyConstrained: Bool { false }
  var isVerticallyConstrained: Bool { false }

  var widthOfHorizontallyNonResizableBlock: CGFloat { 0.0 }
  func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat { 0 }

  var weightOfHorizontallyResizableBlock: LayoutTrait.Weight { .default }
  var weightOfVerticallyResizableBlock: LayoutTrait.Weight { .default }
}

extension ShineBlock: LayoutCachingDefaultImpl {}
extension ShineBlock: ElementStateUpdatingDefaultImpl {}

extension ShineBlock: CustomDebugStringConvertible {
  var debugDescription: String { "Shine" }
}
