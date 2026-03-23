// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivNeighbourPageSizeTemplate: TemplateValue, Sendable {
  public static let type: String = "fixed"
  public let parent: String?
  public let neighbourPageWidth: Field<DivFixedSizeTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      neighbourPageWidth: dictionary.getOptionalField("neighbour_page_width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    neighbourPageWidth: Field<DivFixedSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.neighbourPageWidth = neighbourPageWidth
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivNeighbourPageSizeTemplate?) -> DeserializationResult<DivNeighbourPageSize> {
    let neighbourPageWidthValue = { parent?.neighbourPageWidth?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
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
      neighbourPageWidth: { neighbourPageWidthNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivNeighbourPageSizeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivNeighbourPageSize> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var neighbourPageWidthValue: DeserializationResult<DivFixedSize> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "neighbour_page_width" {
           neighbourPageWidthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: neighbourPageWidthValue)
          }
        }()
        _ = {
         if key == parent?.neighbourPageWidth?.link {
           neighbourPageWidthValue = neighbourPageWidthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { neighbourPageWidthValue = neighbourPageWidthValue.merged(with: { parent.neighbourPageWidth?.resolveValue(context: context, useOnlyLinks: true) }) }()
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
      neighbourPageWidth: { neighbourPageWidthNonNil }()
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
