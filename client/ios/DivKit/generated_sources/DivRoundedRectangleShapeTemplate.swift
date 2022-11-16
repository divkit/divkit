// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivRoundedRectangleShapeTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "rounded_rectangle"
  public let parent: String? // at least 1 char
  public let cornerRadius: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(5))
  public let itemHeight: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(10))
  public let itemWidth: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(10))

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      cornerRadius: try dictionary.getOptionalField("corner_radius", templateToType: templateToType),
      itemHeight: try dictionary.getOptionalField("item_height", templateToType: templateToType),
      itemWidth: try dictionary.getOptionalField("item_width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    cornerRadius: Field<DivFixedSizeTemplate>? = nil,
    itemHeight: Field<DivFixedSizeTemplate>? = nil,
    itemWidth: Field<DivFixedSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.cornerRadius = cornerRadius
    self.itemHeight = itemHeight
    self.itemWidth = itemWidth
  }

  private static func resolveOnlyLinks(context: Context, parent: DivRoundedRectangleShapeTemplate?) -> DeserializationResult<DivRoundedRectangleShape> {
    let cornerRadiusValue = parent?.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator, useOnlyLinks: true) ?? .noValue
    let itemHeightValue = parent?.itemHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.itemHeightValidator, useOnlyLinks: true) ?? .noValue
    let itemWidthValue = parent?.itemWidth?.resolveOptionalValue(context: context, validator: ResolvedValue.itemWidthValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      itemHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_height", error: $0) },
      itemWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_width", error: $0) }
    )
    let result = DivRoundedRectangleShape(
      cornerRadius: cornerRadiusValue.value,
      itemHeight: itemHeightValue.value,
      itemWidth: itemWidthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivRoundedRectangleShapeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRoundedRectangleShape> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var cornerRadiusValue: DeserializationResult<DivFixedSize> = .noValue
    var itemHeightValue: DeserializationResult<DivFixedSize> = .noValue
    var itemWidthValue: DeserializationResult<DivFixedSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "corner_radius":
        cornerRadiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.cornerRadiusValidator, type: DivFixedSizeTemplate.self).merged(with: cornerRadiusValue)
      case "item_height":
        itemHeightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemHeightValidator, type: DivFixedSizeTemplate.self).merged(with: itemHeightValue)
      case "item_width":
        itemWidthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemWidthValidator, type: DivFixedSizeTemplate.self).merged(with: itemWidthValue)
      case parent?.cornerRadius?.link:
        cornerRadiusValue = cornerRadiusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.cornerRadiusValidator, type: DivFixedSizeTemplate.self))
      case parent?.itemHeight?.link:
        itemHeightValue = itemHeightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemHeightValidator, type: DivFixedSizeTemplate.self))
      case parent?.itemWidth?.link:
        itemWidthValue = itemWidthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemWidthValidator, type: DivFixedSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      cornerRadiusValue = cornerRadiusValue.merged(with: parent.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator, useOnlyLinks: true))
      itemHeightValue = itemHeightValue.merged(with: parent.itemHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.itemHeightValidator, useOnlyLinks: true))
      itemWidthValue = itemWidthValue.merged(with: parent.itemWidth?.resolveOptionalValue(context: context, validator: ResolvedValue.itemWidthValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      itemHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_height", error: $0) },
      itemWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_width", error: $0) }
    )
    let result = DivRoundedRectangleShape(
      cornerRadius: cornerRadiusValue.value,
      itemHeight: itemHeightValue.value,
      itemWidth: itemWidthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivRoundedRectangleShapeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivRoundedRectangleShapeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivRoundedRectangleShapeTemplate(
      parent: nil,
      cornerRadius: cornerRadius ?? mergedParent.cornerRadius,
      itemHeight: itemHeight ?? mergedParent.itemHeight,
      itemWidth: itemWidth ?? mergedParent.itemWidth
    )
  }

  public func resolveParent(templates: Templates) throws -> DivRoundedRectangleShapeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivRoundedRectangleShapeTemplate(
      parent: nil,
      cornerRadius: merged.cornerRadius?.tryResolveParent(templates: templates),
      itemHeight: merged.itemHeight?.tryResolveParent(templates: templates),
      itemWidth: merged.itemWidth?.tryResolveParent(templates: templates)
    )
  }
}
