// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCircleShapeTemplate: TemplateValue {
  public static let type: String = "circle"
  public let parent: String?
  public let backgroundColor: Field<Expression<Color>>?
  public let radius: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(10))
  public let stroke: Field<DivStrokeTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      backgroundColor: dictionary.getOptionalExpressionField("background_color", transform: Color.color(withHexString:)),
      radius: dictionary.getOptionalField("radius", templateToType: templateToType),
      stroke: dictionary.getOptionalField("stroke", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    backgroundColor: Field<Expression<Color>>? = nil,
    radius: Field<DivFixedSizeTemplate>? = nil,
    stroke: Field<DivStrokeTemplate>? = nil
  ) {
    self.parent = parent
    self.backgroundColor = backgroundColor
    self.radius = radius
    self.stroke = stroke
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCircleShapeTemplate?) -> DeserializationResult<DivCircleShape> {
    let backgroundColorValue = parent?.backgroundColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let radiusValue = parent?.radius?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let strokeValue = parent?.stroke?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let errors = mergeErrors(
      backgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "background_color", error: $0) },
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivCircleShape(
      backgroundColor: backgroundColorValue.value,
      radius: radiusValue.value,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCircleShapeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCircleShape> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var backgroundColorValue: DeserializationResult<Expression<Color>> = parent?.backgroundColor?.value() ?? .noValue
    var radiusValue: DeserializationResult<DivFixedSize> = .noValue
    var strokeValue: DeserializationResult<DivStroke> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "background_color":
        backgroundColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: backgroundColorValue)
      case "radius":
        radiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: radiusValue)
      case "stroke":
        strokeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivStrokeTemplate.self).merged(with: strokeValue)
      case parent?.backgroundColor?.link:
        backgroundColorValue = backgroundColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
      case parent?.radius?.link:
        radiusValue = radiusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
      case parent?.stroke?.link:
        strokeValue = strokeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivStrokeTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      radiusValue = radiusValue.merged(with: { parent.radius?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      strokeValue = strokeValue.merged(with: { parent.stroke?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    let errors = mergeErrors(
      backgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "background_color", error: $0) },
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) },
      strokeValue.errorsOrWarnings?.map { .nestedObjectError(field: "stroke", error: $0) }
    )
    let result = DivCircleShape(
      backgroundColor: backgroundColorValue.value,
      radius: radiusValue.value,
      stroke: strokeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivCircleShapeTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivCircleShapeTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivCircleShapeTemplate(
      parent: nil,
      backgroundColor: backgroundColor ?? mergedParent.backgroundColor,
      radius: radius ?? mergedParent.radius,
      stroke: stroke ?? mergedParent.stroke
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCircleShapeTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivCircleShapeTemplate(
      parent: nil,
      backgroundColor: merged.backgroundColor,
      radius: merged.radius?.tryResolveParent(templates: templates),
      stroke: merged.stroke?.tryResolveParent(templates: templates)
    )
  }
}
