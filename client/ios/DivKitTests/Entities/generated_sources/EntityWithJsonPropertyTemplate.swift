// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

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
    let jsonPropertyValue = { parent?.jsonProperty?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      jsonPropertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "json_property", error: $0) }
    )
    let result = EntityWithJsonProperty(
      jsonProperty: { jsonPropertyValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithJsonPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithJsonProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var jsonPropertyValue: DeserializationResult<[String: Any]> = { parent?.jsonProperty?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "json_property" {
           jsonPropertyValue = deserialize(__dictValue).merged(with: jsonPropertyValue)
          }
        }()
        _ = {
         if key == parent?.jsonProperty?.link {
           jsonPropertyValue = jsonPropertyValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      jsonPropertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "json_property", error: $0) }
    )
    let result = EntityWithJsonProperty(
      jsonProperty: { jsonPropertyValue.value }()
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
