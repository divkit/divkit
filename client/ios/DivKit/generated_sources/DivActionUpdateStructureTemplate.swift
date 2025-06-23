// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionUpdateStructureTemplate: TemplateValue, Sendable {
  public static let type: String = "update_structure"
  public let parent: String?
  public let path: Field<Expression<String>>?
  public let value: Field<DivTypedValueTemplate>?
  public let variableName: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      path: dictionary.getOptionalExpressionField("path"),
      value: dictionary.getOptionalField("value", templateToType: templateToType),
      variableName: dictionary.getOptionalExpressionField("variable_name")
    )
  }

  init(
    parent: String?,
    path: Field<Expression<String>>? = nil,
    value: Field<DivTypedValueTemplate>? = nil,
    variableName: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.path = path
    self.value = value
    self.variableName = variableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionUpdateStructureTemplate?) -> DeserializationResult<DivActionUpdateStructure> {
    let pathValue = { parent?.path?.resolveValue(context: context) ?? .noValue }()
    let valueValue = { parent?.value?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let variableNameValue = { parent?.variableName?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      pathValue.errorsOrWarnings?.map { .nestedObjectError(field: "path", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = pathValue {
      errors.append(.requiredFieldIsMissing(field: "path"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let pathNonNil = pathValue.value,
      let valueNonNil = valueValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionUpdateStructure(
      path: { pathNonNil }(),
      value: { valueNonNil }(),
      variableName: { variableNameNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionUpdateStructureTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionUpdateStructure> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var pathValue: DeserializationResult<Expression<String>> = { parent?.path?.value() ?? .noValue }()
    var valueValue: DeserializationResult<DivTypedValue> = .noValue
    var variableNameValue: DeserializationResult<Expression<String>> = { parent?.variableName?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "path" {
           pathValue = deserialize(__dictValue).merged(with: pathValue)
          }
        }()
        _ = {
          if key == "value" {
           valueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: valueValue)
          }
        }()
        _ = {
          if key == "variable_name" {
           variableNameValue = deserialize(__dictValue).merged(with: variableNameValue)
          }
        }()
        _ = {
         if key == parent?.path?.link {
           pathValue = pathValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.value?.link {
           valueValue = valueValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.variableName?.link {
           variableNameValue = variableNameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { valueValue = valueValue.merged(with: { parent.value?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      pathValue.errorsOrWarnings?.map { .nestedObjectError(field: "path", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = pathValue {
      errors.append(.requiredFieldIsMissing(field: "path"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let pathNonNil = pathValue.value,
      let valueNonNil = valueValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionUpdateStructure(
      path: { pathNonNil }(),
      value: { valueNonNil }(),
      variableName: { variableNameNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionUpdateStructureTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionUpdateStructureTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionUpdateStructureTemplate(
      parent: nil,
      path: path ?? mergedParent.path,
      value: value ?? mergedParent.value,
      variableName: variableName ?? mergedParent.variableName
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionUpdateStructureTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionUpdateStructureTemplate(
      parent: nil,
      path: merged.path,
      value: try merged.value?.resolveParent(templates: templates),
      variableName: merged.variableName
    )
  }
}
