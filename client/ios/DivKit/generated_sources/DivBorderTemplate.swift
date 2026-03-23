// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivBorderTemplate: TemplateValue, Sendable {
  public let cornerRadius: Field<Expression<Int>>? // constraint: number >= 0
  public let cornersRadius: Field<DivCornersRadiusTemplate>?
  public let hasShadow: Field<Expression<Bool>>? // default value: false
  public let shadow: Field<DivShadowTemplate>?
  public let stroke: Field<DivStrokeTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      cornerRadius: dictionary.getOptionalExpressionField("corner_radius"),
      cornersRadius: dictionary.getOptionalField("corners_radius", templateToType: templateToType),
      hasShadow: dictionary.getOptionalExpressionField("has_shadow"),
      shadow: dictionary.getOptionalField("shadow", templateToType: templateToType),
      stroke: dictionary.getOptionalField("stroke", templateToType: templateToType)
    )
  }

  init(
    cornerRadius: Field<Expression<Int>>? = nil,
    cornersRadius: Field<DivCornersRadiusTemplate>? = nil,
    hasShadow: Field<Expression<Bool>>? = nil,
    shadow: Field<DivShadowTemplate>? = nil,
    stroke: Field<DivStrokeTemplate>? = nil
  ) {
    self.cornerRadius = cornerRadius
    self.cornersRadius = cornersRadius
    self.hasShadow = hasShadow
    self.shadow = shadow
    self.stroke = stroke
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivBorderTemplate?) -> DeserializationResult<DivBorder> {
    let cornerRadiusValue = { parent?.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator) ?? .noValue }()
    let cornersRadiusValue = { parent?.cornersRadius?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hasShadowValue = { parent?.hasShadow?.resolveOptionalValue(context: context) ?? .noValue }()
    let shadowValue = { parent?.shadow?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let strokeValue = { parent?.stroke?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      cornersRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corners_radius", error: $0) },
      hasShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "has_shadow", error: $0) },
      shadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "shadow", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivBorder(
      cornerRadius: { cornerRadiusValue.value }(),
      cornersRadius: { cornersRadiusValue.value }(),
      hasShadow: { hasShadowValue.value }(),
      shadow: { shadowValue.value }(),
      stroke: { strokeValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivBorderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivBorder> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var cornerRadiusValue: DeserializationResult<Expression<Int>> = { parent?.cornerRadius?.value() ?? .noValue }()
    var cornersRadiusValue: DeserializationResult<DivCornersRadius> = .noValue
    var hasShadowValue: DeserializationResult<Expression<Bool>> = { parent?.hasShadow?.value() ?? .noValue }()
    var shadowValue: DeserializationResult<DivShadow> = .noValue
    var strokeValue: DeserializationResult<DivStroke> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "corner_radius" {
           cornerRadiusValue = deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator).merged(with: cornerRadiusValue)
          }
        }()
        _ = {
          if key == "corners_radius" {
           cornersRadiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCornersRadiusTemplate.self).merged(with: cornersRadiusValue)
          }
        }()
        _ = {
          if key == "has_shadow" {
           hasShadowValue = deserialize(__dictValue).merged(with: hasShadowValue)
          }
        }()
        _ = {
          if key == "shadow" {
           shadowValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self).merged(with: shadowValue)
          }
        }()
        _ = {
          if key == "stroke" {
           strokeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivStrokeTemplate.self).merged(with: strokeValue)
          }
        }()
        _ = {
         if key == parent?.cornerRadius?.link {
           cornerRadiusValue = cornerRadiusValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator) })
          }
        }()
        _ = {
         if key == parent?.cornersRadius?.link {
           cornersRadiusValue = cornersRadiusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCornersRadiusTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.hasShadow?.link {
           hasShadowValue = hasShadowValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.shadow?.link {
           shadowValue = shadowValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.stroke?.link {
           strokeValue = strokeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivStrokeTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { cornersRadiusValue = cornersRadiusValue.merged(with: { parent.cornersRadius?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { shadowValue = shadowValue.merged(with: { parent.shadow?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { strokeValue = strokeValue.merged(with: { parent.stroke?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      cornersRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corners_radius", error: $0) },
      hasShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "has_shadow", error: $0) },
      shadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "shadow", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivBorder(
      cornerRadius: { cornerRadiusValue.value }(),
      cornersRadius: { cornersRadiusValue.value }(),
      hasShadow: { hasShadowValue.value }(),
      shadow: { shadowValue.value }(),
      stroke: { strokeValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivBorderTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivBorderTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivBorderTemplate(
      cornerRadius: merged.cornerRadius,
      cornersRadius: merged.cornersRadius?.tryResolveParent(templates: templates),
      hasShadow: merged.hasShadow,
      shadow: merged.shadow?.tryResolveParent(templates: templates),
      stroke: merged.stroke?.tryResolveParent(templates: templates)
    )
  }
}
