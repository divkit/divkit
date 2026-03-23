// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivShadowTemplate: TemplateValue, Sendable {
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.19
  public let blur: Field<Expression<Int>>? // constraint: number >= 0; default value: 2
  public let color: Field<Expression<Color>>? // default value: #000000
  public let offset: Field<DivPointTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      alpha: dictionary.getOptionalExpressionField("alpha"),
      blur: dictionary.getOptionalExpressionField("blur"),
      color: dictionary.getOptionalExpressionField("color", transform: Color.color(withHexString:)),
      offset: dictionary.getOptionalField("offset", templateToType: templateToType)
    )
  }

  init(
    alpha: Field<Expression<Double>>? = nil,
    blur: Field<Expression<Int>>? = nil,
    color: Field<Expression<Color>>? = nil,
    offset: Field<DivPointTemplate>? = nil
  ) {
    self.alpha = alpha
    self.blur = blur
    self.color = color
    self.offset = offset
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivShadowTemplate?) -> DeserializationResult<DivShadow> {
    let alphaValue = { parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue }()
    let blurValue = { parent?.blur?.resolveOptionalValue(context: context, validator: ResolvedValue.blurValidator) ?? .noValue }()
    let colorValue = { parent?.color?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let offsetValue = { parent?.offset?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      blurValue.errorsOrWarnings?.map { .nestedObjectError(field: "blur", error: $0) },
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) }
    )
    if case .noValue = offsetValue {
      errors.append(.requiredFieldIsMissing(field: "offset"))
    }
    guard
      let offsetNonNil = offsetValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivShadow(
      alpha: { alphaValue.value }(),
      blur: { blurValue.value }(),
      color: { colorValue.value }(),
      offset: { offsetNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivShadowTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivShadow> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var alphaValue: DeserializationResult<Expression<Double>> = { parent?.alpha?.value() ?? .noValue }()
    var blurValue: DeserializationResult<Expression<Int>> = { parent?.blur?.value() ?? .noValue }()
    var colorValue: DeserializationResult<Expression<Color>> = { parent?.color?.value() ?? .noValue }()
    var offsetValue: DeserializationResult<DivPoint> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "alpha" {
           alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
          }
        }()
        _ = {
          if key == "blur" {
           blurValue = deserialize(__dictValue, validator: ResolvedValue.blurValidator).merged(with: blurValue)
          }
        }()
        _ = {
          if key == "color" {
           colorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: colorValue)
          }
        }()
        _ = {
          if key == "offset" {
           offsetValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPointTemplate.self).merged(with: offsetValue)
          }
        }()
        _ = {
         if key == parent?.alpha?.link {
           alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
          }
        }()
        _ = {
         if key == parent?.blur?.link {
           blurValue = blurValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.blurValidator) })
          }
        }()
        _ = {
         if key == parent?.color?.link {
           colorValue = colorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.offset?.link {
           offsetValue = offsetValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPointTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { offsetValue = offsetValue.merged(with: { parent.offset?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      blurValue.errorsOrWarnings?.map { .nestedObjectError(field: "blur", error: $0) },
      colorValue.errorsOrWarnings?.map { .nestedObjectError(field: "color", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) }
    )
    if case .noValue = offsetValue {
      errors.append(.requiredFieldIsMissing(field: "offset"))
    }
    guard
      let offsetNonNil = offsetValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivShadow(
      alpha: { alphaValue.value }(),
      blur: { blurValue.value }(),
      color: { colorValue.value }(),
      offset: { offsetNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivShadowTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivShadowTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivShadowTemplate(
      alpha: merged.alpha,
      blur: merged.blur,
      color: merged.color,
      offset: try merged.offset?.resolveParent(templates: templates)
    )
  }
}
