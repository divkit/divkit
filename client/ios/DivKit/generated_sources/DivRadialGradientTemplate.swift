// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivRadialGradientTemplate: TemplateValue {
  public static let type: String = "radial_gradient"
  public let parent: String? // at least 1 char
  public let centerX: Field<DivRadialGradientCenterTemplate>? // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let centerY: Field<DivRadialGradientCenterTemplate>? // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let colors: Field<[Expression<Color>]>? // at least 2 elements
  public let radius: Field<DivRadialGradientRadiusTemplate>? // default value: .divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius(value: .value(.farthestCorner)))

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        centerX: try dictionary.getOptionalField("center_x", templateToType: templateToType),
        centerY: try dictionary.getOptionalField("center_y", templateToType: templateToType),
        colors: try dictionary.getOptionalExpressionArray("colors", transform: Color.color(withHexString:)),
        radius: try dictionary.getOptionalField("radius", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-radial-gradient_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    centerX: Field<DivRadialGradientCenterTemplate>? = nil,
    centerY: Field<DivRadialGradientCenterTemplate>? = nil,
    colors: Field<[Expression<Color>]>? = nil,
    radius: Field<DivRadialGradientRadiusTemplate>? = nil
  ) {
    self.parent = parent
    self.centerX = centerX
    self.centerY = centerY
    self.colors = colors
    self.radius = radius
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivRadialGradientTemplate?) -> DeserializationResult<DivRadialGradient> {
    let centerXValue = parent?.centerX?.resolveOptionalValue(context: context, validator: ResolvedValue.centerXValidator, useOnlyLinks: true) ?? .noValue
    let centerYValue = parent?.centerY?.resolveOptionalValue(context: context, validator: ResolvedValue.centerYValidator, useOnlyLinks: true) ?? .noValue
    let colorsValue = parent?.colors?.resolveValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) ?? .noValue
    let radiusValue = parent?.radius?.resolveOptionalValue(context: context, validator: ResolvedValue.radiusValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      centerXValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_x", error: $0) },
      centerYValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_y", error: $0) },
      colorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "colors", error: $0) },
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) }
    )
    if case .noValue = colorsValue {
      errors.append(.requiredFieldIsMissing(field: "colors"))
    }
    guard
      let colorsNonNil = colorsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivRadialGradient(
      centerX: centerXValue.value,
      centerY: centerYValue.value,
      colors: colorsNonNil,
      radius: radiusValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRadialGradientTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradient> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var centerXValue: DeserializationResult<DivRadialGradientCenter> = .noValue
    var centerYValue: DeserializationResult<DivRadialGradientCenter> = .noValue
    var colorsValue: DeserializationResult<[Expression<Color>]> = parent?.colors?.value() ?? .noValue
    var radiusValue: DeserializationResult<DivRadialGradientRadius> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "center_x":
        centerXValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.centerXValidator, type: DivRadialGradientCenterTemplate.self).merged(with: centerXValue)
      case "center_y":
        centerYValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.centerYValidator, type: DivRadialGradientCenterTemplate.self).merged(with: centerYValue)
      case "colors":
        colorsValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator).merged(with: colorsValue)
      case "radius":
        radiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.radiusValidator, type: DivRadialGradientRadiusTemplate.self).merged(with: radiusValue)
      case parent?.centerX?.link:
        centerXValue = centerXValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.centerXValidator, type: DivRadialGradientCenterTemplate.self))
      case parent?.centerY?.link:
        centerYValue = centerYValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.centerYValidator, type: DivRadialGradientCenterTemplate.self))
      case parent?.colors?.link:
        colorsValue = colorsValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator))
      case parent?.radius?.link:
        radiusValue = radiusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.radiusValidator, type: DivRadialGradientRadiusTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      centerXValue = centerXValue.merged(with: parent.centerX?.resolveOptionalValue(context: context, validator: ResolvedValue.centerXValidator, useOnlyLinks: true))
      centerYValue = centerYValue.merged(with: parent.centerY?.resolveOptionalValue(context: context, validator: ResolvedValue.centerYValidator, useOnlyLinks: true))
      radiusValue = radiusValue.merged(with: parent.radius?.resolveOptionalValue(context: context, validator: ResolvedValue.radiusValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      centerXValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_x", error: $0) },
      centerYValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_y", error: $0) },
      colorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "colors", error: $0) },
      radiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "radius", error: $0) }
    )
    if case .noValue = colorsValue {
      errors.append(.requiredFieldIsMissing(field: "colors"))
    }
    guard
      let colorsNonNil = colorsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivRadialGradient(
      centerX: centerXValue.value,
      centerY: centerYValue.value,
      colors: colorsNonNil,
      radius: radiusValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivRadialGradientTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivRadialGradientTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivRadialGradientTemplate(
      parent: nil,
      centerX: centerX ?? mergedParent.centerX,
      centerY: centerY ?? mergedParent.centerY,
      colors: colors ?? mergedParent.colors,
      radius: radius ?? mergedParent.radius
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivRadialGradientTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivRadialGradientTemplate(
      parent: nil,
      centerX: merged.centerX?.tryResolveParent(templates: templates),
      centerY: merged.centerY?.tryResolveParent(templates: templates),
      colors: merged.colors,
      radius: merged.radius?.tryResolveParent(templates: templates)
    )
  }
}
