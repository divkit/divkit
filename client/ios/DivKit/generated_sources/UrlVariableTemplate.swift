// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class UrlVariableTemplate: TemplateValue {
  public static let type: String = "url"
  public let parent: String?
  public let name: Field<String>?
  public let value: Field<URL>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      name: dictionary.getOptionalField("name"),
      value: dictionary.getOptionalField("value", transform: URL.init(string:))
    )
  }

  init(
    parent: String?,
    name: Field<String>? = nil,
    value: Field<URL>? = nil
  ) {
    self.parent = parent
    self.name = name
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: UrlVariableTemplate?) -> DeserializationResult<UrlVariable> {
    let nameValue = parent?.name?.resolveValue(context: context) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
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
    let result = UrlVariable(
      name: nameNonNil,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: UrlVariableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<UrlVariable> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var nameValue: DeserializationResult<String> = parent?.name?.value() ?? .noValue
    var valueValue: DeserializationResult<URL> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "name":
        nameValue = deserialize(__dictValue).merged(with: nameValue)
      case "value":
        valueValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: valueValue)
      case parent?.name?.link:
        nameValue = nameValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
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
    let result = UrlVariable(
      name: nameNonNil,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> UrlVariableTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? UrlVariableTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return UrlVariableTemplate(
      parent: nil,
      name: name ?? mergedParent.name,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> UrlVariableTemplate {
    return try mergedWithParent(templates: templates)
  }
}
