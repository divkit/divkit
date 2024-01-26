// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithJsonPropertyTemplate: TemplateValue {
  public static let type: String = "entity_with_json_property"
  public let parent: String?
  public let jsonProperty: Field<[String: Any]>? // default value: { "key": "value", "items": [ "value" ] }

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      jsonProperty: dictionary.getOptionalField("json_property")
    )
  }

  init(
    parent: String?,
    jsonProperty: Field<[String: Any]>? = nil
  ) {
    self.parent = parent
    self.jsonProperty = jsonProperty
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithJsonPropertyTemplate?) -> DeserializationResult<EntityWithJsonProperty> {
    let jsonPropertyValue = parent?.jsonProperty?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      jsonPropertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "json_property", error: $0) }
    )
    let result = EntityWithJsonProperty(
      jsonProperty: jsonPropertyValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithJsonPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithJsonProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var jsonPropertyValue: DeserializationResult<[String: Any]> = parent?.jsonProperty?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "json_property":
        jsonPropertyValue = deserialize(__dictValue).merged(with: jsonPropertyValue)
      case parent?.jsonProperty?.link:
        jsonPropertyValue = jsonPropertyValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    let errors = mergeErrors(
      jsonPropertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "json_property", error: $0) }
    )
    let result = EntityWithJsonProperty(
      jsonProperty: jsonPropertyValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithJsonPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithJsonPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithJsonPropertyTemplate(
      parent: nil,
      jsonProperty: jsonProperty ?? mergedParent.jsonProperty
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithJsonPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
