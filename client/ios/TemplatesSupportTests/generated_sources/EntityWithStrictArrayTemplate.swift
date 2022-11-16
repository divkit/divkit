// Generated code. Do not modify.

@testable import DivKit

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class EntityWithStrictArrayTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "entity_with_strict_array"
  public let parent: String? // at least 1 char
  public let array: Field<[EntityTemplate]>? // at least 1 elements; all received elements must be valid

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let arrayValidator: AnyArrayValueValidator<EntityTemplate> =
    makeStrictArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        array: try dictionary.getOptionalArray("array", templateToType: templateToType, validator: Self.arrayValidator)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "entity_with_strict_array_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    array: Field<[EntityTemplate]>? = nil
  ) {
    self.parent = parent
    self.array = array
  }

  private static func resolveOnlyLinks(context: Context, parent: EntityWithStrictArrayTemplate?) -> DeserializationResult<EntityWithStrictArray> {
    let arrayValue = parent?.array?.resolveValue(context: context, validator: ResolvedValue.arrayValidator, useOnlyLinks: true) ?? .noValue
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
    let result = EntityWithStrictArray(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: EntityWithStrictArrayTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithStrictArray> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var arrayValue: DeserializationResult<[Entity]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "array":
        arrayValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.arrayValidator, type: EntityTemplate.self).merged(with: arrayValue)
      case parent?.array?.link:
        arrayValue = arrayValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.arrayValidator, type: EntityTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      arrayValue = arrayValue.merged(with: parent.array?.resolveValue(context: context, validator: ResolvedValue.arrayValidator, useOnlyLinks: true))
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
    let result = EntityWithStrictArray(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> EntityWithStrictArrayTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithStrictArrayTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithStrictArrayTemplate(
      parent: nil,
      array: array ?? mergedParent.array
    )
  }

  public func resolveParent(templates: Templates) throws -> EntityWithStrictArrayTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithStrictArrayTemplate(
      parent: nil,
      array: try merged.array?.resolveParent(templates: templates, validator: Self.arrayValidator)
    )
  }
}
