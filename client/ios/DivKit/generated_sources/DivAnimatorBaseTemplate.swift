// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAnimatorBaseTemplate: TemplateValue {
  public let cancelActions: Field<[DivActionTemplate]>?
  public let direction: Field<Expression<DivAnimationDirection>>? // default value: normal
  public let duration: Field<Expression<Int>>? // constraint: number >= 0
  public let endActions: Field<[DivActionTemplate]>?
  public let id: Field<String>?
  public let interpolator: Field<Expression<DivAnimationInterpolator>>? // default value: linear
  public let repeatCount: Field<Expression<Int>>? // constraint: number >= 0; default value: 1
  public let startDelay: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let variableName: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      cancelActions: dictionary.getOptionalArray("cancel_actions", templateToType: templateToType),
      direction: dictionary.getOptionalExpressionField("direction"),
      duration: dictionary.getOptionalExpressionField("duration"),
      endActions: dictionary.getOptionalArray("end_actions", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      interpolator: dictionary.getOptionalExpressionField("interpolator"),
      repeatCount: dictionary.getOptionalExpressionField("repeat_count"),
      startDelay: dictionary.getOptionalExpressionField("start_delay"),
      variableName: dictionary.getOptionalField("variable_name")
    )
  }

  init(
    cancelActions: Field<[DivActionTemplate]>? = nil,
    direction: Field<Expression<DivAnimationDirection>>? = nil,
    duration: Field<Expression<Int>>? = nil,
    endActions: Field<[DivActionTemplate]>? = nil,
    id: Field<String>? = nil,
    interpolator: Field<Expression<DivAnimationInterpolator>>? = nil,
    repeatCount: Field<Expression<Int>>? = nil,
    startDelay: Field<Expression<Int>>? = nil,
    variableName: Field<String>? = nil
  ) {
    self.cancelActions = cancelActions
    self.direction = direction
    self.duration = duration
    self.endActions = endActions
    self.id = id
    self.interpolator = interpolator
    self.repeatCount = repeatCount
    self.startDelay = startDelay
    self.variableName = variableName
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivAnimatorBaseTemplate?) -> DeserializationResult<DivAnimatorBase> {
    let cancelActionsValue = parent?.cancelActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let directionValue = parent?.direction?.resolveOptionalValue(context: context) ?? .noValue
    let durationValue = parent?.duration?.resolveValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let endActionsValue = parent?.endActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveValue(context: context) ?? .noValue
    let interpolatorValue = parent?.interpolator?.resolveOptionalValue(context: context) ?? .noValue
    let repeatCountValue = parent?.repeatCount?.resolveOptionalValue(context: context, validator: ResolvedValue.repeatCountValidator) ?? .noValue
    let startDelayValue = parent?.startDelay?.resolveOptionalValue(context: context, validator: ResolvedValue.startDelayValidator) ?? .noValue
    let variableNameValue = parent?.variableName?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      cancelActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "cancel_actions", error: $0) },
      directionValue.errorsOrWarnings?.map { .nestedObjectError(field: "direction", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat_count", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = durationValue {
      errors.append(.requiredFieldIsMissing(field: "duration"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let durationNonNil = durationValue.value,
      let idNonNil = idValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAnimatorBase(
      cancelActions: cancelActionsValue.value,
      direction: directionValue.value,
      duration: durationNonNil,
      endActions: endActionsValue.value,
      id: idNonNil,
      interpolator: interpolatorValue.value,
      repeatCount: repeatCountValue.value,
      startDelay: startDelayValue.value,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivAnimatorBaseTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivAnimatorBase> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var cancelActionsValue: DeserializationResult<[DivAction]> = .noValue
    var directionValue: DeserializationResult<Expression<DivAnimationDirection>> = parent?.direction?.value() ?? .noValue
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var endActionsValue: DeserializationResult<[DivAction]> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
    var interpolatorValue: DeserializationResult<Expression<DivAnimationInterpolator>> = parent?.interpolator?.value() ?? .noValue
    var repeatCountValue: DeserializationResult<Expression<Int>> = parent?.repeatCount?.value() ?? .noValue
    var startDelayValue: DeserializationResult<Expression<Int>> = parent?.startDelay?.value() ?? .noValue
    var variableNameValue: DeserializationResult<String> = parent?.variableName?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "cancel_actions":
        cancelActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: cancelActionsValue)
      case "direction":
        directionValue = deserialize(__dictValue).merged(with: directionValue)
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "end_actions":
        endActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: endActionsValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "interpolator":
        interpolatorValue = deserialize(__dictValue).merged(with: interpolatorValue)
      case "repeat_count":
        repeatCountValue = deserialize(__dictValue, validator: ResolvedValue.repeatCountValidator).merged(with: repeatCountValue)
      case "start_delay":
        startDelayValue = deserialize(__dictValue, validator: ResolvedValue.startDelayValidator).merged(with: startDelayValue)
      case "variable_name":
        variableNameValue = deserialize(__dictValue).merged(with: variableNameValue)
      case parent?.cancelActions?.link:
        cancelActionsValue = cancelActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.direction?.link:
        directionValue = directionValue.merged(with: { deserialize(__dictValue) })
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.durationValidator) })
      case parent?.endActions?.link:
        endActionsValue = endActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.interpolator?.link:
        interpolatorValue = interpolatorValue.merged(with: { deserialize(__dictValue) })
      case parent?.repeatCount?.link:
        repeatCountValue = repeatCountValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.repeatCountValidator) })
      case parent?.startDelay?.link:
        startDelayValue = startDelayValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startDelayValidator) })
      case parent?.variableName?.link:
        variableNameValue = variableNameValue.merged(with: { deserialize(__dictValue) })
      default: break
      }
    }
    if let parent = parent {
      cancelActionsValue = cancelActionsValue.merged(with: { parent.cancelActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      endActionsValue = endActionsValue.merged(with: { parent.endActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      cancelActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "cancel_actions", error: $0) },
      directionValue.errorsOrWarnings?.map { .nestedObjectError(field: "direction", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      interpolatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "interpolator", error: $0) },
      repeatCountValue.errorsOrWarnings?.map { .nestedObjectError(field: "repeat_count", error: $0) },
      startDelayValue.errorsOrWarnings?.map { .nestedObjectError(field: "start_delay", error: $0) },
      variableNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_name", error: $0) }
    )
    if case .noValue = durationValue {
      errors.append(.requiredFieldIsMissing(field: "duration"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = variableNameValue {
      errors.append(.requiredFieldIsMissing(field: "variable_name"))
    }
    guard
      let durationNonNil = durationValue.value,
      let idNonNil = idValue.value,
      let variableNameNonNil = variableNameValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivAnimatorBase(
      cancelActions: cancelActionsValue.value,
      direction: directionValue.value,
      duration: durationNonNil,
      endActions: endActionsValue.value,
      id: idNonNil,
      interpolator: interpolatorValue.value,
      repeatCount: repeatCountValue.value,
      startDelay: startDelayValue.value,
      variableName: variableNameNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivAnimatorBaseTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivAnimatorBaseTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivAnimatorBaseTemplate(
      cancelActions: merged.cancelActions?.tryResolveParent(templates: templates),
      direction: merged.direction,
      duration: merged.duration,
      endActions: merged.endActions?.tryResolveParent(templates: templates),
      id: merged.id,
      interpolator: merged.interpolator,
      repeatCount: merged.repeatCount,
      startDelay: merged.startDelay,
      variableName: merged.variableName
    )
  }
}
