// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithStringEnumPropertyTemplate: TemplateValue {
  public typealias Property = EntityWithStringEnumProperty.Property

  public static let type: String = "entity_with_string_enum_property"
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithStringEnumPropertyTemplate?) -> DeserializationResult<EntityWithStringEnumProperty> {
    let propertyValue = { parent?.property?.resolveValue(context: context) ?? .noValue }()
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
      property: { propertyNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithStringEnumPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringEnumProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<Expression<EntityWithStringEnumProperty.Property>> = { parent?.property?.value() ?? .noValue }()
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
      property: { propertyNonNil }()
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
