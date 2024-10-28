// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithArrayOfNestedItemsTemplate: TemplateValue {
  public final class ItemTemplate: TemplateValue {
    public let entity: Field<EntityTemplate>?
    public let property: Field<Expression<String>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        entity: dictionary.getOptionalField("entity", templateToType: templateToType),
        property: dictionary.getOptionalExpressionField("property")
      )
    }

    init(
      entity: Field<EntityTemplate>? = nil,
      property: Field<Expression<String>>? = nil
    ) {
      self.entity = entity
      self.property = property
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ItemTemplate?) -> DeserializationResult<EntityWithArrayOfNestedItems.Item> {
      let entityValue = { parent?.entity?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let propertyValue = { parent?.property?.resolveValue(context: context) ?? .noValue }()
      var errors = mergeErrors(
        entityValue.errorsOrWarnings?.map { .nestedObjectError(field: "entity", error: $0) },
        propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
      )
      if case .noValue = entityValue {
        errors.append(.requiredFieldIsMissing(field: "entity"))
      }
      if case .noValue = propertyValue {
        errors.append(.requiredFieldIsMissing(field: "property"))
      }
      guard
        let entityNonNil = entityValue.value,
        let propertyNonNil = propertyValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithArrayOfNestedItems.Item(
        entity: { entityNonNil }(),
        property: { propertyNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ItemTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArrayOfNestedItems.Item> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var entityValue: DeserializationResult<Entity> = .noValue
      var propertyValue: DeserializationResult<Expression<String>> = { parent?.property?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "entity" {
             entityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityTemplate.self).merged(with: entityValue)
            }
          }()
          _ = {
            if key == "property" {
             propertyValue = deserialize(__dictValue).merged(with: propertyValue)
            }
          }()
          _ = {
           if key == parent?.entity?.link {
             entityValue = entityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.property?.link {
             propertyValue = propertyValue.merged(with: { deserialize(__dictValue) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { entityValue = entityValue.merged(with: { parent.entity?.resolveValue(context: context, useOnlyLinks: true) }) }()
      }
      var errors = mergeErrors(
        entityValue.errorsOrWarnings?.map { .nestedObjectError(field: "entity", error: $0) },
        propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
      )
      if case .noValue = entityValue {
        errors.append(.requiredFieldIsMissing(field: "entity"))
      }
      if case .noValue = propertyValue {
        errors.append(.requiredFieldIsMissing(field: "property"))
      }
      guard
        let entityNonNil = entityValue.value,
        let propertyNonNil = propertyValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithArrayOfNestedItems.Item(
        entity: { entityNonNil }(),
        property: { propertyNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ItemTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ItemTemplate {
      let merged = try mergedWithParent(templates: templates)

      return ItemTemplate(
        entity: try merged.entity?.resolveParent(templates: templates),
        property: merged.property
      )
    }
  }

  public static let type: String = "entity_with_array_of_nested_items"
  public let parent: String?
  public let items: Field<[ItemTemplate]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      items: dictionary.getOptionalArray("items", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    items: Field<[ItemTemplate]>? = nil
  ) {
    self.parent = parent
    self.items = items
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithArrayOfNestedItemsTemplate?) -> DeserializationResult<EntityWithArrayOfNestedItems> {
    let itemsValue = { parent?.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue }()
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
    let result = EntityWithArrayOfNestedItems(
      items: { itemsNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithArrayOfNestedItemsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithArrayOfNestedItems> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemsValue: DeserializationResult<[EntityWithArrayOfNestedItems.Item]> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "items" {
           itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: EntityWithArrayOfNestedItemsTemplate.ItemTemplate.self).merged(with: itemsValue)
          }
        }()
        _ = {
         if key == parent?.items?.link {
           itemsValue = itemsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: EntityWithArrayOfNestedItemsTemplate.ItemTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { itemsValue = itemsValue.merged(with: { parent.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) }) }()
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
    let result = EntityWithArrayOfNestedItems(
      items: { itemsNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithArrayOfNestedItemsTemplate {
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithArrayOfNestedItemsTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithArrayOfNestedItemsTemplate(
      parent: nil,
      items: try merged.items?.resolveParent(templates: templates)
    )
  }
}
