// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStringArrayPropertyTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "entity_with_string_array_property"
  public let parent: String? // at least 1 char
  public let array: Field<[Expression<String>]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        array: try dictionary.getOptionalExpressionArray("array")
      )
    } catch let DeserializationError.invalidFieldRepresentation(fieldName: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(fieldName: "entity_with_string_array_property_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    array: Field<[Expression<String>]>? = nil
  ) {
    self.parent = parent
    self.array = array
  }

  private static func resolveOnlyLinks(context: Context, parent: EntityWithStringArrayPropertyTemplate?) -> DeserializationResult<EntityWithStringArrayProperty> {
    let arrayValue = parent?.array?.resolveValue(context: context, validator: ResolvedValue.arrayValidator) ?? .noValue
    var errors = mergeErrors(
      arrayValue.errorsOrWarnings?.map { .nestedObjectError(fieldName: "array", error: $0) }
    )
    if case .noValue = arrayValue {
      errors.append(.requiredFieldIsMissing(fieldName: "array"))
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

  public static func resolveValue(context: Context, parent: EntityWithStringArrayPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStringArrayProperty> {
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
      arrayValue.errorsOrWarnings?.map { .nestedObjectError(fieldName: "array", error: $0) }
    )
    if case .noValue = arrayValue {
      errors.append(.requiredFieldIsMissing(fieldName: "array"))
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

  private func mergedWithParent(templates: Templates) throws -> EntityWithStringArrayPropertyTemplate {
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

  public func resolveParent(templates: Templates) throws -> EntityWithStringArrayPropertyTemplate {
    return try mergedWithParent(templates: templates)
  }
}
