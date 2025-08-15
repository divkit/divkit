// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivColorAnimatorTemplate: TemplateValue, Sendable {
  public static let type: String = "color_animator"
  public let parent: String?
  public let cancelActions: Field<[DivActionTemplate]>?
  public let direction: Field<Expression<DivAnimationDirection>>? // default value: normal
  public let duration: Field<Expression<Int>>? // constraint: number >= 0
  public let endActions: Field<[DivActionTemplate]>?
  public let endValue: Field<Expression<Color>>?
  public let id: Field<String>?
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: linear
  public let repeatCount: Field<DivCountTemplate>? // default value: .divFixedCount(DivFixedCount(value: .value(1)))
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let startValue: Field<Expression<Color>>?
  public let variableName: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      cancelActions: dictionary.getOptionalArray("cancel_actions", templateToType: templateToType),
      direction: dictionary.getOptionalExpressionField("direction"),
      duration: dictionary.getOptionalExpressionField("duration"),
      endActions: dictionary.getOptionalArray("end_actions", templateToType: templateToType),
      endValue: dictionary.getOptionalExpressionField("end_value", transform: Color.color(withHexString:)),
      id: dictionary.getOptionalField("id"),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      repeatCount: dictionary.getOptionalField("repeat_count", templateToType: templateToType),
      startDelay: dictionary.getOptionalExpressionField("start_delay"),
      startValue: dictionary.getOptionalExpressionField("start_value", transform: Color.color(withHexString:)),
      variableName: dictionary.getOptionalField("variable_name")
    )
  }

  init(
    parent: String?,
    cancelActions: Field<[DivActionTemplate]>? = nil,
    direction: Field<Expression<DivAnimationDirection>>? = nil,
    duration: Field<Expression<Int>>? = nil,
    endActions: Field<[DivActionTemplate]>? = nil,
    endValue: Field<Expression<Color>>? = nil,
    id: Field<String>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    repeatCount: Field<DivCountTemplate>? = nil,
    startDelay: Field<Expression<Int>>? = nil,
    startValue: Field<Expression<Color>>? = nil,
    variableName: Field<String>? = nil
  ) {
    self.parent = parent
    self.cancelActions = cancelActions
    self.direction = direction
    self.duration = duration
    self.endActions = endActions
    self.endValue = endValue
    self.id = id
    self.interpolator = interpolator
    self.repeatCount = repeatCount
    self.startDelay = startDelay
    self.startValue = startValue
    self.variableName = variableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivColorAnimatorTemplate?) -> DeserializationResult<DivColorAnimator> {
    let cancelActionsValue = { parent?.cancelActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let directionValue = { parent?.direction?.resolveOptionalValue(context: context) ?? .noValue }()
    let durationValue = { parent?.duration?.resolveValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let endActionsValue = { parent?.endActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let endValueValue = { parent?.endValue?.resolveValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    let interpolatorValue = { parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue }()
    let repeatCountValue = { parent?.repeatCount?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let startDelayValue = { parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue }()
    let startValueValue = { parent?.startValue?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let variableNameValue = { parent?.variableName?.resolveValue(context: context) ?? .noValue }()
    var errors = mergeErrors(
      cancelActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "cancel_actions", error: $0) },
      directionValue.errorsOrWarnings?.map { .nestedObjectError(field: "direction", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      endValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_value", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat_count", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      startValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = durationValue {
      errors.append(.requiredFieldIsMissing(field: "duration"))
    }
    if case .noValue = endValueValue {
      errors.append(.requiredFieldIsMissing(field: "end_value"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let durationNonNil = durationValue.value,
      let endValueNonNil = endValueValue.value,
      let idNonNil = idValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivColorAnimator(
      cancelActions: { cancelActionsValue.value }(),
      direction: { directionValue.value }(),
      duration: { durationNonNil }(),
      endActions: { endActionsValue.value }(),
      endValue: { endValueNonNil }(),
      id: { idNonNil }(),
      interpolator: { interpolatorValue.value }(),
      repeatCount: { repeatCountValue.value }(),
      startDelay: { startDelayValue.value }(),
      startValue: { startValueValue.value }(),
      variableName: { variableNameNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivColorAnimatorTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivColorAnimator> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var cancelActionsValue: DeserializationResult<[DivAction]> = .noValue
    var directionValue: DeserializationResult<Expression<DivAnimationDirection>> = { parent?.direction?.value() ?? .noValue }()
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var endActionsValue: DeserializationResult<[DivAction]> = .noValue
    var endValueValue: DeserializationResult<Expression<Color>> = { parent?.endValue?.value() ?? .noValue }()
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = { parent?.interpolator?.value() ?? .noValue }()
    var repeatCountValue: DeserializationResult<DivCount> = .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = { parent?.startDelay?.value() ?? .noValue }()
    var startValueValue: DeserializationResult<Expression<Color>> = { parent?.startValue?.value() ?? .noValue }()
    var variableNameValue: DeserializationResult<String> = { parent?.variableName?.value() ?? .noValue }()
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "cancel_actions" {
           cancelActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: cancelActionsValue)
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
          if key == "end_actions" {
           endActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: endActionsValue)
          }
        }()
        _ = {
          if key == "end_value" {
           endValueValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: endValueValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
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
           startValueValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: startValueValue)
          }
        }()
        _ = {
          if key == "variable_name" {
           variableNameValue = deserialize(__dictValue).merged(with: variableNameValue)
          }
        }()
        _ = {
         if key == parent?.cancelActions?.link {
           cancelActionsValue = cancelActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
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
         if key == parent?.endActions?.link {
           endActionsValue = endActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.endValue?.link {
           endValueValue = endValueValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
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
           startValueValue = startValueValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.variableName?.link {
           variableNameValue = variableNameValue.merged(with: { deserialize(__dictValue) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { cancelActionsValue = cancelActionsValue.merged(with: { parent.cancelActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { endActionsValue = endActionsValue.merged(with: { parent.endActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { repeatCountValue = repeatCountValue.merged(with: { parent.repeatCount?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      cancelActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "cancel_actions", error: $0) },
      directionValue.errorsOrWarnings?.map { .nestedObjectError(field: "direction", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      endValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_value", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat_count", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      startValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_value", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = durationValue {
      errors.append(.requiredFieldIsMissing(field: "duration"))
    }
    if case .noValue = endValueValue {
      errors.append(.requiredFieldIsMissing(field: "end_value"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let durationNonNil = durationValue.value,
      let endValueNonNil = endValueValue.value,
      let idNonNil = idValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivColorAnimator(
      cancelActions: { cancelActionsValue.value }(),
      direction: { directionValue.value }(),
      duration: { durationNonNil }(),
      endActions: { endActionsValue.value }(),
      endValue: { endValueNonNil }(),
      id: { idNonNil }(),
      interpolator: { interpolatorValue.value }(),
      repeatCount: { repeatCountValue.value }(),
      startDelay: { startDelayValue.value }(),
      startValue: { startValueValue.value }(),
      variableName: { variableNameNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivColorAnimatorTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivColorAnimatorTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivColorAnimatorTemplate(
      parent: nil,
      cancelActions: cancelActions ?? mergedParent.cancelActions,
      direction: direction ?? mergedParent.direction,
      duration: duration ?? mergedParent.duration,
      endActions: endActions ?? mergedParent.endActions,
      endValue: endValue ?? mergedParent.endValue,
      id: id ?? mergedParent.id,
      interpolator: interpolator ?? mergedParent.interpolator,
      repeatCount: repeatCount ?? mergedParent.repeatCount,
      startDelay: startDelay ?? mergedParent.startDelay,
      startValue: startValue ?? mergedParent.startValue,
      variableName: variableName ?? mergedParent.variableName
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivColorAnimatorTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivColorAnimatorTemplate(
      parent: nil,
      cancelActions: merged.cancelActions?.tryResolveParent(templates: templates),
      direction: merged.direction,
      duration: merged.duration,
      endActions: merged.endActions?.tryResolveParent(templates: templates),
      endValue: merged.endValue,
      id: merged.id,
      interpolator: merged.interpolator,
      repeatCount: merged.repeatCount?.tryResolveParent(templates: templates),
      startDelay: merged.startDelay,
      startValue: merged.startValue,
      variableName: merged.variableName
    )
  }
}
