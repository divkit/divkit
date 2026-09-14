import DivKit
import Foundation
import LayoutKit
import VGSL

public final class BlurExtensionHandler: DivExtensionHandler {
  public let id = extensionID

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    let params: BlurExtensionParams
    do {
      guard let resolvedParams = try div.resolveBlurExtensionParams(
        context.expressionResolver
      ) else {
        return block
      }
      params = resolvedParams
    } catch {
      context.addError(message: "Failed to resolve blur extension params: \(error)")
      return block
    }

    return block.addingDecorations(
      blurEffect: params.blurEffect,
      blurIntensity: params.intensity
    )
  }
}

extension DivBase {
  fileprivate func resolveBlurExtensionParams(
    _ expressionResolver: ExpressionResolver
  ) throws -> BlurExtensionParams? {
    guard
      let blurExtension = extensions?.first(where: { $0.id == extensionID })
    else {
      return nil
    }
    return try BlurExtensionParams(
      params: blurExtension.params ?? [:],
      expressionResolver: expressionResolver
    )
  }
}

struct BlurExtensionParams {
  let blurEffect: BlurEffect
  let intensity: CGFloat

  init?(
    params: [String: Any],
    expressionResolver: ExpressionResolver
  ) throws {
    let hasStyle = params[styleKey] != nil
    let hasIntensity = params[intensityKey] != nil
    guard hasStyle != hasIntensity else {
      throw BlurExtensionParamsError.exactlyOneParameterRequired
    }

    if hasIntensity {
      guard
        let intensity = try params.getOptionalFloat(
          intensityKey,
          expressionResolver: expressionResolver
        ),
        intensity.isFinite
      else {
        throw BlurExtensionParamsError.invalidIntensity
      }

      blurEffect = .regular
      self.intensity = clamp(intensity, min: 0, max: 1)
      return
    }

    guard
      let styleExpression = params[styleKey] as? String,
      let internalBlurEffect: InternalBlurEffect = expressionResolver.resolveEnum(styleExpression)
    else {
      throw BlurExtensionParamsError.invalidStyle
    }
    guard let blurEffect = BlurEffect(internalBlurEffect: internalBlurEffect) else {
      return nil
    }

    self.blurEffect = blurEffect
    intensity = defaultIntensity
  }
}

enum BlurExtensionParamsError: Error, CustomStringConvertible {
  case exactlyOneParameterRequired
  case invalidIntensity
  case invalidStyle

  var description: String {
    switch self {
    case .exactlyOneParameterRequired:
      "Exactly one of 'style' or 'intensity' must be specified"
    case .invalidIntensity:
      "'intensity' must resolve to a finite number"
    case .invalidStyle:
      "'style' must resolve to a supported blur style"
    }
  }
}

private enum InternalBlurEffect: String {
  case extraLight = "extra_light"
  case light
  case dark
  case regular
  case prominent
  case systemUltraThinMaterial = "system_ultra_thin_material"
  case systemThinMaterial = "system_thin_material"
  case systemMaterial = "system_material"
  case systemThickMaterial = "system_thick_material"
  case systemChromeMaterial = "system_chrome_material"
  case systemUltraThinMaterialLight = "system_ultra_thin_material_light"
  case systemThinMaterialLight = "system_thin_material_light"
  case systemMaterialLight = "system_material_light"
  case systemThickMaterialLight = "system_thick_material_light"
  case systemChromeMaterialLight = "system_chrome_material_light"
  case systemUltraThinMaterialDark = "system_ultra_thin_material_dark"
  case systemThinMaterialDark = "system_thin_material_dark"
  case systemMaterialDark = "system_material_dark"
  case systemThickMaterialDark = "system_thick_material_dark"
  case systemChromeMaterialDark = "system_chrome_material_dark"
  case disabled
}

extension BlurEffect {
  fileprivate init?(internalBlurEffect: InternalBlurEffect) {
    switch internalBlurEffect {
    case .extraLight:
      self = .extraLight
    case .light:
      self = .light
    case .dark:
      self = .dark
    case .regular:
      self = .regular
    case .prominent:
      self = .prominent
    case .systemUltraThinMaterial:
      self = .systemUltraThinMaterial
    case .systemThinMaterial:
      self = .systemThinMaterial
    case .systemMaterial:
      self = .systemMaterial
    case .systemThickMaterial:
      self = .systemThickMaterial
    case .systemChromeMaterial:
      self = .systemChromeMaterial
    case .systemUltraThinMaterialLight:
      self = .systemUltraThinMaterialLight
    case .systemThinMaterialLight:
      self = .systemThinMaterialLight
    case .systemMaterialLight:
      self = .systemMaterialLight
    case .systemThickMaterialLight:
      self = .systemThickMaterialLight
    case .systemChromeMaterialLight:
      self = .systemChromeMaterialLight
    case .systemUltraThinMaterialDark:
      self = .systemUltraThinMaterialDark
    case .systemThinMaterialDark:
      self = .systemThinMaterialDark
    case .systemMaterialDark:
      self = .systemMaterialDark
    case .systemThickMaterialDark:
      self = .systemThickMaterialDark
    case .systemChromeMaterialDark:
      self = .systemChromeMaterialDark
    case .disabled:
      return nil
    }
  }
}

private let styleKey = "style"
private let intensityKey = "intensity"
private let defaultIntensity: CGFloat = 1
private let extensionID = "blur"
