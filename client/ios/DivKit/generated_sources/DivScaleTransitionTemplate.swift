// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivScaleTransitionTemplate: TemplateValue {
  public static let type: String = "scale"
  public let parent: String?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 200
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let pivotX: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  public let pivotY: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  public let scale: Field<Expression<Double>>? // constraint: number >= 0.0; default value: 0.0
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      duration: dictionary.getOptionalExpressionField("duration"),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      pivotX: dictionary.getOptionalExpressionField("pivot_x"),
      pivotY: dictionary.getOptionalExpressionField("pivot_y"),
      scale: dictionary.getOptionalExpressionField("scale"),
      startDelay: dictionary.getOptionalExpressionField("start_delay")
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
    let durationValue = { parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let interpolatorValue = { parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue }()
    let pivotXValue = { parent?.pivotX?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotXValidator) ?? .noValue }()
    let pivotYValue = { parent?.pivotY?.resolveOptionalValue(context: context, validator: ResolvedValue.pivotYValidator) ?? .noValue }()
    let scaleValue = { parent?.scale?.resolveOptionalValue(context: context, validator: ResolvedValue.scaleValidator) ?? .noValue }()
    let startDelayValue = { parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue }()
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivScaleTransition(
      duration: { durationValue.value }(),
      interpolator: { interpolatorValue.value }(),
      pivotX: { pivotXValue.value }(),
      pivotY: { pivotYValue.value }(),
      scale: { scaleValue.value }(),
      startDelay: { startDelayValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivScaleTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivScaleTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = { parent?.interpolator?.value() ?? .noValue }()
    var pivotXValue: DeserializationResult<Expression<Double>> = { parent?.pivotX?.value() ?? .noValue }()
    var pivotYValue: DeserializationResult<Expression<Double>> = { parent?.pivotY?.value() ?? .noValue }()
    var scaleValue: DeserializationResult<Expression<Double>> = { parent?.scale?.value() ?? .noValue }()
    var startDelayValue: DeserializationResult<Expression<Int>> = { parent?.startDelay?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "duration" {
           durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
          }
        }()
        _ = {
          if key == "interpolator" {
           interpolatorValue = deserialize(__dictValue).merged(with: interpolatorValue)
          }
        }()
        _ = {
          if key == "pivot_x" {
           pivotXValue = deserialize(__dictValue, validator: ResolvedValue.pivotXValidator).merged(with: pivotXValue)
          }
        }()
        _ = {
          if key == "pivot_y" {
           pivotYValue = deserialize(__dictValue, validator: ResolvedValue.pivotYValidator).merged(with: pivotYValue)
          }
        }()
        _ = {
          if key == "scale" {
           scaleValue = deserialize(__dictValue, validator: ResolvedValue.scaleValidator).merged(with: scaleValue)
          }
        }()
        _ = {
          if key == "start_delay" {
           startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
          }
        }()
        _ = {
         if key == parent?.duration?.link {
           durationValue = durationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.durationValidator) })
          }
        }()
        _ = {
         if key == parent?.interpolator?.link {
           interpolatorValue = interpolatorValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.pivotX?.link {
           pivotXValue = pivotXValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.pivotXValidator) })
          }
        }()
        _ = {
         if key == parent?.pivotY?.link {
           pivotYValue = pivotYValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.pivotYValidator) })
          }
        }()
        _ = {
         if key == parent?.scale?.link {
           scaleValue = scaleValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.scaleValidator) })
          }
        }()
        _ = {
         if key == parent?.startDelay?.link {
           startDelayValue = startDelayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startDelayValidator) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      pivotXValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_x", error: $0) },
      pivotYValue.errorsOrWarnings?.map { .nestedObjectError(field: "pivot_y", error: $0) },
      scaleValue.errorsOrWarnings?.map { .nestedObjectError(field: "scale", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivScaleTransition(
      duration: { durationValue.value }(),
      interpolator: { interpolatorValue.value }(),
      pivotX: { pivotXValue.value }(),
      pivotY: { pivotYValue.value }(),
      scale: { scaleValue.value }(),
      startDelay: { startDelayValue.value }()
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
