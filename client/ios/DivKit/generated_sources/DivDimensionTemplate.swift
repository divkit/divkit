// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDimensionTemplate: TemplateValue {
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      unit: dictionary.getOptionalExpressionField("unit"),
      value: dictionary.getOptionalExpressionField("value")
    )
  }

  init(
    unit: Field<Expression<DivSizeUnit>>? = nil,
    value: Field<Expression<Double>>? = nil
  ) {
    self.unit = unit
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivDimensionTemplate?) -> DeserializationResult<DivDimension> {
    let unitValue = { parent?.unit?.resolveOptionalValue(context: context) ?? .noValue }()
    let valueValue = { parent?.value?.resolveValue(context: context) ?? .noValue }()
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
      unit: { unitValue.value }(),
      value: { valueNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDimensionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDimension> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = { parent?.unit?.value() ?? .noValue }()
    var valueValue: DeserializationResult<Expression<Double>> = { parent?.value?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "unit" {
           unitValue = deserialize(__dictValue).merged(with: unitValue)
          }
        }()
        _ = {
          if key == "value" {
           valueValue = deserialize(__dictValue).merged(with: valueValue)
          }
        }()
        _ = {
         if key == parent?.unit?.link {
           unitValue = unitValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.value?.link {
           valueValue = valueValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
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
      unit: { unitValue.value }(),
      value: { valueNonNil }()
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
