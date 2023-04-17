// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoDataStreamTemplate: TemplateValue {
  public static let type: String = "stream"
  public let parent: String? // at least 1 char
  public let url: Field<Expression<URL>>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-video-data-stream_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    url: Field<Expression<URL>>? = nil
  ) {
    self.parent = parent
    self.url = url
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivVideoDataStreamTemplate?) -> DeserializationResult<DivVideoDataStream> {
    let urlValue = parent?.url?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
    var errors = mergeErrors(
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = urlValue {
      errors.append(.requiredFieldIsMissing(field: "url"))
    }
    guard
      let urlNonNil = urlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideoDataStream(
      url: urlNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVideoDataStreamTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVideoDataStream> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "url":
        urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
      case parent?.url?.link:
        urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
      default: break
      }
    }
    var errors = mergeErrors(
      urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
    )
    if case .noValue = urlValue {
      errors.append(.requiredFieldIsMissing(field: "url"))
    }
    guard
      let urlNonNil = urlValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivVideoDataStream(
      url: urlNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivVideoDataStreamTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivVideoDataStreamTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivVideoDataStreamTemplate(
      parent: nil,
      url: url ?? mergedParent.url
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVideoDataStreamTemplate {
    return try mergedWithParent(templates: templates)
  }
}
