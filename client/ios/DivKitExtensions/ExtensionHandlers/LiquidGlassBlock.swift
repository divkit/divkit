#if os(iOS)
import CoreGraphics
import Foundation
import LayoutKit
import VGSL

@available(iOS 26, *)
final class LiquidGlassBlock: WrapperBlock, LayoutCachingDefaultImpl {
  /// Reports the interface style the glass material resolved for the content beneath it.
  typealias UIStyleUpdater = (UserInterfaceStyle) -> Void

  enum EffectStyle: String {
    case regular
    case clear
  }

  enum CornerStyle: Equatable {
    case capsule(maximumRadius: Double?)
    case corners(topLeft: Double?, topRight: Double?, bottomLeft: Double?, bottomRight: Double?)
  }

  let child: Block
  let effectStyle: EffectStyle
  let isInteractive: Bool?
  let tintColor: Color?
  let cornerStyle: CornerStyle?
  let uiStyleVariableName: String?
  let uiStyleUpdater: UIStyleUpdater?

  init(
    child: Block,
    effectStyle: EffectStyle,
    isInteractive: Bool? = nil,
    tintColor: Color? = nil,
    cornerStyle: CornerStyle? = nil,
    uiStyleVariableName: String? = nil,
    uiStyleUpdater: UIStyleUpdater? = nil
  ) {
    self.child = child
    self.effectStyle = effectStyle
    self.isInteractive = isInteractive
    self.tintColor = tintColor
    self.cornerStyle = cornerStyle
    self.uiStyleVariableName = uiStyleVariableName
    self.uiStyleUpdater = uiStyleUpdater
  }

  func makeCopy(wrapping block: Block) -> LiquidGlassBlock {
    LiquidGlassBlock(
      child: block,
      effectStyle: effectStyle,
      isInteractive: isInteractive,
      tintColor: tintColor,
      cornerStyle: cornerStyle,
      uiStyleVariableName: uiStyleVariableName,
      uiStyleUpdater: uiStyleUpdater
    )
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? LiquidGlassBlock else {
      return false
    }
    return self == other
  }
}

@available(iOS 26, *)
extension LiquidGlassBlock: Equatable {
  static func ==(lhs: LiquidGlassBlock, rhs: LiquidGlassBlock) -> Bool {
    lhs.child == rhs.child &&
      lhs.effectStyle == rhs.effectStyle &&
      lhs.isInteractive == rhs.isInteractive &&
      lhs.tintColor == rhs.tintColor &&
      lhs.cornerStyle == rhs.cornerStyle &&
      lhs.uiStyleVariableName == rhs.uiStyleVariableName
  }
}

@available(iOS 26, *)
extension LiquidGlassBlock: CustomDebugStringConvertible {
  var debugDescription: String {
    "LiquidGlassBlock(style: \(effectStyle), child: \(child))"
  }
}
#endif
