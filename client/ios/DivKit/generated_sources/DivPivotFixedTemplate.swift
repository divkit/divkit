// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPivotFixedTemplate: TemplateValue {
  public static let type: String = "pivot-fixed"
  public let parent: String? // at least 1 char
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Int>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      unit: try dictionary.getOptionalExpressionField("unit"),
      value: try dictionary.getOptionalExpressionField("value")
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPivotFixedTemplate?) -> DeserializationResult<DivPivotFixed> {
    let unitValue = parent?.unit?.resolveOptionalValue(context: context, validator: ResolvedValue.unitValidator) ?? .noValue
    let valueValue = parent?.value?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    let result = DivPivotFixed(
      unit: unitValue.value,
      value: valueValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPivotFixedTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPivotFixed> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var unitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.unit?.value() ?? .noValue
    var valueValue: DeserializationResult<Expression<Int>> = parent?.value?.value() ?? .noValue
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
    let errors = mergeErrors(
      unitValue.errorsOrWarnings?.map { .nestedObjectError(field: "unit", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    let result = DivPivotFixed(
      unit: unitValue.value,
      value: valueValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPivotFixedTemplate {
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPivotFixedTemplate {
    return try mergedWithParent(templates: templates)
  }
}
