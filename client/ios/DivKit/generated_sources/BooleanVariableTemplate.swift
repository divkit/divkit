// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class BooleanVariableTemplate: TemplateValue {
  public static let type: String = "boolean"
  public let parent: String?
  public let name: Field<String>?
  public let value: Field<Bool>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      name: dictionary.getOptionalField("name"),
      value: dictionary.getOptionalField("value")
    )
  }

  init(
    parent: String?,
    name: Field<String>? = nil,
    value: Field<Bool>? = nil
  ) {
    self.parent = parent
    self.name = name
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: BooleanVariableTemplate?) -> DeserializationResult<BooleanVariable> {
    let nameValue = parent?.name?.resolveValue(context: context) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let nameNonNil = nameValue.value,
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = BooleanVariable(
      name: nameNonNil,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: BooleanVariableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<BooleanVariable> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var nameValue: DeserializationResult<String> = parent?.name?.value() ?? .noValue
    var valueValue: DeserializationResult<Bool> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "name":
        nameValue = deserialize(__dictValue).merged(with: nameValue)
      case "value":
        valueValue = deserialize(__dictValue).merged(with: valueValue)
      case parent?.name?.link:
        nameValue = nameValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    var errors = mergeErrors(
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let nameNonNil = nameValue.value,
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = BooleanVariable(
      name: nameNonNil,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> BooleanVariableTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? BooleanVariableTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return BooleanVariableTemplate(
      parent: nil,
      name: name ?? mergedParent.name,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> BooleanVariableTemplate {
    return try mergedWithParent(templates: templates)
  }
}
