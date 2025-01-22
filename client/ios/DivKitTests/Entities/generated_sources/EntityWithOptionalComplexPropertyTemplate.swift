// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithOptionalComplexPropertyTemplate: TemplateValue, Sendable {
  public final class PropertyTemplate: TemplateValue, Sendable {
    public let value: Field<Expression<URL>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        value: dictionary.getOptionalExpressionField("value", transform: URL.init(string:))
      )
    }

    init(
      value: Field<Expression<URL>>? = nil
    ) {
      self.value = value
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: PropertyTemplate?) -> DeserializationResult<EntityWithOptionalComplexProperty.Property> {
      let valueValue = { parent?.value?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue }()
      var errors = mergeErrors(
        valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
      )
      if case .noValue = valueValue {
        errors.append(.requiredFieldIsMissing(field: "value"))
      }
      guard
        let valueNonNil = valueValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithOptionalComplexProperty.Property(
        value: { valueNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: PropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithOptionalComplexProperty.Property> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var valueValue: DeserializationResult<Expression<URL>> = { parent?.value?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "value" {
             valueValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: valueValue)
            }
          }()
          _ = {
           if key == parent?.value?.link {
             valueValue = valueValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
            }
          }()
        }
      }()
      var errors = mergeErrors(
        valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
      )
      if case .noValue = valueValue {
        errors.append(.requiredFieldIsMissing(field: "value"))
      }
      guard
        let valueNonNil = valueValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithOptionalComplexProperty.Property(
        value: { valueNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> PropertyTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> PropertyTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "entity_with_optional_complex_property"
  public let parent: String?
  public let property: Field<PropertyTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      property: dictionary.getOptionalField("property", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    property: Field<PropertyTemplate>? = nil
  ) {
    self.parent = parent
    self.property = property
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithOptionalComplexPropertyTemplate?) -> DeserializationResult<EntityWithOptionalComplexProperty> {
    let propertyValue = { parent?.property?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithOptionalComplexProperty(
      property: { propertyValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithOptionalComplexPropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithOptionalComplexProperty> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<EntityWithOptionalComplexProperty.Property> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "property" {
           propertyValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate.self).merged(with: propertyValue)
          }
        }()
        _ = {
         if key == parent?.property?.link {
           propertyValue = propertyValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { propertyValue = propertyValue.merged(with: { parent.property?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithOptionalComplexProperty(
      property: { propertyValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithOptionalComplexPropertyTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithOptionalComplexPropertyTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithOptionalComplexPropertyTemplate(
      parent: nil,
      property: property ?? mergedParent.property
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithOptionalComplexPropertyTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithOptionalComplexPropertyTemplate(
      parent: nil,
      property: merged.property?.tryResolveParent(templates: templates)
    )
  }
}
