// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithRequiredPropertyTemplate: TemplateValue {
  public static let type: String = "entity_with_required_property"
  public let parent: String?
  public let property: Field<Expression<String>>? // at least 1 char

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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithRequiredPropertyTemplate?) -> DeserializationResult<EntityWithRequiredProperty> {
    let propertyValue = parent?.property?.resolveValue(context: context, validator: ResolvedValue.propertyValidator) ?? .noValue
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
    let result = EntityWithRequiredProperty(
      property: propertyNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithRequiredPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithRequiredProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<Expression<String>> = parent?.property?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "property":
        propertyValue = deserialize(__dictValue, validator: ResolvedValue.propertyValidator).merged(with: propertyValue)
      case parent?.property?.link:
        propertyValue = propertyValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.propertyValidator) })
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
    let result = EntityWithRequiredProperty(
      property: propertyNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithRequiredPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithRequiredPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithRequiredPropertyTemplate(
      parent: nil,
      property: property ?? mergedParent.property
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithRequiredPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
