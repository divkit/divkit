// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivFixedSizeTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "fixed"
  public let parent: String? // at least 1 char
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Int>>? // constraint: number >= 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        unit: try dictionary.getOptionalField("unit"),
        value: try dictionary.getOptionalField("value")
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-fixed-size_template." + field,
        representation: representation
      )
    }
  }

  init(
    parent: String?,
    unit: Field<Expression<DivSizeUnit>>? = nil,
    value: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.unit = unit
    self.value = value
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivFixedSizeTemplate?
  ) -> DeserializationResult<DivFixedSize> {
    let unitValue = parent?.unit?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.unitValidator
    ) ?? .noValue
    let valueValue = parent?.value?.resolveValue(
      context: context,
      validator: ResolvedValue.valueValidator
    ) ?? .noValue
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
    let result = DivFixedSize(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivFixedSizeTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivFixedSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?
      .value() ?? .noValue
    var valueValue: DeserializationResult<Expression<Int>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "unit":
        unitValue = deserialize(__dictValue, validator: ResolvedValue.unitValidator)
          .merged(with: unitValue)
      case "value":
        valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator)
          .merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
      case parent?.value?.link:
        valueValue = valueValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.valueValidator))
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
    let result = DivFixedSize(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivFixedSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivFixedSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivFixedSizeTemplate(
      parent: nil,
      unit: unit ?? mergedParent.unit,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: Templates) throws -> DivFixedSizeTemplate {
    try mergedWithParent(templates: templates)
  }
}
