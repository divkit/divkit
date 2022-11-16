// Generated code. Do not modify.

@testable import DivKit

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class EntityWithComplexPropertyWithDefaultValueTemplate: TemplateValue, TemplateDeserializable {
  public final class PropertyTemplate: TemplateValue, TemplateDeserializable {
    public let value: Field<Expression<String>>?

    public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
      do {
        self.init(
          value: try dictionary.getOptionalExpressionField("value")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "property_template." + field, representation: representation)
      }
    }

    init(
      value: Field<Expression<String>>? = nil
    ) {
      self.value = value
    }

    private static func resolveOnlyLinks(context: Context, parent: PropertyTemplate?) -> DeserializationResult<EntityWithComplexPropertyWithDefaultValue.Property> {
      let valueValue = parent?.value?.resolveValue(context: context) ?? .noValue
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
      let result = EntityWithComplexPropertyWithDefaultValue.Property(
        value: valueNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: Context, parent: PropertyTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithComplexPropertyWithDefaultValue.Property> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var valueValue: DeserializationResult<Expression<String>> = parent?.value?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "value":
          valueValue = deserialize(__dictValue).merged(with: valueValue)
        case parent?.value?.link:
          valueValue = valueValue.merged(with: deserialize(__dictValue))
        default: break
        }
      }
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
      let result = EntityWithComplexPropertyWithDefaultValue.Property(
        value: valueNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: Templates) throws -> PropertyTemplate {
      return self
    }

    public func resolveParent(templates: Templates) throws -> PropertyTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "entity_with_complex_property_with_default_value"
  public let parent: String? // at least 1 char
  public let property: Field<PropertyTemplate>? // default value: EntityWithComplexPropertyWithDefaultValueTemplate.Property(value: .value("Default text"))

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      property: try dictionary.getOptionalField("property", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    property: Field<PropertyTemplate>? = nil
  ) {
    self.parent = parent
    self.property = property
  }

  private static func resolveOnlyLinks(context: Context, parent: EntityWithComplexPropertyWithDefaultValueTemplate?) -> DeserializationResult<EntityWithComplexPropertyWithDefaultValue> {
    let propertyValue = parent?.property?.resolveOptionalValue(context: context, validator: ResolvedValue.propertyValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithComplexPropertyWithDefaultValue(
      property: propertyValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: EntityWithComplexPropertyWithDefaultValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithComplexPropertyWithDefaultValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var propertyValue: DeserializationResult<EntityWithComplexPropertyWithDefaultValue.Property> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "property":
        propertyValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.propertyValidator, type: EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate.self).merged(with: propertyValue)
      case parent?.property?.link:
        propertyValue = propertyValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.propertyValidator, type: EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      propertyValue = propertyValue.merged(with: parent.property?.resolveOptionalValue(context: context, validator: ResolvedValue.propertyValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      propertyValue.errorsOrWarnings?.map { .nestedObjectError(field: "property", error: $0) }
    )
    let result = EntityWithComplexPropertyWithDefaultValue(
      property: propertyValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> EntityWithComplexPropertyWithDefaultValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithComplexPropertyWithDefaultValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithComplexPropertyWithDefaultValueTemplate(
      parent: nil,
      property: property ?? mergedParent.property
    )
  }

  public func resolveParent(templates: Templates) throws -> EntityWithComplexPropertyWithDefaultValueTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithComplexPropertyWithDefaultValueTemplate(
      parent: nil,
      property: merged.property?.tryResolveParent(templates: templates)
    )
  }
}
