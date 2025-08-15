// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithOptionalStringEnumPropertyTemplate: TemplateValue, Sendable {
  public typealias Property = EntityWithOptionalStringEnumProperty.Property

  public static let type: String = "entity_with_optional_string_enum_property"
  public let parent: String?
  public let property: Field<Expression<Property>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      property: dictionary.getOptionalExpressionField("property")
    )
  }

  init(
    parent: String?,
    property: Field<Expression<Property>>? = nil
  ) {
    self.parent = parent
    self.property = property
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithOptionalStringEnumPropertyTemplate?) -> DeserializationResult<EntityWithOptionalStringEnumProperty> {
    let propertyValue = { parent?.property?.resolveOptionalValue(context: context) ?? .noValue }()
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithOptionalStringEnumProperty(
      property: { propertyValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithOptionalStringEnumPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithOptionalStringEnumProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<Expression<EntityWithOptionalStringEnumProperty.Property>> = { parent?.property?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "property" {
           propertyValue = deserialize(__dictValue).merged(with: propertyValue)
          }
        }()
        _ = {
         if key == parent?.property?.link {
           propertyValue = propertyValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithOptionalStringEnumProperty(
      property: { propertyValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithOptionalStringEnumPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithOptionalStringEnumPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithOptionalStringEnumPropertyTemplate(
      parent: nil,
      property: property ?? mergedParent.property
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithOptionalStringEnumPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
