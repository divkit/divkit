// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithStringArrayPropertyTemplate: TemplateValue {
  public static let type: String = "entity_with_string_array_property"
  public let parent: String?
  public let array: Field<[Expression<String>]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type"),
        array: try dictionary.getOptionalExpressionArray("array")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "entity_with_string_array_property_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    array: Field<[Expression<String>]>? = nil
  ) {
    self.parent = parent
    self.array = array
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithStringArrayPropertyTemplate?) -> DeserializationResult<EntityWithStringArrayProperty> {
    let arrayValue = parent?.array?.resolveValue(context: context, validator: ResolvedValue.arrayValidator) ?? .noValue
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { .nestedObjectError(field: "array", error: $0) }
    )
    if case .noValue = arrayValue {
      errors.append(.requiredFieldIsMissing(field: "array"))
    }
    guard
      let arrayNonNil = arrayValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithStringArrayProperty(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithStringArrayPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringArrayProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var arrayValue: DeserializationResult<[Expression<String>]> = parent?.array?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "array":
        arrayValue = deserialize(__dictValue, validator: ResolvedValue.arrayValidator).merged(with: arrayValue)
      case parent?.array?.link:
        arrayValue = arrayValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.arrayValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { .nestedObjectError(field: "array", error: $0) }
    )
    if case .noValue = arrayValue {
      errors.append(.requiredFieldIsMissing(field: "array"))
    }
    guard
      let arrayNonNil = arrayValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithStringArrayProperty(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithStringArrayPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithStringArrayPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithStringArrayPropertyTemplate(
      parent: nil,
      array: array ?? mergedParent.array
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithStringArrayPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
