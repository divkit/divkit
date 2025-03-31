// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRadialGradientTemplate: TemplateValue, Sendable {
  public final class ColorPointTemplate: TemplateValue, Sendable {
    public let color: Field<Expression<Color>>?
    public let position: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
        position: dictionary.getOptionalExpressionField("position")
      )
    }

    init(
      color: Field<Expression<Color>>? = nil,
      position: Field<Expression<Double>>? = nil
    ) {
      self.color = color
      self.position = position
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ColorPointTemplate?) -> DeserializationResult<DivRadialGradient.ColorPoint> {
      let colorValue = { parent?.color?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
      let positionValue = { parent?.position?.resolveValue(context: context, validator: ResolvedValue.positionValidator) ?? .noValue }()
      var errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
      )
      if case .noValue = colorValue {
        errors.append(.requiredFieldIsMissing(field: "color"))
      }
      if case .noValue = positionValue {
        errors.append(.requiredFieldIsMissing(field: "position"))
      }
      guard
        let colorNonNil = colorValue.value,
        let positionNonNil = positionValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivRadialGradient.ColorPoint(
        color: { colorNonNil }(),
        position: { positionNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ColorPointTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradient.ColorPoint> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
      var positionValue: DeserializationResult<Expression<Double>> = { parent?.position?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "color" {
             colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
            }
          }()
          _ = {
            if key == "position" {
             positionValue = deserialize(__dictValue, validator: ResolvedValue.positionValidator).merged(with: positionValue)
            }
          }()
          _ = {
           if key == parent?.color?.link {
             colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
            }
          }()
          _ = {
           if key == parent?.position?.link {
             positionValue = positionValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.positionValidator) })
            }
          }()
        }
      }()
      var errors = mergeErrors(
        colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
        positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
      )
      if case .noValue = colorValue {
        errors.append(.requiredFieldIsMissing(field: "color"))
      }
      if case .noValue = positionValue {
        errors.append(.requiredFieldIsMissing(field: "position"))
      }
      guard
        let colorNonNil = colorValue.value,
        let positionNonNil = positionValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivRadialGradient.ColorPoint(
        color: { colorNonNil }(),
        position: { positionNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ColorPointTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ColorPointTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "radial_gradient"
  public let parent: String?
  public let centerX: Field<DivRadialGradientCenterTemplate>? // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let centerY: Field<DivRadialGradientCenterTemplate>? // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let colorMap: Field<[ColorPointTemplate]>? // at least 2 elements
  public let colors: Field<[Expression<Color>]>? // at least 2 elements
  public let radius: Field<DivRadialGradientRadiusTemplate>? // default value: .divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius(value: .value(.farthestCorner)))

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      centerX: dictionary.getOptionalField("center_x", templateToType: templateToType),
      centerY: dictionary.getOptionalField("center_y", templateToType: templateToType),
      colorMap: dictionary.getOptionalArray("color_map", templateToType: templateToType),
      colors: dictionary.getOptionalExpressionArray("colors", transform: Color.color(withHexString:)),
      radius: dictionary.getOptionalField("radius", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    centerX: Field<DivRadialGradientCenterTemplate>? = nil,
    centerY: Field<DivRadialGradientCenterTemplate>? = nil,
    colorMap: Field<[ColorPointTemplate]>? = nil,
    colors: Field<[Expression<Color>]>? = nil,
    radius: Field<DivRadialGradientRadiusTemplate>? = nil
  ) {
    self.parent = parent
    self.centerX = centerX
    self.centerY = centerY
    self.colorMap = colorMap
    self.colors = colors
    self.radius = radius
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivRadialGradientTemplate?) -> DeserializationResult<DivRadialGradient> {
    let centerXValue = { parent?.centerX?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let centerYValue = { parent?.centerY?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let colorMapValue = { parent?.colorMap?.resolveOptionalValue(context: context, validator: ResolvedValue.colorMapValidator, useOnlyLinks: true) ?? .noValue }()
    let colorsValue = { parent?.colors?.resolveValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) ?? .noValue }()
    let radiusValue = { parent?.radius?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      centerXValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_x", error: $0) },
      centerYValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_y", error: $0) },
      colorMapValue.errorsOrWarnings?.map { .nestedObjectError(field: "color_map", error: $0) },
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
      centerX: { centerXValue.value }(),
      centerY: { centerYValue.value }(),
      colorMap: { colorMapValue.value }(),
      colors: { colorsNonNil }(),
      radius: { radiusValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivRadialGradientTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradient> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var centerXValue: DeserializationResult<DivRadialGradientCenter> = .noValue
    var centerYValue: DeserializationResult<DivRadialGradientCenter> = .noValue
    var colorMapValue: DeserializationResult<[DivRadialGradient.ColorPoint]> = .noValue
    var colorsValue: DeserializationResult<[Expression<Color>]> = { parent?.colors?.value() ?? .noValue }()
    var radiusValue: DeserializationResult<DivRadialGradientRadius> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "center_x" {
           centerXValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRadialGradientCenterTemplate.self).merged(with: centerXValue)
          }
        }()
        _ = {
          if key == "center_y" {
           centerYValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRadialGradientCenterTemplate.self).merged(with: centerYValue)
          }
        }()
        _ = {
          if key == "color_map" {
           colorMapValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.colorMapValidator, type: DivRadialGradientTemplate.ColorPointTemplate.self).merged(with: colorMapValue)
          }
        }()
        _ = {
          if key == "colors" {
           colorsValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator).merged(with: colorsValue)
          }
        }()
        _ = {
          if key == "radius" {
           radiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRadialGradientRadiusTemplate.self).merged(with: radiusValue)
          }
        }()
        _ = {
         if key == parent?.centerX?.link {
           centerXValue = centerXValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRadialGradientCenterTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.centerY?.link {
           centerYValue = centerYValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRadialGradientCenterTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.colorMap?.link {
           colorMapValue = colorMapValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.colorMapValidator, type: DivRadialGradientTemplate.ColorPointTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.colors?.link {
           colorsValue = colorsValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) })
          }
        }()
        _ = {
         if key == parent?.radius?.link {
           radiusValue = radiusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivRadialGradientRadiusTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { centerXValue = centerXValue.merged(with: { parent.centerX?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { centerYValue = centerYValue.merged(with: { parent.centerY?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { colorMapValue = colorMapValue.merged(with: { parent.colorMap?.resolveOptionalValue(context: context, validator: ResolvedValue.colorMapValidator, useOnlyLinks: true) }) }()
      _ = { radiusValue = radiusValue.merged(with: { parent.radius?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      centerXValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_x", error: $0) },
      centerYValue.errorsOrWarnings?.map { .nestedObjectError(field: "center_y", error: $0) },
      colorMapValue.errorsOrWarnings?.map { .nestedObjectError(field: "color_map", error: $0) },
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
      centerX: { centerXValue.value }(),
      centerY: { centerYValue.value }(),
      colorMap: { colorMapValue.value }(),
      colors: { colorsNonNil }(),
      radius: { radiusValue.value }()
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
      colorMap: colorMap ?? mergedParent.colorMap,
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
      colorMap: merged.colorMap?.tryResolveParent(templates: templates),
      colors: merged.colors,
      radius: merged.radius?.tryResolveParent(templates: templates)
    )
  }
}
