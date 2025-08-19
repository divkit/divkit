// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class PropertyVariableTemplate: TemplateValue, Sendable {
  public static let type: String = "property"
  public let parent: String?
  public let get: Field<Expression<String>>?
  public let name: Field<String>?
  public let newValueVariableName: Field<String>? // default value: new_value
  public let set: Field<[DivActionTemplate]>?
  public let valueType: Field<Expression<DivEvaluableType>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      get: dictionary.getOptionalExpressionField("get"),
      name: dictionary.getOptionalField("name"),
      newValueVariableName: dictionary.getOptionalField("new_value_variable_name"),
      set: dictionary.getOptionalArray("set", templateToType: templateToType),
      valueType: dictionary.getOptionalExpressionField("value_type")
    )
  }

  init(
    parent: String?,
    get: Field<Expression<String>>? = nil,
    name: Field<String>? = nil,
    newValueVariableName: Field<String>? = nil,
    set: Field<[DivActionTemplate]>? = nil,
    valueType: Field<Expression<DivEvaluableType>>? = nil
  ) {
    self.parent = parent
    self.get = get
    self.name = name
    self.newValueVariableName = newValueVariableName
    self.set = set
    self.valueType = valueType
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: PropertyVariableTemplate?) -> DeserializationResult<PropertyVariable> {
    let getValue = { parent?.get?.resolveValue(context: context) ?? .noValue }()
    let nameValue = { parent?.name?.resolveValue(context: context) ?? .noValue }()
    let newValueVariableNameValue = { parent?.newValueVariableName?.resolveOptionalValue(context: context) ?? .noValue }()
    let setValue = { parent?.set?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let valueTypeValue = { parent?.valueType?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      getValue.errorsOrWarnings?.map { .nestedObjectError(field: "get", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      newValueVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "new_value_variable_name", error: $0) },
      setValue.errorsOrWarnings?.map { .nestedObjectError(field: "set", error: $0) },
      valueTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "value_type", error: $0) }
    )
    if case .noValue = getValue {
      errors.append(.requiredFieldIsMissing(field: "get"))
    }
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = valueTypeValue {
      errors.append(.requiredFieldIsMissing(field: "value_type"))
    }
    guard
      let getNonNil = getValue.value,
      let nameNonNil = nameValue.value,
      let valueTypeNonNil = valueTypeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = PropertyVariable(
      get: { getNonNil }(),
      name: { nameNonNil }(),
      newValueVariableName: { newValueVariableNameValue.value }(),
      set: { setValue.value }(),
      valueType: { valueTypeNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: PropertyVariableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<PropertyVariable> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var getValue: DeserializationResult<Expression<String>> = { parent?.get?.value() ?? .noValue }()
    var nameValue: DeserializationResult<String> = { parent?.name?.value() ?? .noValue }()
    var newValueVariableNameValue: DeserializationResult<String> = { parent?.newValueVariableName?.value() ?? .noValue }()
    var setValue: DeserializationResult<[DivAction]> = .noValue
    var valueTypeValue: DeserializationResult<Expression<DivEvaluableType>> = { parent?.valueType?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "get" {
           getValue = deserialize(__dictValue).merged(with: getValue)
          }
        }()
        _ = {
          if key == "name" {
           nameValue = deserialize(__dictValue).merged(with: nameValue)
          }
        }()
        _ = {
          if key == "new_value_variable_name" {
           newValueVariableNameValue = deserialize(__dictValue).merged(with: newValueVariableNameValue)
          }
        }()
        _ = {
          if key == "set" {
           setValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: setValue)
          }
        }()
        _ = {
          if key == "value_type" {
           valueTypeValue = deserialize(__dictValue).merged(with: valueTypeValue)
          }
        }()
        _ = {
         if key == parent?.get?.link {
           getValue = getValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.name?.link {
           nameValue = nameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.newValueVariableName?.link {
           newValueVariableNameValue = newValueVariableNameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.set?.link {
           setValue = setValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.valueType?.link {
           valueTypeValue = valueTypeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { setValue = setValue.merged(with: { parent.set?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      getValue.errorsOrWarnings?.map { .nestedObjectError(field: "get", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      newValueVariableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "new_value_variable_name", error: $0) },
      setValue.errorsOrWarnings?.map { .nestedObjectError(field: "set", error: $0) },
      valueTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "value_type", error: $0) }
    )
    if case .noValue = getValue {
      errors.append(.requiredFieldIsMissing(field: "get"))
    }
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = valueTypeValue {
      errors.append(.requiredFieldIsMissing(field: "value_type"))
    }
    guard
      let getNonNil = getValue.value,
      let nameNonNil = nameValue.value,
      let valueTypeNonNil = valueTypeValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = PropertyVariable(
      get: { getNonNil }(),
      name: { nameNonNil }(),
      newValueVariableName: { newValueVariableNameValue.value }(),
      set: { setValue.value }(),
      valueType: { valueTypeNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> PropertyVariableTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? PropertyVariableTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return PropertyVariableTemplate(
      parent: nil,
      get: get ?? mergedParent.get,
      name: name ?? mergedParent.name,
      newValueVariableName: newValueVariableName ?? mergedParent.newValueVariableName,
      set: set ?? mergedParent.set,
      valueType: valueType ?? mergedParent.valueType
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> PropertyVariableTemplate {
    let merged = try mergedWithParent(templates: templates)

    return PropertyVariableTemplate(
      parent: nil,
      get: merged.get,
      name: merged.name,
      newValueVariableName: merged.newValueVariableName,
      set: merged.set?.tryResolveParent(templates: templates),
      valueType: merged.valueType
    )
  }
}
