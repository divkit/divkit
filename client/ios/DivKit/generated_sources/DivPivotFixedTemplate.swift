// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivPivotFixedTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "pivot-fixed"
  public let parent: String? // at least 1 char
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Int>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      unit: try dictionary.getOptionalField("unit"),
      value: try dictionary.getOptionalField("value")
    )
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
    parent: DivPivotFixedTemplate?
  ) -> DeserializationResult<DivPivotFixed> {
    let unitValue = parent?.unit?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.unitValidator
    ) ?? .noValue
    let valueValue = parent?.value?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      unitValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "unit", level: .warning)) },
      valueValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "value", level: .warning)) }
    )
    let result = DivPivotFixed(
      unit: unitValue.value,
      value: valueValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivPivotFixedTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivPivotFixed> {
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
        valueValue = deserialize(__dictValue).merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.unitValidator))
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    let errors = mergeErrors(
      unitValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "unit", level: .warning)) },
      valueValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "value", level: .warning)) }
    )
    let result = DivPivotFixed(
      unit: unitValue.value,
      value: valueValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivPivotFixedTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivPivotFixedTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivPivotFixedTemplate(
      parent: nil,
      unit: unit ?? mergedParent.unit,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: Templates) throws -> DivPivotFixedTemplate {
    try mergedWithParent(templates: templates)
  }
}
