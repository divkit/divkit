// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivDimensionTemplate: TemplateValue, TemplateDeserializable {
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Double>>?

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    do {
      self.init(
        unit: try dictionary.getOptionalField("unit"),
        value: try dictionary.getOptionalField("value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-dimension_template." + field,
        representation: representation
      )
    }
  }

  init(
    unit: Field<Expression<DivSizeUnit>>? = nil,
    value: Field<Expression<Double>>? = nil
  ) {
    self.unit = unit
    self.value = value
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivDimensionTemplate?
  ) -> DeserializationResult<DivDimension> {
    let unitValue = parent?.unit?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.unitValidator
    ) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      unitValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "unit", level: .warning)) },
      valueValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "value", level: .error)) }
    )
    if case .noValue = valueValue {
      errors
        .append(.right(FieldError(
          fieldName: "value",
          level: .error,
          error: .requiredFieldIsMissing
        )))
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
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivDimensionTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivDimension> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?
      .value() ?? .noValue
    var valueValue: DeserializationResult<Expression<Double>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "unit":
        unitValue = deserialize(__dictValue, validator: ResolvedValue.unitValidator)
          .merged(with: unitValue)
      case "value":
        valueValue = deserialize(__dictValue).merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    var errors = mergeErrors(
      unitValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "unit", level: .warning)) },
      valueValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "value", level: .error)) }
    )
    if case .noValue = valueValue {
      errors
        .append(.right(FieldError(
          fieldName: "value",
          level: .error,
          error: .requiredFieldIsMissing
        )))
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
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates _: Templates) throws -> DivDimensionTemplate {
    self
  }

  public func resolveParent(templates: Templates) throws -> DivDimensionTemplate {
    try mergedWithParent(templates: templates)
  }
}
