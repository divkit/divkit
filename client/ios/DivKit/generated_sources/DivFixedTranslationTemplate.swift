// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFixedTranslationTemplate: TemplateValue, Sendable {
  public static let type: String = "translation-fixed"
  public let parent: String?
  public let unit: Field<Expression<DivSizeUnit>>? // default value: dp
  public let value: Field<Expression<Int>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      unit: dictionary.getOptionalExpressionField("unit"),
      value: dictionary.getOptionalExpressionField("value")
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFixedTranslationTemplate?) -> DeserializationResult<DivFixedTranslation> {
    let unitValue = parent?.unit?.resolveOptionalValue(context: context) ?? .noValue
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
    let result = DivFixedTranslation(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFixedTranslationTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFixedTranslation> {
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
        valueValue = deserialize(__dictValue).merged(with: valueValue)
      case parent?.unit?.link:
        unitValue = unitValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue) })
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
    let result = DivFixedTranslation(
      unit: unitValue.value,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFixedTranslationTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivFixedTranslationTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivFixedTranslationTemplate(
      parent: nil,
      unit: unit ?? mergedParent.unit,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFixedTranslationTemplate {
    return try mergedWithParent(templates: templates)
  }
}
