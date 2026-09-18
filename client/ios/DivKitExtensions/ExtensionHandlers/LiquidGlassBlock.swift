#if os(iOS)
import CoreGraphics
import Foundation
import LayoutKit
import VGSL

/// Puts a block on the Liquid Glass material.
///
/// Cards use it through the `liquid_glass` extension. It is public for hosts that build LayoutKit
/// blocks directly, without a DivKit card.
@available(iOS 26, *)
public final class LiquidGlassBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public enum EffectStyle: String {
    case regular
    case clear
  }

  public enum CornerStyle: Equatable {
    case capsule(maximumRadius: Double?)
    case corners(topLeft: Double?, topRight: Double?, bottomLeft: Double?, bottomRight: Double?)
  }

  /// Reports the interface style the glass material resolved for the content beneath it.
  public typealias UIStyleUpdater = (UserInterfaceStyle) -> Void

  public let child: Block
  public let effectStyle: EffectStyle
  public let isInteractive: Bool?
  public let tintColor: Color?
  public let cornerStyle: CornerStyle?
  public let uiStyleVariableName: String?
  public let uiStyleUpdater: UIStyleUpdater?

  public init(
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

  public func makeCopy(wrapping block: Block) -> LiquidGlassBlock {
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

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? LiquidGlassBlock else {
      return false
    }
    return self == other
  }
}

@available(iOS 26, *)
extension LiquidGlassBlock: Equatable {
  public static func ==(lhs: LiquidGlassBlock, rhs: LiquidGlassBlock) -> Bool {
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
  public var debugDescription: String {
    "LiquidGlassBlock(style: \(effectStyle), child: \(child))"
  }
}
#endif
