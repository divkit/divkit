// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class EntityWithArrayOfEnumsTemplate: TemplateValue {
  public typealias Item = EntityWithArrayOfEnums.Item

  public static let type: String = "entity_with_array_of_enums"
  public let parent: String?
  public let items: Field<[Item]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      items: dictionary.getOptionalArray("items")
    )
  }

  init(
    parent: String?,
    items: Field<[Item]>? = nil
  ) {
    self.parent = parent
    self.items = items
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithArrayOfEnumsTemplate?) -> DeserializationResult<EntityWithArrayOfEnums> {
    let itemsValue = parent?.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator) ?? .noValue
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithArrayOfEnums(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithArrayOfEnumsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArrayOfEnums> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemsValue: DeserializationResult<[EntityWithArrayOfEnums.Item]> = parent?.items?.value(validatedBy: ResolvedValue.itemsValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "items":
        itemsValue = deserialize(__dictValue, validator: ResolvedValue.itemsValidator).merged(with: itemsValue)
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.itemsValidator) })
      default: break
      }
    }
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithArrayOfEnums(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithArrayOfEnumsTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithArrayOfEnumsTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithArrayOfEnumsTemplate(
      parent: nil,
      items: items ?? mergedParent.items
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithArrayOfEnumsTemplate {
    return try mergedWithParent(templates: templates)
  }
}
