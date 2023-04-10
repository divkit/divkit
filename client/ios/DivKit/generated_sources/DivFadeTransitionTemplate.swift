// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFadeTransitionTemplate: TemplateValue {
  public static let type: String = "fade"
  public let parent: String? // at least 1 char
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.0
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 200
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      alpha: try dictionary.getOptionalExpressionField("alpha"),
      duration: try dictionary.getOptionalExpressionField("duration"),
      interpolator: try dictionary.getOptionalExpressionField("interpolator"),
      startDelay: try dictionary.getOptionalExpressionField("start_delay")
    )
  }

  init(
    parent: String?,
    alpha: Field<Expression<Double>>? = nil,
    duration: Field<Expression<Int>>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    startDelay: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.alpha = alpha
    self.duration = duration
    self.interpolator = interpolator
    self.startDelay = startDelay
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFadeTransitionTemplate?) -> DeserializationResult<DivFadeTransition> {
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let durationValue = parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(context: context, validator: ResolvedValue.interpolatorValidator) ?? .noValue
    let startDelayValue = parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue
    let errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivFadeTransition(
      alpha: alphaValue.value,
      duration: durationValue.value,
      interpolator: interpolatorValue.value,
      startDelay: startDelayValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFadeTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFadeTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?.interpolator?.value() ?? .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = parent?.startDelay?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "interpolator":
        interpolatorValue = deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator).merged(with: interpolatorValue)
      case "start_delay":
        startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator))
      case parent?.startDelay?.link:
        startDelayValue = startDelayValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startDelayValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivFadeTransition(
      alpha: alphaValue.value,
      duration: durationValue.value,
      interpolator: interpolatorValue.value,
      startDelay: startDelayValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFadeTransitionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivFadeTransitionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivFadeTransitionTemplate(
      parent: nil,
      alpha: alpha ?? mergedParent.alpha,
      duration: duration ?? mergedParent.duration,
      interpolator: interpolator ?? mergedParent.interpolator,
      startDelay: startDelay ?? mergedParent.startDelay
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFadeTransitionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
