// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivActionSetVariableTemplate: TemplateValue {
  public static let type: String = "set_variable"
  public let parent: String?
  public let value: Field<DivTypedValueTemplate>?
  public let variableName: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type"),
        value: try dictionary.getOptionalField("value", templateToType: templateToType),
        variableName: try dictionary.getOptionalExpressionField("variable_name")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-set-variable_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    value: Field<DivTypedValueTemplate>? = nil,
    variableName: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.value = value
    self.variableName = variableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionSetVariableTemplate?) -> DeserializationResult<DivActionSetVariable> {
    let valueValue = parent?.value?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let variableNameValue = parent?.variableName?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let valueNonNil = valueValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetVariable(
      value: valueNonNil,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionSetVariableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSetVariable> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var valueValue: DeserializationResult<DivTypedValue> = .noValue
    var variableNameValue: DeserializationResult<Expression<String>> = parent?.variableName?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "value":
        valueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: valueValue)
      case "variable_name":
        variableNameValue = deserialize(__dictValue).merged(with: variableNameValue)
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self))
      case parent?.variableName?.link:
        variableNameValue = variableNameValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      valueValue = valueValue.merged(with: parent.value?.resolveValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let valueNonNil = valueValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetVariable(
      value: valueNonNil,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionSetVariableTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionSetVariableTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionSetVariableTemplate(
      parent: nil,
      value: value ?? mergedParent.value,
      variableName: variableName ?? mergedParent.variableName
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionSetVariableTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionSetVariableTemplate(
      parent: nil,
      value: try merged.value?.resolveParent(templates: templates),
      variableName: merged.variableName
    )
  }
}
