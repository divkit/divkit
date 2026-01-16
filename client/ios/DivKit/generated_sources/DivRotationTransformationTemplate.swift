// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRotationTransformationTemplate: TemplateValue, Sendable {
  public static let type: String = "rotation"
  public let parent: String?
  public let angle: Field<Expression<Double>>?
  public let pivotX: Field<DivPivotTemplate>? // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let pivotY: Field<DivPivotTemplate>? // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      angle: dictionary.getOptionalExpressionField("angle"),
      pivotX: dictionary.getOptionalField("pivot_x", templateToType: templateToType),
      pivotY: dictionary.getOptionalField("pivot_y", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    angle: Field<Expression<Double>>? = nil,
    pivotX: Field<DivPivotTemplate>? = nil,
    pivotY: Field<DivPivotTemplate>? = nil
  ) {
    self.parent = parent
    self.angle = angle
    self.pivotX = pivotX
    self.pivotY = pivotY
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivRotationTransformationTemplate?) -> DeserializationResult<DivRotationTransformation> {
    let angleValue = parent?.angle?.resolveValue(context: context) ?? .noValue
    let pivotXValue = parent?.pivotX?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let pivotYValue = parent?.pivotY?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .nestedObjectError(field: "angle", error: $0) },
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) }
    )
    if case .noValue = angleValue {
      errors.append(.requiredFieldIsMissing(field: "angle"))
    }
    guard
      let angleNonNil = angleValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivRotationTransformation(
      angle: angleNonNil,
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRotationTransformationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRotationTransformation> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var angleValue: DeserializationResult<Expression<Double>> = parent?.angle?.value() ?? .noValue
    var pivotXValue: DeserializationResult<DivPivot> = .noValue
    var pivotYValue: DeserializationResult<DivPivot> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "angle":
        angleValue = deserialize(__dictValue).merged(with: angleValue)
      case "pivot_x":
        pivotXValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self).merged(with: pivotXValue)
      case "pivot_y":
        pivotYValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self).merged(with: pivotYValue)
      case parent?.angle?.link:
        angleValue = angleValue.merged(with: { deserialize(__dictValue) })
      case parent?.pivotX?.link:
        pivotXValue = pivotXValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self) })
      case parent?.pivotY?.link:
        pivotYValue = pivotYValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPivotTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      _ = pivotXValue = pivotXValue.merged(with: { parent.pivotX?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      _ = pivotYValue = pivotYValue.merged(with: { parent.pivotY?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .nestedObjectError(field: "angle", error: $0) },
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) }
    )
    if case .noValue = angleValue {
      errors.append(.requiredFieldIsMissing(field: "angle"))
    }
    guard
      let angleNonNil = angleValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivRotationTransformation(
      angle: angleNonNil,
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivRotationTransformationTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivRotationTransformationTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivRotationTransformationTemplate(
      parent: nil,
      angle: angle ?? mergedParent.angle,
      pivotX: pivotX ?? mergedParent.pivotX,
      pivotY: pivotY ?? mergedParent.pivotY
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivRotationTransformationTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivRotationTransformationTemplate(
      parent: nil,
      angle: merged.angle,
      pivotX: merged.pivotX?.tryResolveParent(templates: templates),
      pivotY: merged.pivotY?.tryResolveParent(templates: templates)
    )
  }
}
