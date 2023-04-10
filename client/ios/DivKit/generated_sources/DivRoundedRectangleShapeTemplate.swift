// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivRoundedRectangleShapeTemplate: TemplateValue {
  public static let type: String = "rounded_rectangle"
  public let parent: String? // at least 1 char
  public let backgroundColor: Field<Expression<Color>>?
  public let cornerRadius: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(5))
  public let itemHeight: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(10))
  public let itemWidth: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(10))
  public let stroke: Field<DivStrokeTemplate>?

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      backgroundColor: try dictionary.getOptionalExpressionField("background_color", transform: Color.color(withHexString:)),
      cornerRadius: try dictionary.getOptionalField("corner_radius", templateToType: templateToType),
      itemHeight: try dictionary.getOptionalField("item_height", templateToType: templateToType),
      itemWidth: try dictionary.getOptionalField("item_width", templateToType: templateToType),
      stroke: try dictionary.getOptionalField("stroke", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    backgroundColor: Field<Expression<Color>>? = nil,
    cornerRadius: Field<DivFixedSizeTemplate>? = nil,
    itemHeight: Field<DivFixedSizeTemplate>? = nil,
    itemWidth: Field<DivFixedSizeTemplate>? = nil,
    stroke: Field<DivStrokeTemplate>? = nil
  ) {
    self.parent = parent
    self.backgroundColor = backgroundColor
    self.cornerRadius = cornerRadius
    self.itemHeight = itemHeight
    self.itemWidth = itemWidth
    self.stroke = stroke
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivRoundedRectangleShapeTemplate?) -> DeserializationResult<DivRoundedRectangleShape> {
    let backgroundColorValue = parent?.backgroundColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.backgroundColorValidator) ?? .noValue
    let cornerRadiusValue = parent?.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator, useOnlyLinks: true) ?? .noValue
    let itemHeightValue = parent?.itemHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.itemHeightValidator, useOnlyLinks: true) ?? .noValue
    let itemWidthValue = parent?.itemWidth?.resolveOptionalValue(context: context, validator: ResolvedValue.itemWidthValidator, useOnlyLinks: true) ?? .noValue
    let strokeValue = parent?.stroke?.resolveOptionalValue(context: context, validator: ResolvedValue.strokeValidator, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      backgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "background_color", error: $0) },
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      itemHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_height", error: $0) },
      itemWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_width", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivRoundedRectangleShape(
      backgroundColor: backgroundColorValue.value,
      cornerRadius: cornerRadiusValue.value,
      itemHeight: itemHeightValue.value,
      itemWidth: itemWidthValue.value,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRoundedRectangleShapeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRoundedRectangleShape> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var backgroundColorValue: DeserializationResult<Expression<Color>> = parent?.backgroundColor?.value() ?? .noValue
    var cornerRadiusValue: DeserializationResult<DivFixedSize> = .noValue
    var itemHeightValue: DeserializationResult<DivFixedSize> = .noValue
    var itemWidthValue: DeserializationResult<DivFixedSize> = .noValue
    var strokeValue: DeserializationResult<DivStroke> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "background_color":
        backgroundColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.backgroundColorValidator).merged(with: backgroundColorValue)
      case "corner_radius":
        cornerRadiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.cornerRadiusValidator, type: DivFixedSizeTemplate.self).merged(with: cornerRadiusValue)
      case "item_height":
        itemHeightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemHeightValidator, type: DivFixedSizeTemplate.self).merged(with: itemHeightValue)
      case "item_width":
        itemWidthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemWidthValidator, type: DivFixedSizeTemplate.self).merged(with: itemWidthValue)
      case "stroke":
        strokeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.strokeValidator, type: DivStrokeTemplate.self).merged(with: strokeValue)
      case parent?.backgroundColor?.link:
        backgroundColorValue = backgroundColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.backgroundColorValidator))
      case parent?.cornerRadius?.link:
        cornerRadiusValue = cornerRadiusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.cornerRadiusValidator, type: DivFixedSizeTemplate.self))
      case parent?.itemHeight?.link:
        itemHeightValue = itemHeightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemHeightValidator, type: DivFixedSizeTemplate.self))
      case parent?.itemWidth?.link:
        itemWidthValue = itemWidthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemWidthValidator, type: DivFixedSizeTemplate.self))
      case parent?.stroke?.link:
        strokeValue = strokeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.strokeValidator, type: DivStrokeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      cornerRadiusValue = cornerRadiusValue.merged(with: parent.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator, useOnlyLinks: true))
      itemHeightValue = itemHeightValue.merged(with: parent.itemHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.itemHeightValidator, useOnlyLinks: true))
      itemWidthValue = itemWidthValue.merged(with: parent.itemWidth?.resolveOptionalValue(context: context, validator: ResolvedValue.itemWidthValidator, useOnlyLinks: true))
      strokeValue = strokeValue.merged(with: parent.stroke?.resolveOptionalValue(context: context, validator: ResolvedValue.strokeValidator, useOnlyLinks: true))
    }
    let errors = mergeErrors(
      backgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "background_color", error: $0) },
      cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
      itemHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_height", error: $0) },
      itemWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_width", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivRoundedRectangleShape(
      backgroundColor: backgroundColorValue.value,
      cornerRadius: cornerRadiusValue.value,
      itemHeight: itemHeightValue.value,
      itemWidth: itemWidthValue.value,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivRoundedRectangleShapeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivRoundedRectangleShapeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivRoundedRectangleShapeTemplate(
      parent: nil,
      backgroundColor: backgroundColor ?? mergedParent.backgroundColor,
      cornerRadius: cornerRadius ?? mergedParent.cornerRadius,
      itemHeight: itemHeight ?? mergedParent.itemHeight,
      itemWidth: itemWidth ?? mergedParent.itemWidth,
      stroke: stroke ?? mergedParent.stroke
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivRoundedRectangleShapeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivRoundedRectangleShapeTemplate(
      parent: nil,
      backgroundColor: merged.backgroundColor,
      cornerRadius: merged.cornerRadius?.tryResolveParent(templates: templates),
      itemHeight: merged.itemHeight?.tryResolveParent(templates: templates),
      itemWidth: merged.itemWidth?.tryResolveParent(templates: templates),
      stroke: merged.stroke?.tryResolveParent(templates: templates)
    )
  }
}
