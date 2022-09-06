// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivRadialGradientTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "radial_gradient"
  public let parent: String? // at least 1 char
  public let centerX: Field<Expression<Double>>? // default value: 0.5
  public let centerY: Field<Expression<Double>>? // default value: 0.5
  public let colors: Field<[Expression<Color>]>? // at least 2 elements
  public let radius: Field<Expression<Int>>? // constraint: number > 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        centerX: try dictionary.getOptionalExpressionField("center_x"),
        centerY: try dictionary.getOptionalExpressionField("center_y"),
        colors: try dictionary.getOptionalExpressionArray("colors", transform: Color.color(withHexString:)),
        radius: try dictionary.getOptionalExpressionField("radius")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-radial-gradient_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    centerX: Field<Expression<Double>>? = nil,
    centerY: Field<Expression<Double>>? = nil,
    colors: Field<[Expression<Color>]>? = nil,
    radius: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.centerX = centerX
    self.centerY = centerY
    self.colors = colors
    self.radius = radius
  }

  private static func resolveOnlyLinks(context: Context, parent: DivRadialGradientTemplate?) -> DeserializationResult<DivRadialGradient> {
    let centerXValue = parent?.centerX?.resolveOptionalValue(context: context) ?? .noValue
    let centerYValue = parent?.centerY?.resolveOptionalValue(context: context) ?? .noValue
    let colorsValue = parent?.colors?.resolveValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) ?? .noValue
    let radiusValue = parent?.radius?.resolveValue(context: context, validator: ResolvedValue.radiusValidator) ?? .noValue
    var errors = mergeErrors(
      centerXValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "center_x", level: .warning)) },
      centerYValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "center_y", level: .warning)) },
      colorsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "colors", level: .error)) },
      radiusValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "radius", level: .error)) }
    )
    if case .noValue = colorsValue {
      errors.append(.right(FieldError(fieldName: "colors", level: .error, error: .requiredFieldIsMissing)))
    }
    if case .noValue = radiusValue {
      errors.append(.right(FieldError(fieldName: "radius", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let colorsNonNil = colorsValue.value,
      let radiusNonNil = radiusValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivRadialGradient(
      centerX: centerXValue.value,
      centerY: centerYValue.value,
      colors: colorsNonNil,
      radius: radiusNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivRadialGradientTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivRadialGradient> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var centerXValue: DeserializationResult<Expression<Double>> = parent?.centerX?.value() ?? .noValue
    var centerYValue: DeserializationResult<Expression<Double>> = parent?.centerY?.value() ?? .noValue
    var colorsValue: DeserializationResult<[Expression<Color>]> = parent?.colors?.value() ?? .noValue
    var radiusValue: DeserializationResult<Expression<Int>> = parent?.radius?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "center_x":
        centerXValue = deserialize(__dictValue).merged(with: centerXValue)
      case "center_y":
        centerYValue = deserialize(__dictValue).merged(with: centerYValue)
      case "colors":
        colorsValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator).merged(with: colorsValue)
      case "radius":
        radiusValue = deserialize(__dictValue, validator: ResolvedValue.radiusValidator).merged(with: radiusValue)
      case parent?.centerX?.link:
        centerXValue = centerXValue.merged(with: deserialize(__dictValue))
      case parent?.centerY?.link:
        centerYValue = centerYValue.merged(with: deserialize(__dictValue))
      case parent?.colors?.link:
        colorsValue = colorsValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator))
      case parent?.radius?.link:
        radiusValue = radiusValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.radiusValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      centerXValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "center_x", level: .warning)) },
      centerYValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "center_y", level: .warning)) },
      colorsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "colors", level: .error)) },
      radiusValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "radius", level: .error)) }
    )
    if case .noValue = colorsValue {
      errors.append(.right(FieldError(fieldName: "colors", level: .error, error: .requiredFieldIsMissing)))
    }
    if case .noValue = radiusValue {
      errors.append(.right(FieldError(fieldName: "radius", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let colorsNonNil = colorsValue.value,
      let radiusNonNil = radiusValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivRadialGradient(
      centerX: centerXValue.value,
      centerY: centerYValue.value,
      colors: colorsNonNil,
      radius: radiusNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivRadialGradientTemplate {
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

  public func resolveParent(templates: Templates) throws -> DivRadialGradientTemplate {
    return try mergedWithParent(templates: templates)
  }
}
