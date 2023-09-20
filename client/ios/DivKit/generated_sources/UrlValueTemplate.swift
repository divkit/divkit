// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class UrlValueTemplate: TemplateValue {
  public static let type: String = "url"
  public let parent: String? // at least 1 char
  public let value: Field<Expression<URL>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        value: try dictionary.getOptionalExpressionField("value", transform: URL.init(string:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "url_value_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    value: Field<Expression<URL>>? = nil
  ) {
    self.parent = parent
    self.value = value
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: UrlValueTemplate?) -> DeserializationResult<UrlValue> {
    let valueValue = parent?.value?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
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
    let result = UrlValue(
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: UrlValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<UrlValue> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var valueValue: DeserializationResult<Expression<URL>> = parent?.value?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "value":
        valueValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: valueValue)
      case parent?.value?.link:
        valueValue = valueValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
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
    let result = UrlValue(
      value: valueNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> UrlValueTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? UrlValueTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return UrlValueTemplate(
      parent: nil,
      value: value ?? mergedParent.value
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> UrlValueTemplate {
    return try mergedWithParent(templates: templates)
  }
}
