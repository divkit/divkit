// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTextRangeBorderTemplate: TemplateValue {
  public let cornerRadius: Field<Expression<Int>>? // constraint: number >= 0
  public let stroke: Field<DivStrokeTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      cornerRadius: try dictionary.getOptionalExpressionField("corner_radius"),
      stroke: try dictionary.getOptionalField("stroke", templateToType: templateToType)
    )
  }

  init(
    cornerRadius: Field<Expression<Int>>? = nil,
    stroke: Field<DivStrokeTemplate>? = nil
  ) {
    self.cornerRadius = cornerRadius
    self.stroke = stroke
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextRangeBorderTemplate?) -> DeserializationResult<DivTextRangeBorder> {
    let cornerRadiusValue = parent?.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator) ?? .noValue
    let strokeValue = parent?.stroke?.resolveOptionalValue(context: context, validator: ResolvedValue.strokeValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivTextRangeBorder(
      cornerRadius: cornerRadiusValue.value,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeBorderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeBorder> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var cornerRadiusValue: DeserializationResult<Expression<Int>> = parent?.cornerRadius?.value() ?? .noValue
    var strokeValue: DeserializationResult<DivStroke> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "corner_radius":
        cornerRadiusValue = deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator).merged(with: cornerRadiusValue)
      case "stroke":
        strokeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.strokeValidator, type: DivStrokeTemplate.self).merged(with: strokeValue)
      case parent?.cornerRadius?.link:
        cornerRadiusValue = cornerRadiusValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator))
      case parent?.stroke?.link:
        strokeValue = strokeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.strokeValidator, type: DivStrokeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      strokeValue = strokeValue.merged(with: parent.stroke?.resolveOptionalValue(context: context, validator: ResolvedValue.strokeValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivTextRangeBorder(
      cornerRadius: cornerRadiusValue.value,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTextRangeBorderTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextRangeBorderTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTextRangeBorderTemplate(
      cornerRadius: merged.cornerRadius,
      stroke: merged.stroke?.tryResolveParent(templates: templates)
    )
  }
}
