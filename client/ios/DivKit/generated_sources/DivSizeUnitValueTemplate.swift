// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSizeUnitValueTemplate: TemplateValue, Sendable {
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Int>>? // constraint: number >= 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      unit: dictionary.getOptionalExpressionField("unit"),
      value: dictionary.getOptionalExpressionField("value")
    )
  }

  init(
    unit: Field<Expression<DivSizeUnit>>? = nil,
    value: Field<Expression<Int>>? = nil
  ) {
    self.unit = unit
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivSizeUnitValueTemplate?) -> DeserializationResult<DivSizeUnitValue> {
    let unitValue = parent?.unit?.resolveOptionalValue(context: context) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context, validator: ResolvedValue.valueValidator) ?? .noValue
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
    let result = DivSizeUnitValue(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivSizeUnitValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSizeUnitValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    var valueValue: DeserializationResult<Expression<Int>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "unit":
        unitValue = deserialize(__dictValue).merged(with: unitValue)
      case "value":
        valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator).merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.valueValidator) })
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
    let result = DivSizeUnitValue(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivSizeUnitValueTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivSizeUnitValueTemplate {
    return try mergedWithParent(templates: templates)
  }
}
