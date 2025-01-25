// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDefaultIndicatorItemPlacementTemplate: TemplateValue, Sendable {
  public static let type: String = "default"
  public let parent: String?
  public let spaceBetweenCenters: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(15))

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      spaceBetweenCenters: dictionary.getOptionalField("space_between_centers", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    spaceBetweenCenters: Field<DivFixedSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.spaceBetweenCenters = spaceBetweenCenters
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivDefaultIndicatorItemPlacementTemplate?) -> DeserializationResult<DivDefaultIndicatorItemPlacement> {
    let spaceBetweenCentersValue = { parent?.spaceBetweenCenters?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let errors = mergeErrors(
      spaceBetweenCentersValue.errorsOrWarnings?.map { .nestedObjectError(field: "space_between_centers", error: $0) }
    )
    let result = DivDefaultIndicatorItemPlacement(
      spaceBetweenCenters: { spaceBetweenCentersValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDefaultIndicatorItemPlacementTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDefaultIndicatorItemPlacement> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var spaceBetweenCentersValue: DeserializationResult<DivFixedSize> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "space_between_centers" {
           spaceBetweenCentersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: spaceBetweenCentersValue)
          }
        }()
        _ = {
         if key == parent?.spaceBetweenCenters?.link {
           spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: { parent.spaceBetweenCenters?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    let errors = mergeErrors(
      spaceBetweenCentersValue.errorsOrWarnings?.map { .nestedObjectError(field: "space_between_centers", error: $0) }
    )
    let result = DivDefaultIndicatorItemPlacement(
      spaceBetweenCenters: { spaceBetweenCentersValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivDefaultIndicatorItemPlacementTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivDefaultIndicatorItemPlacementTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivDefaultIndicatorItemPlacementTemplate(
      parent: nil,
      spaceBetweenCenters: spaceBetweenCenters ?? mergedParent.spaceBetweenCenters
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivDefaultIndicatorItemPlacementTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivDefaultIndicatorItemPlacementTemplate(
      parent: nil,
      spaceBetweenCenters: merged.spaceBetweenCenters?.tryResolveParent(templates: templates)
    )
  }
}
