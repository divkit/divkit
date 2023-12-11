// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithPropertyWithDefaultValueTemplate: TemplateValue {
  public final class NestedTemplate: TemplateValue {
    public let int: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
    public let nonOptional: Field<Expression<String>>?
    public let url: Field<Expression<URL>>? // valid schemes: [https]; default value: https://yandex.ru

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          int: try dictionary.getOptionalExpressionField("int"),
          nonOptional: try dictionary.getOptionalExpressionField("non_optional"),
          url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "nested_template." + field, representation: representation)
      }
    }

    init(
      int: Field<Expression<Int>>? = nil,
      nonOptional: Field<Expression<String>>? = nil,
      url: Field<Expression<URL>>? = nil
    ) {
      self.int = int
      self.nonOptional = nonOptional
      self.url = url
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: NestedTemplate?) -> DeserializationResult<EntityWithPropertyWithDefaultValue.Nested> {
      let intValue = parent?.int?.resolveOptionalValue(context: context, validator: ResolvedValue.intValidator) ?? .noValue
      let nonOptionalValue = parent?.nonOptional?.resolveValue(context: context) ?? .noValue
      let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.urlValidator) ?? .noValue
      var errors = mergeErrors(
        intValue.errorsOrWarnings?.map { .nestedObjectError(field: "int", error: $0) },
        nonOptionalValue.errorsOrWarnings?.map { .nestedObjectError(field: "non_optional", error: $0) },
        urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
      )
      if case .noValue = nonOptionalValue {
        errors.append(.requiredFieldIsMissing(field: "non_optional"))
      }
      guard
        let nonOptionalNonNil = nonOptionalValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithPropertyWithDefaultValue.Nested(
        int: intValue.value,
        nonOptional: nonOptionalNonNil,
        url: urlValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: NestedTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithPropertyWithDefaultValue.Nested> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var intValue: DeserializationResult<Expression<Int>> = parent?.int?.value() ?? .noValue
      var nonOptionalValue: DeserializationResult<Expression<String>> = parent?.nonOptional?.value() ?? .noValue
      var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "int":
          intValue = deserialize(__dictValue, validator: ResolvedValue.intValidator).merged(with: intValue)
        case "non_optional":
          nonOptionalValue = deserialize(__dictValue).merged(with: nonOptionalValue)
        case "url":
          urlValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator).merged(with: urlValue)
        case parent?.int?.link:
          intValue = intValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.intValidator))
        case parent?.nonOptional?.link:
          nonOptionalValue = nonOptionalValue.merged(with: deserialize(__dictValue))
        case parent?.url?.link:
          urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator))
        default: break
        }
      }
      var errors = mergeErrors(
        intValue.errorsOrWarnings?.map { .nestedObjectError(field: "int", error: $0) },
        nonOptionalValue.errorsOrWarnings?.map { .nestedObjectError(field: "non_optional", error: $0) },
        urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
      )
      if case .noValue = nonOptionalValue {
        errors.append(.requiredFieldIsMissing(field: "non_optional"))
      }
      guard
        let nonOptionalNonNil = nonOptionalValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = EntityWithPropertyWithDefaultValue.Nested(
        int: intValue.value,
        nonOptional: nonOptionalNonNil,
        url: urlValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> NestedTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> NestedTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "entity_with_property_with_default_value"
  public let parent: String?
  public let int: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let nested: Field<NestedTemplate>?
  public let url: Field<Expression<URL>>? // valid schemes: [https]; default value: https://yandex.ru

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type"),
      int: try dictionary.getOptionalExpressionField("int"),
      nested: try dictionary.getOptionalField("nested", templateToType: templateToType),
      url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
    )
  }

  init(
    parent: String?,
    int: Field<Expression<Int>>? = nil,
    nested: Field<NestedTemplate>? = nil,
    url: Field<Expression<URL>>? = nil
  ) {
    self.parent = parent
    self.int = int
    self.nested = nested
    self.url = url
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: EntityWithPropertyWithDefaultValueTemplate?) -> DeserializationResult<EntityWithPropertyWithDefaultValue> {
    let intValue = parent?.int?.resolveOptionalValue(context: context, validator: ResolvedValue.intValidator) ?? .noValue
    let nestedValue = parent?.nested?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let urlValue = parent?.url?.resolveOptionalValue(context: context, transform: URL.init(string:), validator: ResolvedValue.urlValidator) ?? .noValue
    let errors = mergeErrors(
      intValue.errorsOrWarnings?.map { .nestedObjectError(field: "int", error: $0) },
      nestedValue.errorsOrWarnings?.map { .nestedObjectError(field: "nested", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    let result = EntityWithPropertyWithDefaultValue(
      int: intValue.value,
      nested: nestedValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityWithPropertyWithDefaultValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<EntityWithPropertyWithDefaultValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var intValue: DeserializationResult<Expression<Int>> = parent?.int?.value() ?? .noValue
    var nestedValue: DeserializationResult<EntityWithPropertyWithDefaultValue.Nested> = .noValue
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "int":
        intValue = deserialize(__dictValue, validator: ResolvedValue.intValidator).merged(with: intValue)
      case "nested":
        nestedValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate.self).merged(with: nestedValue)
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator).merged(with: urlValue)
      case parent?.int?.link:
        intValue = intValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.intValidator))
      case parent?.nested?.link:
        nestedValue = nestedValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate.self))
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:), validator: ResolvedValue.urlValidator))
      default: break
      }
    }
    if let parent = parent {
      nestedValue = nestedValue.merged(with: parent.nested?.resolveOptionalValue(context: context, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      intValue.errorsOrWarnings?.map { .nestedObjectError(field: "int", error: $0) },
      nestedValue.errorsOrWarnings?.map { .nestedObjectError(field: "nested", error: $0) },
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    let result = EntityWithPropertyWithDefaultValue(
      int: intValue.value,
      nested: nestedValue.value,
      url: urlValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> EntityWithPropertyWithDefaultValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? EntityWithPropertyWithDefaultValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return EntityWithPropertyWithDefaultValueTemplate(
      parent: nil,
      int: int ?? mergedParent.int,
      nested: nested ?? mergedParent.nested,
      url: url ?? mergedParent.url
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityWithPropertyWithDefaultValueTemplate {
    let merged = try mergedWithParent(templates: templates)

    return EntityWithPropertyWithDefaultValueTemplate(
      parent: nil,
      int: merged.int,
      nested: merged.nested?.tryResolveParent(templates: templates),
      url: merged.url
    )
  }
}
