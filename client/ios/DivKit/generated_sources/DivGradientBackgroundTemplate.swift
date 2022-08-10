// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivGradientBackgroundTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "gradient"
  public let parent: String? // at least 1 char
  public let angle: Field<Expression<Int>>? // constraint: number >= 0 && number <= 360; default value: 0
  public let colors: Field<[Expression<Color>]>? // at least 2 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        angle: try dictionary.getOptionalExpressionField("angle"),
        colors: try dictionary.getOptionalExpressionArray("colors", transform: Color.color(withHexString:))
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-gradient-background_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    angle: Field<Expression<Int>>? = nil,
    colors: Field<[Expression<Color>]>? = nil
  ) {
    self.parent = parent
    self.angle = angle
    self.colors = colors
  }

  private static func resolveOnlyLinks(context: Context, parent: DivGradientBackgroundTemplate?) -> DeserializationResult<DivGradientBackground> {
    let angleValue = parent?.angle?.resolveOptionalValue(context: context, validator: ResolvedValue.angleValidator) ?? .noValue
    let colorsValue = parent?.colors?.resolveValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) ?? .noValue
    var errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "angle", level: .warning)) },
      colorsValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "colors", level: .error)) }
    )
    if case .noValue = colorsValue {
      errors.append(.right(FieldError(fieldName: "colors", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let colorsNonNil = colorsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivGradientBackground(
      angle: angleValue.value,
      colors: colorsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivGradientBackgroundTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivGradientBackground> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var angleValue: DeserializationResult<Expression<Int>> = parent?.angle?.value() ?? .noValue
    var colorsValue: DeserializationResult<[Expression<Color>]> = parent?.colors?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "angle":
        angleValue = deserialize(__dictValue, validator: ResolvedValue.angleValidator).merged(with: angleValue)
      case "colors":
        colorsValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator).merged(with: colorsValue)
      case parent?.angle?.link:
        angleValue = angleValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.angleValidator))
      case parent?.colors?.link:
        colorsValue = colorsValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator))
      default: break
      }
    }
    var errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "angle", level: .warning)) },
      colorsValue.errorsOrWarnings?.map { Either.right($0.asError(deserializing: "colors", level: .error)) }
    )
    if case .noValue = colorsValue {
      errors.append(.right(FieldError(fieldName: "colors", level: .error, error: .requiredFieldIsMissing)))
    }
    guard
      let colorsNonNil = colorsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivGradientBackground(
      angle: angleValue.value,
      colors: colorsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivGradientBackgroundTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivGradientBackgroundTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivGradientBackgroundTemplate(
      parent: nil,
      angle: angle ?? mergedParent.angle,
      colors: colors ?? mergedParent.colors
    )
  }

  public func resolveParent(templates: Templates) throws -> DivGradientBackgroundTemplate {
    return try mergedWithParent(templates: templates)
  }
}
