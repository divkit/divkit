// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeBorderTemplate: TemplateValue, Sendable {
  public let cornerRadius: Field<Expression<Int>>? // constraint: number >= 0
  public let stroke: Field<DivStrokeTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      cornerRadius: dictionary.getOptionalExpressionField("corner_radius"),
      stroke: dictionary.getOptionalField("stroke", templateToType: templateToType)
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
    let cornerRadiusValue = { parent?.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator) ?? .noValue }()
    let strokeValue = { parent?.stroke?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivTextRangeBorder(
      cornerRadius: { cornerRadiusValue.value }(),
      stroke: { strokeValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextRangeBorderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTextRangeBorder> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var cornerRadiusValue: DeserializationResult<Expression<Int>> = { parent?.cornerRadius?.value() ?? .noValue }()
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
         if key == parent?.stroke?.link {
           strokeValue = strokeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivStrokeTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { strokeValue = strokeValue.merged(with: { parent.stroke?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivTextRangeBorder(
      cornerRadius: { cornerRadiusValue.value }(),
      stroke: { strokeValue.value }()
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
