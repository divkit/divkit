// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class EntityWithArrayTemplate: TemplateValue {
  public static let type: String = "entity_with_array"
  public let parent: String?
  public let array: Field<[EntityTemplate]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      array: dictionary.getOptionalArray("array", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    array: Field<[EntityTemplate]>? = nil
  ) {
    self.parent = parent
    self.array = array
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithArrayTemplate?) -> DeserializationResult<EntityWithArray> {
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
    let result = EntityWithArray(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithArrayTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArray> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var arrayValue: DeserializationResult<[Entity]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "array":
        arrayValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.arrayValidator, type: EntityTemplate.self).merged(with: arrayValue)
      case parent?.array?.link:
        arrayValue = arrayValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.arrayValidator, type: EntityTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      arrayValue = arrayValue.merged(with: { parent.array?.resolveValue(context: context, validator: ResolvedValue.arrayValidator, useOnlyLinks: true) })
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
    let result = EntityWithArray(
      array: arrayNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithArrayTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithArrayTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithArrayTemplate(
      parent: nil,
      array: array ?? mergedParent.array
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithArrayTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithArrayTemplate(
      parent: nil,
      array: try merged.array?.resolveParent(templates: templates)
    )
  }
}
