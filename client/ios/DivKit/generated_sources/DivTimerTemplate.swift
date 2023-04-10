// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTimerTemplate: TemplateValue {
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let endActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let id: Field<String>? // at least 1 char
  public let tickActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let tickInterval: Field<Expression<Int>>? // constraint: number > 0
  public let valueVariable: Field<String>? // at least 1 char

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        duration: try dictionary.getOptionalExpressionField("duration"),
        endActions: try dictionary.getOptionalArray("end_actions", templateToType: templateToType),
        id: try dictionary.getOptionalField("id"),
        tickActions: try dictionary.getOptionalArray("tick_actions", templateToType: templateToType),
        tickInterval: try dictionary.getOptionalExpressionField("tick_interval"),
        valueVariable: try dictionary.getOptionalField("value_variable")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-timer_template." + field, representation: representation)
    }
  }

  init(
    duration: Field<Expression<Int>>? = nil,
    endActions: Field<[DivActionTemplate]>? = nil,
    id: Field<String>? = nil,
    tickActions: Field<[DivActionTemplate]>? = nil,
    tickInterval: Field<Expression<Int>>? = nil,
    valueVariable: Field<String>? = nil
  ) {
    self.duration = duration
    self.endActions = endActions
    self.id = id
    self.tickActions = tickActions
    self.tickInterval = tickInterval
    self.valueVariable = valueVariable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTimerTemplate?) -> DeserializationResult<DivTimer> {
    let durationValue = parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let endActionsValue = parent?.endActions?.resolveOptionalValue(context: context, validator: ResolvedValue.endActionsValidator, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let tickActionsValue = parent?.tickActions?.resolveOptionalValue(context: context, validator: ResolvedValue.tickActionsValidator, useOnlyLinks: true) ?? .noValue
    let tickIntervalValue = parent?.tickInterval?.resolveOptionalValue(context: context, validator: ResolvedValue.tickIntervalValidator) ?? .noValue
    let valueVariableValue = parent?.valueVariable?.resolveOptionalValue(context: context, validator: ResolvedValue.valueVariableValidator) ?? .noValue
    var errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      tickActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_actions", error: $0) },
      tickIntervalValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_interval", error: $0) },
      valueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "value_variable", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTimer(
      duration: durationValue.value,
      endActions: endActionsValue.value,
      id: idNonNil,
      tickActions: tickActionsValue.value,
      tickInterval: tickIntervalValue.value,
      valueVariable: valueVariableValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTimerTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTimer> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var endActionsValue: DeserializationResult<[DivAction]> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var tickActionsValue: DeserializationResult<[DivAction]> = .noValue
    var tickIntervalValue: DeserializationResult<Expression<Int>> = parent?.tickInterval?.value() ?? .noValue
    var valueVariableValue: DeserializationResult<String> = parent?.valueVariable?.value(validatedBy: ResolvedValue.valueVariableValidator) ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "end_actions":
        endActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.endActionsValidator, type: DivActionTemplate.self).merged(with: endActionsValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "tick_actions":
        tickActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tickActionsValidator, type: DivActionTemplate.self).merged(with: tickActionsValue)
      case "tick_interval":
        tickIntervalValue = deserialize(__dictValue, validator: ResolvedValue.tickIntervalValidator).merged(with: tickIntervalValue)
      case "value_variable":
        valueVariableValue = deserialize(__dictValue, validator: ResolvedValue.valueVariableValidator).merged(with: valueVariableValue)
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.endActions?.link:
        endActionsValue = endActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.endActionsValidator, type: DivActionTemplate.self))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.tickActions?.link:
        tickActionsValue = tickActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tickActionsValidator, type: DivActionTemplate.self))
      case parent?.tickInterval?.link:
        tickIntervalValue = tickIntervalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.tickIntervalValidator))
      case parent?.valueVariable?.link:
        valueVariableValue = valueVariableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.valueVariableValidator))
      default: break
      }
    }
    if let parent = parent {
      endActionsValue = endActionsValue.merged(with: parent.endActions?.resolveOptionalValue(context: context, validator: ResolvedValue.endActionsValidator, useOnlyLinks: true))
      tickActionsValue = tickActionsValue.merged(with: parent.tickActions?.resolveOptionalValue(context: context, validator: ResolvedValue.tickActionsValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      endActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "end_actions", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      tickActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_actions", error: $0) },
      tickIntervalValue.errorsOrWarnings?.map { .nestedObjectError(field: "tick_interval", error: $0) },
      valueVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "value_variable", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    guard
      let idNonNil = idValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTimer(
      duration: durationValue.value,
      endActions: endActionsValue.value,
      id: idNonNil,
      tickActions: tickActionsValue.value,
      tickInterval: tickIntervalValue.value,
      valueVariable: valueVariableValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTimerTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTimerTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTimerTemplate(
      duration: merged.duration,
      endActions: merged.endActions?.tryResolveParent(templates: templates),
      id: merged.id,
      tickActions: merged.tickActions?.tryResolveParent(templates: templates),
      tickInterval: merged.tickInterval,
      valueVariable: merged.valueVariable
    )
  }
}
