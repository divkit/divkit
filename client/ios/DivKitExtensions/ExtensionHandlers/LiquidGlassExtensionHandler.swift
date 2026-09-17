#if os(iOS)
import DivKit
import Foundation
import LayoutKit
import VGSL

/// Wraps a div into a `UIVisualEffectView` with the Liquid Glass material applied.
///
/// Supported params:
/// * `is_enabled` - turns the effect off without changing the layout. Defaults to `true`.
/// * `style` - `regular` or `clear`. Required.
/// * `is_interactive` - enables the interactive behavior of the glass.
/// * `tint_color` - color applied to the glass.
/// * `corner_style` - `{"type": "capsule", "max_radius": Number}` or
///   `{"type": "corners", "top_left": Number, ...}`.
/// * `ui_style_variable` - name of the variable to write `light`/`dark` into. The glass material
///   resolves the interface style from the content beneath it, so it may differ from the style of
///   the card.
///
/// All params support expressions. The effect is applied on iOS 26 and newer, on older versions the
/// div is rendered as is.
public final class LiquidGlassExtensionHandler: DivExtensionHandler {
  public let id = extensionID

  public init() {}

  public func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard #available(iOS 26, *) else {
      return block
    }
    let extensionParams = getExtensionParams(div)
    let expressionResolver = context.expressionResolver
    do {
      guard try extensionParams.getOptionalBool(
        isEnabledKey,
        expressionResolver: expressionResolver
      ) ?? defaultIsEnabled else {
        return block
      }
      let params = try LiquidGlassExtensionParams(
        params: extensionParams,
        expressionResolver: expressionResolver
      )
      return LiquidGlassBlock(
        child: block,
        effectStyle: params.effectStyle,
        isInteractive: params.isInteractive,
        tintColor: params.tintColor,
        cornerStyle: params.cornerStyle,
        uiStyleVariableName: params.uiStyleVariable,
        uiStyleUpdater: makeUIStyleUpdater(
          variableName: params.uiStyleVariable,
          context: context
        )
      )
    } catch {
      context.addError(message: "Failed to resolve liquid glass extension params: \(error)")
      return block
    }
  }
}

@available(iOS 26, *)
private func makeUIStyleUpdater(
  variableName: String?,
  context: DivBlockModelingContext
) -> LiquidGlassBlock.UIStyleUpdater? {
  guard let variableName, !variableName.isEmpty else {
    return nil
  }
  let name = DivVariableName(rawValue: variableName)
  let path = context.path
  return { [weak variablesStorage = context.variablesStorage] userInterfaceStyle in
    variablesStorage?.update(path: path, name: name, value: userInterfaceStyle.rawValue)
  }
}

@available(iOS 26, *)
struct LiquidGlassExtensionParams {
  let effectStyle: LiquidGlassBlock.EffectStyle
  let isInteractive: Bool?
  let tintColor: Color?
  let cornerStyle: LiquidGlassBlock.CornerStyle?
  let uiStyleVariable: String?

  init(
    params: [String: Any],
    expressionResolver: ExpressionResolver
  ) throws {
    let effectStyle: LiquidGlassBlock.EffectStyle? = (params[styleKey] as? String)
      .flatMap { expressionResolver.resolveEnum($0) }
    guard let effectStyle else {
      throw LiquidGlassExtensionParamsError.invalidStyle
    }
    self.effectStyle = effectStyle

    self.isInteractive = try params.getOptionalBool(
      isInteractiveKey,
      expressionResolver: expressionResolver
    )

    self.tintColor = (params[tintColorKey] as? String)
      .flatMap { expressionResolver.resolveColor($0) }

    self.cornerStyle = try Self.makeCornerStyle(
      params[cornerStyleKey],
      expressionResolver: expressionResolver
    )

    self.uiStyleVariable = (params[uiStyleVariableKey] as? String)
      .flatMap { expressionResolver.resolveString($0) }
  }

  private static func makeCornerStyle(
    _ value: Any?,
    expressionResolver: ExpressionResolver
  ) throws -> LiquidGlassBlock.CornerStyle? {
    guard let params = value as? [String: Any] else {
      return nil
    }
    let type: CornerStyleType? = (params[typeKey] as? String)
      .flatMap { expressionResolver.resolveEnum($0) }
    guard let type else {
      throw LiquidGlassExtensionParamsError.invalidCornerStyle
    }

    switch type {
    case .capsule:
      let maximumRadius = try params.getOptionalDouble(
        maxRadiusKey,
        expressionResolver: expressionResolver
      ) ?? params.getOptionalDouble(
        // The extension shipped in Yandex Browser with a camelCased key, keep it working.
        legacyMaxRadiusKey,
        expressionResolver: expressionResolver
      )
      return .capsule(maximumRadius: maximumRadius)
    case .corners:
      return try .corners(
        topLeft: params.getOptionalDouble(topLeftKey, expressionResolver: expressionResolver),
        topRight: params.getOptionalDouble(topRightKey, expressionResolver: expressionResolver),
        bottomLeft: params.getOptionalDouble(bottomLeftKey, expressionResolver: expressionResolver),
        bottomRight: params.getOptionalDouble(
          bottomRightKey,
          expressionResolver: expressionResolver
        )
      )
    }
  }
}

enum LiquidGlassExtensionParamsError: Error, CustomStringConvertible {
  case invalidStyle
  case invalidCornerStyle

  var description: String {
    switch self {
    case .invalidStyle:
      "'style' must resolve to 'regular' or 'clear'"
    case .invalidCornerStyle:
      "'corner_style.type' must resolve to 'capsule' or 'corners'"
    }
  }
}

private enum CornerStyleType: String {
  case capsule
  case corners
}

extension Dictionary where Key == String {
  fileprivate func getOptionalDouble(
    _ key: Key,
    expressionResolver: ExpressionResolver
  ) throws -> Double? {
    try getOptionalFloat(key, expressionResolver: expressionResolver).map { Double($0) }
  }
}

private let extensionID = "liquid_glass"
private let isEnabledKey = "is_enabled"
private let styleKey = "style"
private let isInteractiveKey = "is_interactive"
private let tintColorKey = "tint_color"
private let cornerStyleKey = "corner_style"
private let uiStyleVariableKey = "ui_style_variable"
private let typeKey = "type"
private let maxRadiusKey = "max_radius"
private let legacyMaxRadiusKey = "maxRadius"
private let topLeftKey = "top_left"
private let topRightKey = "top_right"
private let bottomLeftKey = "bottom_left"
private let bottomRightKey = "bottom_right"
private let defaultIsEnabled = true
#endif
