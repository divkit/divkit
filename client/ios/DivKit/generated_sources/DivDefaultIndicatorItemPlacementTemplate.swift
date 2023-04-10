// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivDefaultIndicatorItemPlacementTemplate: TemplateValue {
  public static let type: String = "default"
  public let parent: String? // at least 1 char
  public let spaceBetweenCenters: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(15))

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      spaceBetweenCenters: try dictionary.getOptionalField("space_between_centers", templateToType: templateToType)
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
    let spaceBetweenCentersValue = parent?.spaceBetweenCenters?.resolveOptionalValue(context: context, validator: ResolvedValue.spaceBetweenCentersValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      spaceBetweenCentersValue.errorsOrWarnings?.map { .nestedObjectError(field: "space_between_centers", error: $0) }
    )
    let result = DivDefaultIndicatorItemPlacement(
      spaceBetweenCenters: spaceBetweenCentersValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDefaultIndicatorItemPlacementTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivDefaultIndicatorItemPlacement> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var spaceBetweenCentersValue: DeserializationResult<DivFixedSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "space_between_centers":
        spaceBetweenCentersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.spaceBetweenCentersValidator, type: DivFixedSizeTemplate.self).merged(with: spaceBetweenCentersValue)
      case parent?.spaceBetweenCenters?.link:
        spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.spaceBetweenCentersValidator, type: DivFixedSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      spaceBetweenCentersValue = spaceBetweenCentersValue.merged(with: parent.spaceBetweenCenters?.resolveOptionalValue(context: context, validator: ResolvedValue.spaceBetweenCentersValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      spaceBetweenCentersValue.errorsOrWarnings?.map { .nestedObjectError(field: "space_between_centers", error: $0) }
    )
    let result = DivDefaultIndicatorItemPlacement(
      spaceBetweenCenters: spaceBetweenCentersValue.value
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
