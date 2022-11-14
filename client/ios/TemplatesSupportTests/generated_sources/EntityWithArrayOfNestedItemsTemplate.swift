// Generated code. Do not modify.

@testable import DivKit

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class EntityWithArrayOfNestedItemsTemplate: TemplateValue, TemplateDeserializable {
  public final class ItemTemplate: TemplateValue, TemplateDeserializable {
    public let entity: Field<EntityTemplate>?
    public let property: Field<Expression<String>>? // at least 1 char

    public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
      do {
        self.init(
          entity: try dictionary.getOptionalField("entity", templateToType: templateToType),
          property: try dictionary.getOptionalExpressionField("property")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "item_template." + field, representation: representation)
      }
    }

    init(
      entity: Field<EntityTemplate>? = nil,
      property: Field<Expression<String>>? = nil
    ) {
      self.entity = entity
      self.property = property
    }

    private static func resolveOnlyLinks(context: Context, parent: ItemTemplate?) -> DeserializationResult<EntityWithArrayOfNestedItems.Item> {
      let entityValue = parent?.entity?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
      let propertyValue = parent?.property?.resolveValue(context: context, validator: ResolvedValue.propertyValidator) ?? .noValue
      var errors = mergeErrors(
        entityValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "entity", level: .error)) },
        propertyValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "property", level: .error)) }
      )
      if case .noValue = entityValue {
        errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "entity")))
      }
      if case .noValue = propertyValue {
        errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "property")))
      }
      guard
        let entityNonNil = entityValue.value,
        let propertyNonNil = propertyValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithArrayOfNestedItems.Item(
        entity: entityNonNil,
        property: propertyNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: Context, parent: ItemTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArrayOfNestedItems.Item> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var entityValue: DeserializationResult<Entity> = .noValue
      var propertyValue: DeserializationResult<Expression<String>> = parent?.property?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "entity":
          entityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityTemplate.self).merged(with: entityValue)
        case "property":
          propertyValue = deserialize(__dictValue, validator: ResolvedValue.propertyValidator).merged(with: propertyValue)
        case parent?.entity?.link:
          entityValue = entityValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityTemplate.self))
        case parent?.property?.link:
          propertyValue = propertyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.propertyValidator))
        default: break
        }
      }
      if let parent = parent {
        entityValue = entityValue.merged(with: parent.entity?.resolveValue(context: context, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        entityValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "entity", level: .error)) },
        propertyValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "property", level: .error)) }
      )
      if case .noValue = entityValue {
        errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "entity")))
      }
      if case .noValue = propertyValue {
        errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "property")))
      }
      guard
        let entityNonNil = entityValue.value,
        let propertyNonNil = propertyValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithArrayOfNestedItems.Item(
        entity: entityNonNil,
        property: propertyNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: Templates) throws -> ItemTemplate {
      return self
    }

    public func resolveParent(templates: Templates) throws -> ItemTemplate {
      let merged = try mergedWithParent(templates: templates)

      return ItemTemplate(
        entity: try merged.entity?.resolveParent(templates: templates),
        property: merged.property
      )
    }
  }

  public static let type: String = "entity_with_array_of_nested_items"
  public let parent: String? // at least 1 char
  public let items: Field<[ItemTemplate]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        items: try dictionary.getOptionalArray("items", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "entity_with_array_of_nested_items_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    items: Field<[ItemTemplate]>? = nil
  ) {
    self.parent = parent
    self.items = items
  }

  private static func resolveOnlyLinks(context: Context, parent: EntityWithArrayOfNestedItemsTemplate?) -> DeserializationResult<EntityWithArrayOfNestedItems> {
    let itemsValue = parent?.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "items", level: .error)) }
    )
    if case .noValue = itemsValue {
      errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "items")))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithArrayOfNestedItems(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: EntityWithArrayOfNestedItemsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArrayOfNestedItems> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemsValue: DeserializationResult<[EntityWithArrayOfNestedItems.Item]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: EntityWithArrayOfNestedItemsTemplate.ItemTemplate.self).merged(with: itemsValue)
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: EntityWithArrayOfNestedItemsTemplate.ItemTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      itemsValue = itemsValue.merged(with: parent.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "items", level: .error)) }
    )
    if case .noValue = itemsValue {
      errors.append(.left(DeserializationError.requiredFieldIsMissing(fieldName: "items")))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = EntityWithArrayOfNestedItems(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> EntityWithArrayOfNestedItemsTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithArrayOfNestedItemsTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithArrayOfNestedItemsTemplate(
      parent: nil,
      items: items ?? mergedParent.items
    )
  }

  public func resolveParent(templates: Templates) throws -> EntityWithArrayOfNestedItemsTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithArrayOfNestedItemsTemplate(
      parent: nil,
      items: try merged.items?.resolveParent(templates: templates)
    )
  }
}
