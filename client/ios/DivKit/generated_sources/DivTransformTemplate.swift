// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTransformTemplate: TemplateValue {
  public let pivotX: Field<DivPivotTemplate>? // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let pivotY: Field<DivPivotTemplate>? // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let rotation: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      pivotX: dictionary.getOptionalField("pivot_x", templateToType: templateToType),
      pivotY: dictionary.getOptionalField("pivot_y", templateToType: templateToType),
      rotation: dictionary.getOptionalExpressionField("rotation")
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
    let pivotXValue = { parent?.pivotX?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let pivotYValue = { parent?.pivotY?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let rotationValue = { parent?.rotation?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      rotationValue.errorsOrWarnings?.map { .nestedObjectError(field: "rotation", error: $0) }
    )
    let result = DivTransform(
      pivotX: { pivotXValue.value }(),
      pivotY: { pivotYValue.value }(),
      rotation: { rotationValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTransformTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTransform> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var pivotXValue: DeserializationResult<DivPivot> = .noValue
    var pivotYValue: DeserializationResult<DivPivot> = .noValue
    var rotationValue: DeserializationResult<Expression<Double>> = { parent?.rotation?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "pivot_x" {
           pivotXValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self).merged(with: pivotXValue)
          }
        }()
        _ = {
          if key == "pivot_y" {
           pivotYValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self).merged(with: pivotYValue)
          }
        }()
        _ = {
          if key == "rotation" {
           rotationValue = deserialize(__dictValue).merged(with: rotationValue)
          }
        }()
        _ = {
         if key == parent?.pivotX?.link {
           pivotXValue = pivotXValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.pivotY?.link {
           pivotYValue = pivotYValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.rotation?.link {
           rotationValue = rotationValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { pivotXValue = pivotXValue.merged(with: { parent.pivotX?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { pivotYValue = pivotYValue.merged(with: { parent.pivotY?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      rotationValue.errorsOrWarnings?.map { .nestedObjectError(field: "rotation", error: $0) }
    )
    let result = DivTransform(
      pivotX: { pivotXValue.value }(),
      pivotY: { pivotYValue.value }(),
      rotation: { rotationValue.value }()
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
