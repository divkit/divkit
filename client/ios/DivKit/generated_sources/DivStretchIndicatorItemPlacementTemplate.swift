// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivStretchIndicatorItemPlacementTemplate: TemplateValue {
  public static let type: String = "stretch"
  public let parent: String? // at least 1 char
  public let itemSpacing: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(5))
  public let maxVisibleItems: Field<Expression<Int>>? // constraint: number > 0; default value: 10

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      itemSpacing: try dictionary.getOptionalField("item_spacing", templateToType: templateToType),
      maxVisibleItems: try dictionary.getOptionalExpressionField("max_visible_items")
    )
  }

  init(
    parent: String?,
    itemSpacing: Field<DivFixedSizeTemplate>? = nil,
    maxVisibleItems: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.itemSpacing = itemSpacing
    self.maxVisibleItems = maxVisibleItems
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivStretchIndicatorItemPlacementTemplate?) -> DeserializationResult<DivStretchIndicatorItemPlacement> {
    let itemSpacingValue = parent?.itemSpacing?.resolveOptionalValue(context: context, validator: ResolvedValue.itemSpacingValidator, useOnlyLinks: true) ?? .noValue
    let maxVisibleItemsValue = parent?.maxVisibleItems?.resolveOptionalValue(context: context, validator: ResolvedValue.maxVisibleItemsValidator) ?? .noValue
    let errors = mergeErrors(
      itemSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_spacing", error: $0) },
      maxVisibleItemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_visible_items", error: $0) }
    )
    let result = DivStretchIndicatorItemPlacement(
      itemSpacing: itemSpacingValue.value,
      maxVisibleItems: maxVisibleItemsValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivStretchIndicatorItemPlacementTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivStretchIndicatorItemPlacement> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemSpacingValue: DeserializationResult<DivFixedSize> = .noValue
    var maxVisibleItemsValue: DeserializationResult<Expression<Int>> = parent?.maxVisibleItems?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "item_spacing":
        itemSpacingValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemSpacingValidator, type: DivFixedSizeTemplate.self).merged(with: itemSpacingValue)
      case "max_visible_items":
        maxVisibleItemsValue = deserialize(__dictValue, validator: ResolvedValue.maxVisibleItemsValidator).merged(with: maxVisibleItemsValue)
      case parent?.itemSpacing?.link:
        itemSpacingValue = itemSpacingValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemSpacingValidator, type: DivFixedSizeTemplate.self))
      case parent?.maxVisibleItems?.link:
        maxVisibleItemsValue = maxVisibleItemsValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.maxVisibleItemsValidator))
      default: break
      }
    }
    if let parent = parent {
      itemSpacingValue = itemSpacingValue.merged(with: parent.itemSpacing?.resolveOptionalValue(context: context, validator: ResolvedValue.itemSpacingValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      itemSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_spacing", error: $0) },
      maxVisibleItemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_visible_items", error: $0) }
    )
    let result = DivStretchIndicatorItemPlacement(
      itemSpacing: itemSpacingValue.value,
      maxVisibleItems: maxVisibleItemsValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivStretchIndicatorItemPlacementTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivStretchIndicatorItemPlacementTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivStretchIndicatorItemPlacementTemplate(
      parent: nil,
      itemSpacing: itemSpacing ?? mergedParent.itemSpacing,
      maxVisibleItems: maxVisibleItems ?? mergedParent.maxVisibleItems
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivStretchIndicatorItemPlacementTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivStretchIndicatorItemPlacementTemplate(
      parent: nil,
      itemSpacing: merged.itemSpacing?.tryResolveParent(templates: templates),
      maxVisibleItems: merged.maxVisibleItems
    )
  }
}
