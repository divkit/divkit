// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionDictSetValueTemplate: TemplateValue {
  public static let type: String = "dict_set_value"
  public let parent: String?
  public let key: Field<Expression<String>>?
  public let value: Field<DivTypedValueTemplate>?
  public let variableName: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      key: dictionary.getOptionalExpressionField("key"),
      value: dictionary.getOptionalField("value", templateToType: templateToType),
      variableName: dictionary.getOptionalExpressionField("variable_name")
    )
  }

  init(
    parent: String?,
    key: Field<Expression<String>>? = nil,
    value: Field<DivTypedValueTemplate>? = nil,
    variableName: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.key = key
    self.value = value
    self.variableName = variableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionDictSetValueTemplate?) -> DeserializationResult<DivActionDictSetValue> {
    let keyValue = parent?.key?.resolveValue(context: context) ?? .noValue
    let valueValue = parent?.value?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let variableNameValue = parent?.variableName?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      keyValue.errorsOrWarnings?.map { .nestedObjectError(field: "key", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = keyValue {
      errors.append(.requiredFieldIsMissing(field: "key"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let keyNonNil = keyValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionDictSetValue(
      key: keyNonNil,
      value: valueValue.value,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionDictSetValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionDictSetValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var keyValue: DeserializationResult<Expression<String>> = parent?.key?.value() ?? .noValue
    var valueValue: DeserializationResult<DivTypedValue> = .noValue
    var variableNameValue: DeserializationResult<Expression<String>> = parent?.variableName?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "key":
        keyValue = deserialize(__dictValue).merged(with: keyValue)
      case "value":
        valueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: valueValue)
      case "variable_name":
        variableNameValue = deserialize(__dictValue).merged(with: variableNameValue)
      case parent?.key?.link:
        keyValue = keyValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self) })
      case parent?.variableName?.link:
        variableNameValue = variableNameValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    if let parent = parent {
      valueValue = valueValue.merged(with: { parent.value?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      keyValue.errorsOrWarnings?.map { .nestedObjectError(field: "key", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = keyValue {
      errors.append(.requiredFieldIsMissing(field: "key"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let keyNonNil = keyValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionDictSetValue(
      key: keyNonNil,
      value: valueValue.value,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionDictSetValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionDictSetValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionDictSetValueTemplate(
      parent: nil,
      key: key ?? mergedParent.key,
      value: value ?? mergedParent.value,
      variableName: variableName ?? mergedParent.variableName
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionDictSetValueTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionDictSetValueTemplate(
      parent: nil,
      key: merged.key,
      value: merged.value?.tryResolveParent(templates: templates),
      variableName: merged.variableName
    )
  }
}
