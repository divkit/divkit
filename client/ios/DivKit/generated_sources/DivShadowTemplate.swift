// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivShadowTemplate: TemplateValue, TemplateDeserializable {
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.19
  public let blur: Field<Expression<Int>>? // constraint: number >= 0; default value: 2
  public let color: Field<Expression<Color>>? // default value: #000000
  public let offset: Field<DivPointTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        alpha: try dictionary.getOptionalField("alpha"),
        blur: try dictionary.getOptionalField("blur"),
        color: try dictionary.getOptionalField("color", transform: Color.color(withHexString:)),
        offset: try dictionary.getOptionalField("offset", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-shadow_template." + field,
        representation: representation
      )
    }
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

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivShadowTemplate?
  ) -> DeserializationResult<DivShadow> {
    let alphaValue = parent?.alpha?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.alphaValidator
    ) ?? .noValue
    let blurValue = parent?.blur?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.blurValidator
    ) ?? .noValue
    let colorValue = parent?.color?.resolveOptionalValue(
      context: context,
      transform: Color.color(withHexString:),
      validator: ResolvedValue.colorValidator
    ) ?? .noValue
    let offsetValue = parent?.offset?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "alpha", level: .warning)) },
      blurValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "blur", level: .warning)) },
      colorValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "color", level: .warning)) },
      offsetValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "offset", level: .error)) }
    )
    if case .noValue = offsetValue {
      errors
        .append(.right(FieldError(
          fieldName: "offset",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    guard
      let offsetNonNil = offsetValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivShadow(
      alpha: alphaValue.value,
      blur: blurValue.value,
      color: colorValue.value,
      offset: offsetNonNil
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivShadowTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivShadow> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var blurValue: DeserializationResult<Expression<Int>> = parent?.blur?.value() ?? .noValue
    var colorValue: DeserializationResult<Expression<Color>> = parent?.color?.value() ?? .noValue
    var offsetValue: DeserializationResult<DivPoint> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator)
          .merged(with: alphaValue)
      case "blur":
        blurValue = deserialize(__dictValue, validator: ResolvedValue.blurValidator)
          .merged(with: blurValue)
      case "color":
        colorValue = deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.colorValidator
        ).merged(with: colorValue)
      case "offset":
        offsetValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivPointTemplate.self
        ).merged(with: offsetValue)
      case parent?.alpha?.link:
        alphaValue = alphaValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.blur?.link:
        blurValue = blurValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.blurValidator))
      case parent?.color?.link:
        colorValue = colorValue.merged(with: deserialize(
          __dictValue,
          transform: Color.color(withHexString:),
          validator: ResolvedValue.colorValidator
        ))
      case parent?.offset?.link:
        offsetValue = offsetValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivPointTemplate.self
        ))
      default: break
      }
    }
    if let parent = parent {
      offsetValue = offsetValue
        .merged(with: parent.offset?.resolveValue(context: context, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      alphaValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "alpha", level: .warning)) },
      blurValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "blur", level: .warning)) },
      colorValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "color", level: .warning)) },
      offsetValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "offset", level: .error)) }
    )
    if case .noValue = offsetValue {
      errors
        .append(.right(FieldError(
          fieldName: "offset",
          level: .error,
          error: .requiredFieldIsMissing
        )))
    }
    guard
      let offsetNonNil = offsetValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivShadow(
      alpha: alphaValue.value,
      blur: blurValue.value,
      color: colorValue.value,
      offset: offsetNonNil
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates _: Templates) throws -> DivShadowTemplate {
    self
  }

  public func resolveParent(templates: Templates) throws -> DivShadowTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivShadowTemplate(
      alpha: merged.alpha,
      blur: merged.blur,
      color: merged.color,
      offset: try merged.offset?.resolveParent(templates: templates)
    )
  }
}
