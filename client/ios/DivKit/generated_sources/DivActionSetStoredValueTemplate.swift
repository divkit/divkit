// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetStoredValueTemplate: TemplateValue {
  public static let type: String = "set_stored_value"
  public let parent: String?
  public let lifetime: Field<Expression<Int>>?
  public let name: Field<Expression<String>>?
  public let value: Field<DivTypedValueTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      lifetime: dictionary.getOptionalExpressionField("lifetime"),
      name: dictionary.getOptionalExpressionField("name"),
      value: dictionary.getOptionalField("value", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    lifetime: Field<Expression<Int>>? = nil,
    name: Field<Expression<String>>? = nil,
    value: Field<DivTypedValueTemplate>? = nil
  ) {
    self.parent = parent
    self.lifetime = lifetime
    self.name = name
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionSetStoredValueTemplate?) -> DeserializationResult<DivActionSetStoredValue> {
    let lifetimeValue = parent?.lifetime?.resolveValue(context: context) ?? .noValue
    let nameValue = parent?.name?.resolveValue(context: context) ?? .noValue
    let valueValue = parent?.value?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      lifetimeValue.errorsOrWarnings?.map { .nestedObjectError(field: "lifetime", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = lifetimeValue {
      errors.append(.requiredFieldIsMissing(field: "lifetime"))
    }
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let lifetimeNonNil = lifetimeValue.value,
      let nameNonNil = nameValue.value,
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetStoredValue(
      lifetime: lifetimeNonNil,
      name: nameNonNil,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionSetStoredValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSetStoredValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var lifetimeValue: DeserializationResult<Expression<Int>> = parent?.lifetime?.value() ?? .noValue
    var nameValue: DeserializationResult<Expression<String>> = parent?.name?.value() ?? .noValue
    var valueValue: DeserializationResult<DivTypedValue> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "lifetime":
        lifetimeValue = deserialize(__dictValue).merged(with: lifetimeValue)
      case "name":
        nameValue = deserialize(__dictValue).merged(with: nameValue)
      case "value":
        valueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: valueValue)
      case parent?.lifetime?.link:
        lifetimeValue = lifetimeValue.merged(with: { deserialize(__dictValue) })
      case parent?.name?.link:
        nameValue = nameValue.merged(with: { deserialize(__dictValue) })
      case parent?.value?.link:
        valueValue = valueValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      valueValue = valueValue.merged(with: { parent.value?.resolveValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      lifetimeValue.errorsOrWarnings?.map { .nestedObjectError(field: "lifetime", error: $0) },
      nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    if case .noValue = lifetimeValue {
      errors.append(.requiredFieldIsMissing(field: "lifetime"))
    }
    if case .noValue = nameValue {
      errors.append(.requiredFieldIsMissing(field: "name"))
    }
    if case .noValue = valueValue {
      errors.append(.requiredFieldIsMissing(field: "value"))
    }
    guard
      let lifetimeNonNil = lifetimeValue.value,
      let nameNonNil = nameValue.value,
      let valueNonNil = valueValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetStoredValue(
      lifetime: lifetimeNonNil,
      name: nameNonNil,
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionSetStoredValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionSetStoredValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionSetStoredValueTemplate(
      parent: nil,
      lifetime: lifetime ?? mergedParent.lifetime,
      name: name ?? mergedParent.name,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionSetStoredValueTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionSetStoredValueTemplate(
      parent: nil,
      lifetime: merged.lifetime,
      name: merged.name,
      value: try merged.value?.resolveParent(templates: templates)
    )
  }
}
