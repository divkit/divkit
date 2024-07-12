// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPageTransformationSlideTemplate: TemplateValue {
  public static let type: String = "slide"
  public let parent: String?
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let nextPageAlpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let nextPageScale: Field<Expression<Double>>? // constraint: number >= 0.0; default value: 1.0
  public let previousPageAlpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let previousPageScale: Field<Expression<Double>>? // constraint: number >= 0.0; default value: 1.0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      nextPageAlpha: dictionary.getOptionalExpressionField("next_page_alpha"),
      nextPageScale: dictionary.getOptionalExpressionField("next_page_scale"),
      previousPageAlpha: dictionary.getOptionalExpressionField("previous_page_alpha"),
      previousPageScale: dictionary.getOptionalExpressionField("previous_page_scale")
    )
  }

  init(
    parent: String?,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    nextPageAlpha: Field<Expression<Double>>? = nil,
    nextPageScale: Field<Expression<Double>>? = nil,
    previousPageAlpha: Field<Expression<Double>>? = nil,
    previousPageScale: Field<Expression<Double>>? = nil
  ) {
    self.parent = parent
    self.interpolator = interpolator
    self.nextPageAlpha = nextPageAlpha
    self.nextPageScale = nextPageScale
    self.previousPageAlpha = previousPageAlpha
    self.previousPageScale = previousPageScale
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPageTransformationSlideTemplate?) -> DeserializationResult<DivPageTransformationSlide> {
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue
    let nextPageAlphaValue = parent?.nextPageAlpha?.resolveOptionalValue(context: context, validator: ResolvedValue.nextPageAlphaValidator) ?? .noValue
    let nextPageScaleValue = parent?.nextPageScale?.resolveOptionalValue(context: context, validator: ResolvedValue.nextPageScaleValidator) ?? .noValue
    let previousPageAlphaValue = parent?.previousPageAlpha?.resolveOptionalValue(context: context, validator: ResolvedValue.previousPageAlphaValidator) ?? .noValue
    let previousPageScaleValue = parent?.previousPageScale?.resolveOptionalValue(context: context, validator: ResolvedValue.previousPageScaleValidator) ?? .noValue
    let errors = mergeErrors(
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      nextPageAlphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "next_page_alpha", error: $0) },
      nextPageScaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "next_page_scale", error: $0) },
      previousPageAlphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "previous_page_alpha", error: $0) },
      previousPageScaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "previous_page_scale", error: $0) }
    )
    let result = DivPageTransformationSlide(
      interpolator: interpolatorValue.value,
      nextPageAlpha: nextPageAlphaValue.value,
      nextPageScale: nextPageScaleValue.value,
      previousPageAlpha: previousPageAlphaValue.value,
      previousPageScale: previousPageScaleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPageTransformationSlideTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPageTransformationSlide> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?.interpolator?.value() ?? .noValue
    var nextPageAlphaValue: DeserializationResult<Expression<Double>> = parent?.nextPageAlpha?.value() ?? .noValue
    var nextPageScaleValue: DeserializationResult<Expression<Double>> = parent?.nextPageScale?.value() ?? .noValue
    var previousPageAlphaValue: DeserializationResult<Expression<Double>> = parent?.previousPageAlpha?.value() ?? .noValue
    var previousPageScaleValue: DeserializationResult<Expression<Double>> = parent?.previousPageScale?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "interpolator":
        interpolatorValue = deserialize(__dictValue).merged(with: interpolatorValue)
      case "next_page_alpha":
        nextPageAlphaValue = deserialize(__dictValue, validator: ResolvedValue.nextPageAlphaValidator).merged(with: nextPageAlphaValue)
      case "next_page_scale":
        nextPageScaleValue = deserialize(__dictValue, validator: ResolvedValue.nextPageScaleValidator).merged(with: nextPageScaleValue)
      case "previous_page_alpha":
        previousPageAlphaValue = deserialize(__dictValue, validator: ResolvedValue.previousPageAlphaValidator).merged(with: previousPageAlphaValue)
      case "previous_page_scale":
        previousPageScaleValue = deserialize(__dictValue, validator: ResolvedValue.previousPageScaleValidator).merged(with: previousPageScaleValue)
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue.merged(with: { deserialize(__dictValue) })
      case parent?.nextPageAlpha?.link:
        nextPageAlphaValue = nextPageAlphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.nextPageAlphaValidator) })
      case parent?.nextPageScale?.link:
        nextPageScaleValue = nextPageScaleValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.nextPageScaleValidator) })
      case parent?.previousPageAlpha?.link:
        previousPageAlphaValue = previousPageAlphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.previousPageAlphaValidator) })
      case parent?.previousPageScale?.link:
        previousPageScaleValue = previousPageScaleValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.previousPageScaleValidator) })
      default: break
      }
    }
    let errors = mergeErrors(
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      nextPageAlphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "next_page_alpha", error: $0) },
      nextPageScaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "next_page_scale", error: $0) },
      previousPageAlphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "previous_page_alpha", error: $0) },
      previousPageScaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "previous_page_scale", error: $0) }
    )
    let result = DivPageTransformationSlide(
      interpolator: interpolatorValue.value,
      nextPageAlpha: nextPageAlphaValue.value,
      nextPageScale: nextPageScaleValue.value,
      previousPageAlpha: previousPageAlphaValue.value,
      previousPageScale: previousPageScaleValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPageTransformationSlideTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivPageTransformationSlideTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivPageTransformationSlideTemplate(
      parent: nil,
      interpolator: interpolator ?? mergedParent.interpolator,
      nextPageAlpha: nextPageAlpha ?? mergedParent.nextPageAlpha,
      nextPageScale: nextPageScale ?? mergedParent.nextPageScale,
      previousPageAlpha: previousPageAlpha ?? mergedParent.previousPageAlpha,
      previousPageScale: previousPageScale ?? mergedParent.previousPageScale
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPageTransformationSlideTemplate {
    return try mergedWithParent(templates: templates)
  }
}
