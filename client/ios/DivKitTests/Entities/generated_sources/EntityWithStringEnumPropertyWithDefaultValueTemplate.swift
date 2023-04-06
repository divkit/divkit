// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithStringEnumPropertyWithDefaultValueTemplate: TemplateValue, TemplateDeserializable {
  public typealias Value = EntityWithStringEnumPropertyWithDefaultValue.Value

  public static let type: String = "entity_with_string_enum_property_with_default_value"
  public let parent: String? // at least 1 char
  public let value: Field<Expression<Value>>? // default value: second

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      value: try dictionary.getOptionalExpressionField("value")
    )
  }

  init(
    parent: String?,
    value: Field<Expression<Value>>? = nil
  ) {
    self.parent = parent
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithStringEnumPropertyWithDefaultValueTemplate?) -> DeserializationResult<EntityWithStringEnumPropertyWithDefaultValue> {
    let valueValue = parent?.value?.resolveOptionalValue(context: context, validator: ResolvedValue.valueValidator) ?? .noValue
    let errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    let result = EntityWithStringEnumPropertyWithDefaultValue(
      value: valueValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithStringEnumPropertyWithDefaultValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringEnumPropertyWithDefaultValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var valueValue: DeserializationResult<Expression<EntityWithStringEnumPropertyWithDefaultValue.Value>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "value":
        valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator).merged(with: valueValue)
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.valueValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
    )
    let result = EntityWithStringEnumPropertyWithDefaultValue(
      value: valueValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithStringEnumPropertyWithDefaultValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithStringEnumPropertyWithDefaultValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithStringEnumPropertyWithDefaultValueTemplate(
      parent: nil,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithStringEnumPropertyWithDefaultValueTemplate {
    return try mergedWithParent(templates: templates)
  }
}
