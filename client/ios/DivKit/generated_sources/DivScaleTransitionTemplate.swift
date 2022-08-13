// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivScaleTransitionTemplate: TemplateValue, TemplateDeserializable {
  public static let type: String = "scale"
  public let parent: String? // at least 1 char
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 200
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let pivotX: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  public let pivotY: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  public let scale: Field<Expression<Double>>? // constraint: number >= 0.0; default value: 0.0
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType _: TemplateToType) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      duration: try dictionary.getOptionalField("duration"),
      interpolator: try dictionary.getOptionalField("interpolator"),
      pivotX: try dictionary.getOptionalField("pivot_x"),
      pivotY: try dictionary.getOptionalField("pivot_y"),
      scale: try dictionary.getOptionalField("scale"),
      startDelay: try dictionary.getOptionalField("start_delay")
    )
  }

  init(
    parent: String?,
    duration: Field<Expression<Int>>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    pivotX: Field<Expression<Double>>? = nil,
    pivotY: Field<Expression<Double>>? = nil,
    scale: Field<Expression<Double>>? = nil,
    startDelay: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.duration = duration
    self.interpolator = interpolator
    self.pivotX = pivotX
    self.pivotY = pivotY
    self.scale = scale
    self.startDelay = startDelay
  }

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivScaleTransitionTemplate?
  ) -> DeserializationResult<DivScaleTransition> {
    let durationValue = parent?.duration?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.durationValidator
    ) ?? .noValue
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.interpolatorValidator
    ) ?? .noValue
    let pivotXValue = parent?.pivotX?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.pivotXValidator
    ) ?? .noValue
    let pivotYValue = parent?.pivotY?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.pivotYValidator
    ) ?? .noValue
    let scaleValue = parent?.scale?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.scaleValidator
    ) ?? .noValue
    let startDelayValue = parent?.startDelay?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.startDelayValidator
    ) ?? .noValue
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "duration", level: .warning)) },
      interpolatorValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "interpolator", level: .warning)) },
      pivotXValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "pivot_x", level: .warning)) },
      pivotYValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "pivot_y", level: .warning)) },
      scaleValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "scale", level: .warning)) },
      startDelayValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "start_delay", level: .warning)) }
    )
    let result = DivScaleTransition(
      duration: durationValue.value,
      interpolator: interpolatorValue.value,
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value,
      scale: scaleValue.value,
      startDelay: startDelayValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivScaleTransitionTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivScaleTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?
      .value() ?? .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?
      .interpolator?.value() ?? .noValue
    var pivotXValue: DeserializationResult<Expression<Double>> = parent?.pivotX?.value() ?? .noValue
    var pivotYValue: DeserializationResult<Expression<Double>> = parent?.pivotY?.value() ?? .noValue
    var scaleValue: DeserializationResult<Expression<Double>> = parent?.scale?.value() ?? .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = parent?.startDelay?
      .value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator)
          .merged(with: durationValue)
      case "interpolator":
        interpolatorValue = deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator)
          .merged(with: interpolatorValue)
      case "pivot_x":
        pivotXValue = deserialize(__dictValue, validator: ResolvedValue.pivotXValidator)
          .merged(with: pivotXValue)
      case "pivot_y":
        pivotYValue = deserialize(__dictValue, validator: ResolvedValue.pivotYValidator)
          .merged(with: pivotYValue)
      case "scale":
        scaleValue = deserialize(__dictValue, validator: ResolvedValue.scaleValidator)
          .merged(with: scaleValue)
      case "start_delay":
        startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator)
          .merged(with: startDelayValue)
      case parent?.duration?.link:
        durationValue = durationValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator))
      case parent?.pivotX?.link:
        pivotXValue = pivotXValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.pivotXValidator))
      case parent?.pivotY?.link:
        pivotYValue = pivotYValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.pivotYValidator))
      case parent?.scale?.link:
        scaleValue = scaleValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.scaleValidator))
      case parent?.startDelay?.link:
        startDelayValue = startDelayValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.startDelayValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "duration", level: .warning)) },
      interpolatorValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "interpolator", level: .warning)) },
      pivotXValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "pivot_x", level: .warning)) },
      pivotYValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "pivot_y", level: .warning)) },
      scaleValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "scale", level: .warning)) },
      startDelayValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "start_delay", level: .warning)) }
    )
    let result = DivScaleTransition(
      duration: durationValue.value,
      interpolator: interpolatorValue.value,
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value,
      scale: scaleValue.value,
      startDelay: startDelayValue.value
    )
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivScaleTransitionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivScaleTransitionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivScaleTransitionTemplate(
      parent: nil,
      duration: duration ?? mergedParent.duration,
      interpolator: interpolator ?? mergedParent.interpolator,
      pivotX: pivotX ?? mergedParent.pivotX,
      pivotY: pivotY ?? mergedParent.pivotY,
      scale: scale ?? mergedParent.scale,
      startDelay: startDelay ?? mergedParent.startDelay
    )
  }

  public func resolveParent(templates: Templates) throws -> DivScaleTransitionTemplate {
    try mergedWithParent(templates: templates)
  }
}
