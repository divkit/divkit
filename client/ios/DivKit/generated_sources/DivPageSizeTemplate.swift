// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPageSizeTemplate: TemplateValue {
  public static let type: String = "percentage"
  public let parent: String? // at least 1 char
  public let pageWidth: Field<DivPercentageSizeTemplate>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        pageWidth: try dictionary.getOptionalField("page_width", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-page-size_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    pageWidth: Field<DivPercentageSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.pageWidth = pageWidth
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPageSizeTemplate?) -> DeserializationResult<DivPageSize> {
    let pageWidthValue = parent?.pageWidth?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      pageWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "page_width", error: $0) }
    )
    if case .noValue = pageWidthValue {
      errors.append(.requiredFieldIsMissing(field: "page_width"))
    }
    guard
      let pageWidthNonNil = pageWidthValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPageSize(
      pageWidth: pageWidthNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPageSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPageSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var pageWidthValue: DeserializationResult<DivPercentageSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "page_width":
        pageWidthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPercentageSizeTemplate.self).merged(with: pageWidthValue)
      case parent?.pageWidth?.link:
        pageWidthValue = pageWidthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPercentageSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      pageWidthValue = pageWidthValue.merged(with: parent.pageWidth?.resolveValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      pageWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "page_width", error: $0) }
    )
    if case .noValue = pageWidthValue {
      errors.append(.requiredFieldIsMissing(field: "page_width"))
    }
    guard
      let pageWidthNonNil = pageWidthValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPageSize(
      pageWidth: pageWidthNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPageSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivPageSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivPageSizeTemplate(
      parent: nil,
      pageWidth: pageWidth ?? mergedParent.pageWidth
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPageSizeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivPageSizeTemplate(
      parent: nil,
      pageWidth: try merged.pageWidth?.resolveParent(templates: templates)
    )
  }
}
