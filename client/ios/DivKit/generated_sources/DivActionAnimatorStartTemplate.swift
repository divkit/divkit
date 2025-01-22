// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionAnimatorStartTemplate: TemplateValue, Sendable {
  public static let type: String = "animator_start"
  public let parent: String?
  public let animatorId: Field<String>?
  public let direction: Field<Expression<DivAnimationDirection>>?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0
  public let endValue: Field<DivTypedValueTemplate>?
  public let interpolator: Field<Expression<DivAnimationInterpolator>>?
  public let repeatCount: Field<DivCountTemplate>?
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0
  public let startValue: Field<DivTypedValueTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      animatorId: dictionary.getOptionalField("animator_id"),
      direction: dictionary.getOptionalExpressionField("direction"),
      duration: dictionary.getOptionalExpressionField("duration"),
      endValue: dictionary.getOptionalField("end_value", templateToType: templateToType),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      repeatCount: dictionary.getOptionalField("repeat_count", templateToType: templateToType),
      startDelay: dictionary.getOptionalExpressionField("start_delay"),
      startValue: dictionary.getOptionalField("start_value", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    animatorId: Field<String>? = nil,
    direction: Field<Expression<DivAnimationDirection>>? = nil,
    duration: Field<Expression<Int>>? = nil,
    endValue: Field<DivTypedValueTemplate>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    repeatCount: Field<DivCountTemplate>? = nil,
    startDelay: Field<Expression<Int>>? = nil,
    startValue: Field<DivTypedValueTemplate>? = nil
  ) {
    self.parent = parent
    self.animatorId = animatorId
    self.direction = direction
    self.duration = duration
    self.endValue = endValue
    self.interpolator = interpolator
    self.repeatCount = repeatCount
    self.startDelay = startDelay
    self.startValue = startValue
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionAnimatorStartTemplate?) -> DeserializationResult<DivActionAnimatorStart> {
    let animatorIdValue = { parent?.animatorId?.resolveValue(context: context) ?? .noValue }()
    let directionValue = { parent?.direction?.resolveOptionalValue(context: context) ?? .noValue }()
    let durationValue = { parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let endValueValue = { parent?.endValue?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let interpolatorValue = { parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue }()
    let repeatCountValue = { parent?.repeatCount?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let startDelayValue = { parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue }()
    let startValueValue = { parent?.startValue?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      animatorIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "animator_id", error: $0) },
      directionValue.errorsOrWarnings?.map { .nestedObjectError(field: "direction", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_value", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat_count", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      startValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_value", error: $0) }
    )
    if case .noValue = animatorIdValue {
      errors.append(.requiredFieldIsMissing(field: "animator_id"))
    }
    guard
      let animatorIdNonNil = animatorIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionAnimatorStart(
      animatorId: { animatorIdNonNil }(),
      direction: { directionValue.value }(),
      duration: { durationValue.value }(),
      endValue: { endValueValue.value }(),
      interpolator: { interpolatorValue.value }(),
      repeatCount: { repeatCountValue.value }(),
      startDelay: { startDelayValue.value }(),
      startValue: { startValueValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionAnimatorStartTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionAnimatorStart> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animatorIdValue: DeserializationResult<String> = { parent?.animatorId?.value() ?? .noValue }()
    var directionValue: DeserializationResult<Expression<DivAnimationDirection>> = { parent?.direction?.value() ?? .noValue }()
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var endValueValue: DeserializationResult<DivTypedValue> = .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = { parent?.interpolator?.value() ?? .noValue }()
    var repeatCountValue: DeserializationResult<DivCount> = .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = { parent?.startDelay?.value() ?? .noValue }()
    var startValueValue: DeserializationResult<DivTypedValue> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "animator_id" {
           animatorIdValue = deserialize(__dictValue).merged(with: animatorIdValue)
          }
        }()
        _ = {
          if key == "direction" {
           directionValue = deserialize(__dictValue).merged(with: directionValue)
          }
        }()
        _ = {
          if key == "duration" {
           durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
          }
        }()
        _ = {
          if key == "end_value" {
           endValueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: endValueValue)
          }
        }()
        _ = {
          if key == "interpolator" {
           interpolatorValue = deserialize(__dictValue).merged(with: interpolatorValue)
          }
        }()
        _ = {
          if key == "repeat_count" {
           repeatCountValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCountTemplate.self).merged(with: repeatCountValue)
          }
        }()
        _ = {
          if key == "start_delay" {
           startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
          }
        }()
        _ = {
          if key == "start_value" {
           startValueValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self).merged(with: startValueValue)
          }
        }()
        _ = {
         if key == parent?.animatorId?.link {
           animatorIdValue = animatorIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.direction?.link {
           directionValue = directionValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.duration?.link {
           durationValue = durationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.durationValidator) })
          }
        }()
        _ = {
         if key == parent?.endValue?.link {
           endValueValue = endValueValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.interpolator?.link {
           interpolatorValue = interpolatorValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.repeatCount?.link {
           repeatCountValue = repeatCountValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivCountTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.startDelay?.link {
           startDelayValue = startDelayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startDelayValidator) })
          }
        }()
        _ = {
         if key == parent?.startValue?.link {
           startValueValue = startValueValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTypedValueTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { endValueValue = endValueValue.merged(with: { parent.endValue?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { repeatCountValue = repeatCountValue.merged(with: { parent.repeatCount?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { startValueValue = startValueValue.merged(with: { parent.startValue?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      animatorIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "animator_id", error: $0) },
      directionValue.errorsOrWarnings?.map { .nestedObjectError(field: "direction", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_value", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat_count", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      startValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_value", error: $0) }
    )
    if case .noValue = animatorIdValue {
      errors.append(.requiredFieldIsMissing(field: "animator_id"))
    }
    guard
      let animatorIdNonNil = animatorIdValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionAnimatorStart(
      animatorId: { animatorIdNonNil }(),
      direction: { directionValue.value }(),
      duration: { durationValue.value }(),
      endValue: { endValueValue.value }(),
      interpolator: { interpolatorValue.value }(),
      repeatCount: { repeatCountValue.value }(),
      startDelay: { startDelayValue.value }(),
      startValue: { startValueValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionAnimatorStartTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionAnimatorStartTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionAnimatorStartTemplate(
      parent: nil,
      animatorId: animatorId ?? mergedParent.animatorId,
      direction: direction ?? mergedParent.direction,
      duration: duration ?? mergedParent.duration,
      endValue: endValue ?? mergedParent.endValue,
      interpolator: interpolator ?? mergedParent.interpolator,
      repeatCount: repeatCount ?? mergedParent.repeatCount,
      startDelay: startDelay ?? mergedParent.startDelay,
      startValue: startValue ?? mergedParent.startValue
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionAnimatorStartTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionAnimatorStartTemplate(
      parent: nil,
      animatorId: merged.animatorId,
      direction: merged.direction,
      duration: merged.duration,
      endValue: merged.endValue?.tryResolveParent(templates: templates),
      interpolator: merged.interpolator,
      repeatCount: merged.repeatCount?.tryResolveParent(templates: templates),
      startDelay: merged.startDelay,
      startValue: merged.startValue?.tryResolveParent(templates: templates)
    )
  }
}
