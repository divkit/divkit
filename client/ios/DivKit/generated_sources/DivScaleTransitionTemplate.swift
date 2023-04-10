// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivScaleTransitionTemplate: TemplateValue {
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

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
      duration: try dictionary.getOptionalExpressionField("duration"),
      interpolator: try dictionary.getOptionalExpressionField("interpolator"),
      pivotX: try dictionary.getOptionalExpressionField("pivot_x"),
      pivotY: try dictionary.getOptionalExpressionField("pivot_y"),
      scale: try dictionary.getOptionalExpressionField("scale"),
      startDelay: try dictionary.getOptionalExpressionField("start_delay")
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

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivScaleTransitionTemplate?) -> DeserializationResult<DivScaleTransition> {
    let durationValue = parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(context: context, validator: ResolvedValue.interpolatorValidator) ?? .noValue
    let pivotXValue = parent?.pivotX?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotXValidator) ?? .noValue
    let pivotYValue = parent?.pivotY?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotYValidator) ?? .noValue
    let scaleValue = parent?.scale?.resolveOptionalValue(context: context, validator: ResolvedValue.scaleValidator) ?? .noValue
    let startDelayValue = parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivScaleTransition(
      duration: durationValue.value,
      interpolator: interpolatorValue.value,
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value,
      scale: scaleValue.value,
      startDelay: startDelayValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivScaleTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivScaleTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?.interpolator?.value() ?? .noValue
    var pivotXValue: DeserializationResult<Expression<Double>> = parent?.pivotX?.value() ?? .noValue
    var pivotYValue: DeserializationResult<Expression<Double>> = parent?.pivotY?.value() ?? .noValue
    var scaleValue: DeserializationResult<Expression<Double>> = parent?.scale?.value() ?? .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = parent?.startDelay?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "interpolator":
        interpolatorValue = deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator).merged(with: interpolatorValue)
      case "pivot_x":
        pivotXValue = deserialize(__dictValue, validator: ResolvedValue.pivotXValidator).merged(with: pivotXValue)
      case "pivot_y":
        pivotYValue = deserialize(__dictValue, validator: ResolvedValue.pivotYValidator).merged(with: pivotYValue)
      case "scale":
        scaleValue = deserialize(__dictValue, validator: ResolvedValue.scaleValidator).merged(with: scaleValue)
      case "start_delay":
        startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.interpolatorValidator))
      case parent?.pivotX?.link:
        pivotXValue = pivotXValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.pivotXValidator))
      case parent?.pivotY?.link:
        pivotYValue = pivotYValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.pivotYValidator))
      case parent?.scale?.link:
        scaleValue = scaleValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.scaleValidator))
      case parent?.startDelay?.link:
        startDelayValue = startDelayValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startDelayValidator))
      default: break
      }
    }
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivScaleTransition(
      duration: durationValue.value,
      interpolator: interpolatorValue.value,
      pivotX: pivotXValue.value,
      pivotY: pivotYValue.value,
      scale: scaleValue.value,
      startDelay: startDelayValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivScaleTransitionTemplate {
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

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivScaleTransitionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
