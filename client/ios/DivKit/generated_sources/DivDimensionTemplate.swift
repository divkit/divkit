// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivDimensionTemplate: TemplateValue {
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        unit: try dictionary.getOptionalExpressionField("unit"),
        value: try dictionary.getOptionalExpressionField("value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-dimension_template." + field, representation: representation)
    }
  }

  init(
    unit: Field<Expression<DivSizeUnit>>? = nil,
    value: Field<Expression<Double>>? = nil
  ) {
    self.unit = unit
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivDimensionTemplate?) -> DeserializationResult<DivDimension> {
    let unitValue = parent?.unit?.resolveOptionalValue(context: context, validator: ResolvedValue.unitValidator) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivDimension(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDimensionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDimension> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    var valueValue: DeserializationResult<Expression<Double>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "unit":
        unitValue = deserialize(__dictValue, validator: ResolvedValue.unitValidator).merged(with: unitValue)
      case "value":
        valueValue = deserialize(__dictValue).merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    var errors = mergeErrors(
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivDimension(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivDimensionTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivDimensionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
