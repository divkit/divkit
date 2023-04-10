// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivNeighbourPageSizeTemplate: TemplateValue {
  public static let type: String = "fixed"
  public let parent: String? // at least 1 char
  public let neighbourPageWidth: Field<DivFixedSizeTemplate>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        neighbourPageWidth: try dictionary.getOptionalField("neighbour_page_width", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-neighbour-page-size_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    neighbourPageWidth: Field<DivFixedSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.neighbourPageWidth = neighbourPageWidth
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivNeighbourPageSizeTemplate?) -> DeserializationResult<DivNeighbourPageSize> {
    let neighbourPageWidthValue = parent?.neighbourPageWidth?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      neighbourPageWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "neighbour_page_width", error: $0) }
    )
    if case .noValue = neighbourPageWidthValue {
      errors.append(.requiredFieldIsMissing(field: "neighbour_page_width"))
    }
    guard
      let neighbourPageWidthNonNil = neighbourPageWidthValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivNeighbourPageSize(
      neighbourPageWidth: neighbourPageWidthNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivNeighbourPageSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivNeighbourPageSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var neighbourPageWidthValue: DeserializationResult<DivFixedSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "neighbour_page_width":
        neighbourPageWidthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: neighbourPageWidthValue)
      case parent?.neighbourPageWidth?.link:
        neighbourPageWidthValue = neighbourPageWidthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      neighbourPageWidthValue = neighbourPageWidthValue.merged(with: parent.neighbourPageWidth?.resolveValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      neighbourPageWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "neighbour_page_width", error: $0) }
    )
    if case .noValue = neighbourPageWidthValue {
      errors.append(.requiredFieldIsMissing(field: "neighbour_page_width"))
    }
    guard
      let neighbourPageWidthNonNil = neighbourPageWidthValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivNeighbourPageSize(
      neighbourPageWidth: neighbourPageWidthNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivNeighbourPageSizeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivNeighbourPageSizeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivNeighbourPageSizeTemplate(
      parent: nil,
      neighbourPageWidth: neighbourPageWidth ?? mergedParent.neighbourPageWidth
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivNeighbourPageSizeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivNeighbourPageSizeTemplate(
      parent: nil,
      neighbourPageWidth: try merged.neighbourPageWidth?.resolveParent(templates: templates)
    )
  }
}
