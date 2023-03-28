// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

public final class EntityWithStringEnumPropertyTemplate: TemplateValue, TemplateDeserializable {
  public typealias Property = EntityWithStringEnumProperty.Property

  public static let type: String = "entity_with_string_enum_property"
  public let parent: String? // at least 1 char
  public let property: Field<Expression<Property>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
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

  private static func resolveOnlyLinks(context: Context, parent: EntityWithStringEnumPropertyTemplate?) -> DeserializationResult<EntityWithStringEnumProperty> {
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

  public static func resolveValue(context: Context, parent: EntityWithStringEnumPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringEnumProperty> {
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

  private func mergedWithParent(templates: Templates) throws -> EntityWithStringEnumPropertyTemplate {
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

  public func resolveParent(templates: Templates) throws -> EntityWithStringEnumPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
