// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class EntityWithOptionalPropertyTemplate: TemplateValue {
  public static let type: String = "entity_with_optional_property"
  public let parent: String?
  public let property: Field<Expression<String>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      property: dictionary.getOptionalExpressionField("property")
    )
  }

  init(
    parent: String?,
    property: Field<Expression<String>>? = nil
  ) {
    self.parent = parent
    self.property = property
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithOptionalPropertyTemplate?) -> DeserializationResult<EntityWithOptionalProperty> {
    let propertyValue = parent?.property?.resolveOptionalValue(context: context) ?? .noValue
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithOptionalProperty(
      property: propertyValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithOptionalPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithOptionalProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<Expression<String>> = parent?.property?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "property":
        propertyValue = deserialize(__dictValue).merged(with: propertyValue)
      case parent?.property?.link:
        propertyValue = propertyValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithOptionalProperty(
      property: propertyValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithOptionalPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithOptionalPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithOptionalPropertyTemplate(
      parent: nil,
      property: property ?? mergedParent.property
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithOptionalPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
