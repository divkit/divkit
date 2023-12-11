// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStringEnumPropertyTemplate: TemplateValue {
  public typealias Property = EntityWithStringEnumProperty.Property

  public static let type: String = "entity_with_string_enum_property"
  public let parent: String?
  public let property: Field<Expression<Property>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type"),
        property: try dictionary.getOptionalExpressionField("property")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "entity_with_string_enum_property_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    property: Field<Expression<Property>>? = nil
  ) {
    self.parent = parent
    self.property = property
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithStringEnumPropertyTemplate?) -> DeserializationResult<EntityWithStringEnumProperty> {
    let propertyValue = parent?.property?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    if case .noValue = propertyValue {
      errors.append(.requiredFieldIsMissing(field: "property"))
    }
    guard
      let propertyNonNil = propertyValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithStringEnumProperty(
      property: propertyNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithStringEnumPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringEnumProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<Expression<EntityWithStringEnumProperty.Property>> = parent?.property?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "property":
        propertyValue = deserialize(__dictValue).merged(with: propertyValue)
      case parent?.property?.link:
        propertyValue = propertyValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    var errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    if case .noValue = propertyValue {
      errors.append(.requiredFieldIsMissing(field: "property"))
    }
    guard
      let propertyNonNil = propertyValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithStringEnumProperty(
      property: propertyNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithStringEnumPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithStringEnumPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithStringEnumPropertyTemplate(
      parent: nil,
      property: property ?? mergedParent.property
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithStringEnumPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
