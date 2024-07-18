// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionArraySetValueTemplate: TemplateValue {
  public static let type: String = "array_set_value"
  public let parent: String?
  public let index: Field<Expression<Int>>?
  public let value: Field<DivTypedValueTemplate>?
  public let variableName: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      index: dictionary.getOptionalExpressionField("index"),
      value: dictionary.getOptionalField("value", templateToType: templateToType),
      variableName: dictionary.getOptionalExpressionField("variable_name")
    )
  }

  init(
    parent: String?,
    index: Field<Expression<Int>>? = nil,
    value: Field<DivTypedValueTemplate>? = nil,
    variableName: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.index = index
    self.value = value
    self.variableName = variableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionArraySetValueTemplate?) -> DeserializationResult<DivActionArraySetValue> {
    let indexValue = parent?.index?.resolveValue(context: context) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let variableNameValue = parent?.variableName?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      indexValue.errorsOrWarnings?.map { .nestedObjectError(field: "index", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = indexValue {
      errors.append(.requiredFieldIsMissing(field: "index"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let indexNonNil = indexValue.value,
      let valueNonNil = valueValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionArraySetValue(
      index: indexNonNil,
      value: valueNonNil,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionArraySetValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionArraySetValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var indexValue: DeserializationResult<Expression<Int>> = parent?.index?.value() ?? .noValue
    var valueValue: DeserializationResult<DivTypedValue> = .noValue
    var variableNameValue: DeserializationResult<Expression<String>> = parent?.variableName?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "index":
        indexValue = deserialize(__dictValue).merged(with: indexValue)
      case "value":
        valueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: valueValue)
      case "variable_name":
        variableNameValue = deserialize(__dictValue).merged(with: variableNameValue)
      case parent?.index?.link:
        indexValue = indexValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self) })
      case parent?.variableName?.link:
        variableNameValue = variableNameValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    if let parent = parent {
      valueValue = valueValue.merged(with: { parent.value?.resolveValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      indexValue.errorsOrWarnings?.map { .nestedObjectError(field: "index", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = indexValue {
      errors.append(.requiredFieldIsMissing(field: "index"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let indexNonNil = indexValue.value,
      let valueNonNil = valueValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionArraySetValue(
      index: indexNonNil,
      value: valueNonNil,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionArraySetValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionArraySetValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionArraySetValueTemplate(
      parent: nil,
      index: index ?? mergedParent.index,
      value: value ?? mergedParent.value,
      variableName: variableName ?? mergedParent.variableName
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionArraySetValueTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionArraySetValueTemplate(
      parent: nil,
      index: merged.index,
      value: try merged.value?.resolveParent(templates: templates),
      variableName: merged.variableName
    )
  }
}
