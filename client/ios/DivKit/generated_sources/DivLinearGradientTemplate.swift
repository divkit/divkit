// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLinearGradientTemplate: TemplateValue, Sendable {
  public final class ColorPointTemplate: TemplateValue, Sendable {
    public let color: Field<Expression<Color>>?
    public let position: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
        position: dictionary.getOptionalExpressionField("position")
      )
    }

    init(
      color: Field<Expression<Color>>? = nil,
      position: Field<Expression<Double>>? = nil
    ) {
      self.color = color
      self.position = position
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ColorPointTemplate?) -> DeserializationResult<DivLinearGradient.ColorPoint> {
      let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
      let positionValue = { parent?.position?.resolveValue(context: context, validator: ResolvedValue.positionValidator) ?? .noValue }()
      var errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
      )
      if case .noValue = colorValue {
        errors.append(.requiredFieldIsMissing(field: "color"))
      }
      if case .noValue = positionValue {
        errors.append(.requiredFieldIsMissing(field: "position"))
      }
      guard
        let colorNonNil = colorValue.value,
        let positionNonNil = positionValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivLinearGradient.ColorPoint(
        color: { colorNonNil }(),
        position: { positionNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ColorPointTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivLinearGradient.ColorPoint> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
      var positionValue: DeserializationResult<Expression<Double>> = { parent?.position?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "color" {
             colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
            }
          }()
          _ = {
            if key == "position" {
             positionValue = deserialize(__dictValue, validator: ResolvedValue.positionValidator).merged(with: positionValue)
            }
          }()
          _ = {
           if key == parent?.color?.link {
             colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
            }
          }()
          _ = {
           if key == parent?.position?.link {
             positionValue = positionValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.positionValidator) })
            }
          }()
        }
      }()
      var errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
      )
      if case .noValue = colorValue {
        errors.append(.requiredFieldIsMissing(field: "color"))
      }
      if case .noValue = positionValue {
        errors.append(.requiredFieldIsMissing(field: "position"))
      }
      guard
        let colorNonNil = colorValue.value,
        let positionNonNil = positionValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivLinearGradient.ColorPoint(
        color: { colorNonNil }(),
        position: { positionNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ColorPointTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ColorPointTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "gradient"
  public let parent: String?
  public let angle: Field<Expression<Int>>? // constraint: number >= 0 && number <= 360; default value: 0
  public let colorMap: Field<[ColorPointTemplate]>? // at least 2 elements
  public let colors: Field<[Expression<Color>]>? // at least 2 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      angle: dictionary.getOptionalExpressionField("angle"),
      colorMap: dictionary.getOptionalArray("color_map", templateToType: templateToType),
      colors: dictionary.getOptionalExpressionArray("colors", transform: Color.color(withHexString:))
    )
  }

  init(
    parent: String?,
    angle: Field<Expression<Int>>? = nil,
    colorMap: Field<[ColorPointTemplate]>? = nil,
    colors: Field<[Expression<Color>]>? = nil
  ) {
    self.parent = parent
    self.angle = angle
    self.colorMap = colorMap
    self.colors = colors
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivLinearGradientTemplate?) -> DeserializationResult<DivLinearGradient> {
    let angleValue = { parent?.angle?.resolveOptionalValue(context: context, validator: ResolvedValue.angleValidator) ?? .noValue }()
    let colorMapValue = { parent?.colorMap?.resolveOptionalValue(context: context, validator: ResolvedValue.colorMapValidator, useOnlyLinks: true) ?? .noValue }()
    let colorsValue = { parent?.colors?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) ?? .noValue }()
    let errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .nestedObjectError(field: "angle", error: $0) },
      colorMapValue.errorsOrWarnings?.map { .nestedObjectError(field: "color_map", error: $0) },
      colorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "colors", error: $0) }
    )
    let result = DivLinearGradient(
      angle: { angleValue.value }(),
      colorMap: { colorMapValue.value }(),
      colors: { colorsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivLinearGradientTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivLinearGradient> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var angleValue: DeserializationResult<Expression<Int>> = { parent?.angle?.value() ?? .noValue }()
    var colorMapValue: DeserializationResult<[DivLinearGradient.ColorPoint]> = .noValue
    var colorsValue: DeserializationResult<[Expression<Color>]> = { parent?.colors?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "angle" {
           angleValue = deserialize(__dictValue, validator: ResolvedValue.angleValidator).merged(with: angleValue)
          }
        }()
        _ = {
          if key == "color_map" {
           colorMapValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.colorMapValidator, type: DivLinearGradientTemplate.ColorPointTemplate.self).merged(with: colorMapValue)
          }
        }()
        _ = {
          if key == "colors" {
           colorsValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator).merged(with: colorsValue)
          }
        }()
        _ = {
         if key == parent?.angle?.link {
           angleValue = angleValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.angleValidator) })
          }
        }()
        _ = {
         if key == parent?.colorMap?.link {
           colorMapValue = colorMapValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.colorMapValidator, type: DivLinearGradientTemplate.ColorPointTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.colors?.link {
           colorsValue = colorsValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { colorMapValue = colorMapValue.merged(with: { parent.colorMap?.resolveOptionalValue(context: context, validator: ResolvedValue.colorMapValidator, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .nestedObjectError(field: "angle", error: $0) },
      colorMapValue.errorsOrWarnings?.map { .nestedObjectError(field: "color_map", error: $0) },
      colorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "colors", error: $0) }
    )
    let result = DivLinearGradient(
      angle: { angleValue.value }(),
      colorMap: { colorMapValue.value }(),
      colors: { colorsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivLinearGradientTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivLinearGradientTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivLinearGradientTemplate(
      parent: nil,
      angle: angle ?? mergedParent.angle,
      colorMap: colorMap ?? mergedParent.colorMap,
      colors: colors ?? mergedParent.colors
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivLinearGradientTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivLinearGradientTemplate(
      parent: nil,
      angle: merged.angle,
      colorMap: merged.colorMap?.tryResolveParent(templates: templates),
      colors: merged.colors
    )
  }
}
