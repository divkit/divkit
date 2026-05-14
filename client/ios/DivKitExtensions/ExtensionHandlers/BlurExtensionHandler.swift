import DivKit
import Foundation
import LayoutKit

public final class BlurExtensionHandler: DivExtensionHandler {
  public let id = extensionID

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    block.addingDecorations(blurEffect: div.resolveBlurEffect(context.expressionResolver))
  }
}

extension DivBase {
  fileprivate func resolveBlurEffect(
    _ expressionResolver: ExpressionResolver
  ) -> BlurEffect? {
    guard
      let blurThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let styleExpression = blurThemeExtension.params?[styleKey] as? String
    else {
      return nil
    }

    if let blur: InternalBlurEffect = expressionResolver.resolveEnum(styleExpression) {
      return BlurEffect(internalBlurEffect: blur)
    }

    return nil
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
private let extensionID = "blur"
