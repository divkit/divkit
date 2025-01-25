// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivChangeBoundsTransitionTemplate: TemplateValue {
  public static let type: String = "change_bounds"
  public let parent: String?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 200
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: ease_in_out
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      duration: dictionary.getOptionalExpressionField("duration"),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      startDelay: dictionary.getOptionalExpressionField("start_delay")
    )
  }

  init(
    parent: String?,
    duration: Field<Expression<Int>>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    startDelay: Field<Expression<Int>>? = nil
  ) {
    self.parent = parent
    self.duration = duration
    self.interpolator = interpolator
    self.startDelay = startDelay
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivChangeBoundsTransitionTemplate?) -> DeserializationResult<DivChangeBoundsTransition> {
    let durationValue = { parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let interpolatorValue = { parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue }()
    let startDelayValue = { parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue }()
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivChangeBoundsTransition(
      duration: { durationValue.value }(),
      interpolator: { interpolatorValue.value }(),
      startDelay: { startDelayValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivChangeBoundsTransitionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivChangeBoundsTransition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = { parent?.interpolator?.value() ?? .noValue }()
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
         if key == parent?.startDelay?.link {
           startDelayValue = startDelayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startDelayValidator) })
          }
        }()
      }
    }()
    let errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) }
    )
    let result = DivChangeBoundsTransition(
      duration: { durationValue.value }(),
      interpolator: { interpolatorValue.value }(),
      startDelay: { startDelayValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivChangeBoundsTransitionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivChangeBoundsTransitionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivChangeBoundsTransitionTemplate(
      parent: nil,
      duration: duration ?? mergedParent.duration,
      interpolator: interpolator ?? mergedParent.interpolator,
      startDelay: startDelay ?? mergedParent.startDelay
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivChangeBoundsTransitionTemplate {
    return try mergedWithParent(templates: templates)
  }
}
