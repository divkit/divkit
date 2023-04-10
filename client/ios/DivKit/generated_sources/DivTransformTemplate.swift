// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTransformTemplate: TemplateValue {
  public let pivotX: Field<DivPivotTemplate>? // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let pivotY: Field<DivPivotTemplate>? // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let rotation: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      pivotX: try dictionary.getOptionalField("pivot_x", templateToType: templateToType),
      pivotY: try dictionary.getOptionalField("pivot_y", templateToType: templateToType),
      rotation: try dictionary.getOptionalExpressionField("rotation")
    )
  }

  init(
    pivotX: Field<DivPivotTemplate>? = nil,
    pivotY: Field<DivPivotTemplate>? = nil,
    rotation: Field<Expression<Double>>? = nil
  ) {
    self.pivotX = pivotX
    self.pivotY = pivotY
    self.rotation = rotation
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTransformTemplate?) -> DeserializationResult<DivTransform> {
    let pivotXValue = parent?.pivotX?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotXValidator, useOnlyLinks: true) ?? .noValue
    let pivotYValue = parent?.pivotY?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotYValidator, useOnlyLinks: true) ?? .noValue
    let rotationValue = parent?.rotation?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      rotationValue.errorsOrWarnings?.map { .nestedObjectError(field: "rotation", error: $0) }
    )
    let result = DivTransform(
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value,
      rotation: rotationValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTransformTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTransform> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var pivotXValue: DeserializationResult<DivPivot> = .noValue
    var pivotYValue: DeserializationResult<DivPivot> = .noValue
    var rotationValue: DeserializationResult<Expression<Double>> = parent?.rotation?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "pivot_x":
        pivotXValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.pivotXValidator, type: DivPivotTemplate.self).merged(with: pivotXValue)
      case "pivot_y":
        pivotYValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.pivotYValidator, type: DivPivotTemplate.self).merged(with: pivotYValue)
      case "rotation":
        rotationValue = deserialize(__dictValue).merged(with: rotationValue)
      case parent?.pivotX?.link:
        pivotXValue = pivotXValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.pivotXValidator, type: DivPivotTemplate.self))
      case parent?.pivotY?.link:
        pivotYValue = pivotYValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.pivotYValidator, type: DivPivotTemplate.self))
      case parent?.rotation?.link:
        rotationValue = rotationValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      pivotXValue = pivotXValue.merged(with: parent.pivotX?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotXValidator, useOnlyLinks: true))
      pivotYValue = pivotYValue.merged(with: parent.pivotY?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotYValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      rotationValue.errorsOrWarnings?.map { .nestedObjectError(field: "rotation", error: $0) }
    )
    let result = DivTransform(
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value,
      rotation: rotationValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTransformTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTransformTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTransformTemplate(
      pivotX: merged.pivotX?.tryResolveParent(templates: templates),
      pivotY: merged.pivotY?.tryResolveParent(templates: templates),
      rotation: merged.rotation
    )
  }
}
