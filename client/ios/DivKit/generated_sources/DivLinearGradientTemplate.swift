// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLinearGradientTemplate: TemplateValue {
  public static let type: String = "gradient"
  public let parent: String?
  public let angle: Field<Expression<Int>>? // constraint: number >= 0 && number <= 360; default value: 0
  public let colors: Field<[Expression<Color>]>? // at least 2 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      angle: dictionary.getOptionalExpressionField("angle"),
      colors: dictionary.getOptionalExpressionArray("colors", transform: Color.color(withHexString:))
    )
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivLinearGradientTemplate?) -> DeserializationResult<DivLinearGradient> {
    let angleValue = { parent?.angle?.resolveOptionalValue(context: context, validator: ResolvedValue.angleValidator) ?? .noValue }()
    let colorsValue = { parent?.colors?.resolveValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) ?? .noValue }()
    var errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .nestedObjectError(field: "angle", error: $0) },
      colorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "colors", error: $0) }
    )
    if case .noValue = colorsValue {
      errors.append(.requiredFieldIsMissing(field: "colors"))
    }
    guard
      let colorsNonNil = colorsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivLinearGradient(
      angle: { angleValue.value }(),
      colors: { colorsNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivLinearGradientTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivLinearGradient> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var angleValue: DeserializationResult<Expression<Int>> = { parent?.angle?.value() ?? .noValue }()
    var colorsValue: DeserializationResult<[Expression<Color>]> = { parent?.colors?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "angle" {
           angleValue = deserialize(__dictValue, validator: ResolvedValue.angleValidator).merged(with: angleValue)
          }
        }()
        _ = {
          if key == "colors" {
           colorsValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator).merged(with: colorsValue)
          }
        }()
        _ = {
         if key == parent?.angle?.link {
           angleValue = angleValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.angleValidator) })
          }
        }()
        _ = {
         if key == parent?.colors?.link {
           colorsValue = colorsValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.colorsValidator) })
          }
        }()
      }
    }()
    var errors = mergeErrors(
      angleValue.errorsOrWarnings?.map { .nestedObjectError(field: "angle", error: $0) },
      colorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "colors", error: $0) }
    )
    if case .noValue = colorsValue {
      errors.append(.requiredFieldIsMissing(field: "colors"))
    }
    guard
      let colorsNonNil = colorsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivLinearGradient(
      angle: { angleValue.value }(),
      colors: { colorsNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivLinearGradientTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivLinearGradientTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivLinearGradientTemplate(
      parent: nil,
      angle: angle ?? mergedParent.angle,
      colors: colors ?? mergedParent.colors
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivLinearGradientTemplate {
    return try mergedWithParent(templates: templates)
  }
}
