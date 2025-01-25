// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivDataTemplate: TemplateValue {
  public final class StateTemplate: TemplateValue {
    public let div: Field<DivTemplate>?
    public let stateId: Field<Int>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        div: dictionary.getOptionalField("div", templateToType: templateToType),
        stateId: dictionary.getOptionalField("state_id")
      )
    }

    init(
      div: Field<DivTemplate>? = nil,
      stateId: Field<Int>? = nil
    ) {
      self.div = div
      self.stateId = stateId
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: StateTemplate?) -> DeserializationResult<DivData.State> {
      let divValue = { parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let stateIdValue = { parent?.stateId?.resolveValue(context: context) ?? .noValue }()
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
        stateIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "state_id", error: $0) }
      )
      if case .noValue = divValue {
        errors.append(.requiredFieldIsMissing(field: "div"))
      }
      if case .noValue = stateIdValue {
        errors.append(.requiredFieldIsMissing(field: "state_id"))
      }
      guard
        let divNonNil = divValue.value,
        let stateIdNonNil = stateIdValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivData.State(
        div: { divNonNil }(),
        stateId: { stateIdNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: StateTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivData.State> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var divValue: DeserializationResult<Div> = .noValue
      var stateIdValue: DeserializationResult<Int> = { parent?.stateId?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "div" {
             divValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: divValue)
            }
          }()
          _ = {
            if key == "state_id" {
             stateIdValue = deserialize(__dictValue).merged(with: stateIdValue)
            }
          }()
          _ = {
           if key == parent?.div?.link {
             divValue = divValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.stateId?.link {
             stateIdValue = stateIdValue.merged(with: { deserialize(__dictValue) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { divValue = divValue.merged(with: { parent.div?.resolveValue(context: context, useOnlyLinks: true) }) }()
      }
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
        stateIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "state_id", error: $0) }
      )
      if case .noValue = divValue {
        errors.append(.requiredFieldIsMissing(field: "div"))
      }
      if case .noValue = stateIdValue {
        errors.append(.requiredFieldIsMissing(field: "state_id"))
      }
      guard
        let divNonNil = divValue.value,
        let stateIdNonNil = stateIdValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivData.State(
        div: { divNonNil }(),
        stateId: { stateIdNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> StateTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> StateTemplate {
      let merged = try mergedWithParent(templates: templates)

      return StateTemplate(
        div: try merged.div?.resolveParent(templates: templates),
        stateId: merged.stateId
      )
    }
  }

  public let logId: Field<String>?
  public let states: Field<[StateTemplate]>? // at least 1 elements
  public let timers: Field<[DivTimerTemplate]>?
  public let transitionAnimationSelector: Field<Expression<DivTransitionSelector>>? // default value: none
  public let variableTriggers: Field<[DivTriggerTemplate]>?
  public let variables: Field<[DivVariableTemplate]>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      logId: dictionary.getOptionalField("log_id"),
      states: dictionary.getOptionalArray("states", templateToType: templateToType),
      timers: dictionary.getOptionalArray("timers", templateToType: templateToType),
      transitionAnimationSelector: dictionary.getOptionalExpressionField("transition_animation_selector"),
      variableTriggers: dictionary.getOptionalArray("variable_triggers", templateToType: templateToType),
      variables: dictionary.getOptionalArray("variables", templateToType: templateToType)
    )
  }

  init(
    logId: Field<String>? = nil,
    states: Field<[StateTemplate]>? = nil,
    timers: Field<[DivTimerTemplate]>? = nil,
    transitionAnimationSelector: Field<Expression<DivTransitionSelector>>? = nil,
    variableTriggers: Field<[DivTriggerTemplate]>? = nil,
    variables: Field<[DivVariableTemplate]>? = nil
  ) {
    self.logId = logId
    self.states = states
    self.timers = timers
    self.transitionAnimationSelector = transitionAnimationSelector
    self.variableTriggers = variableTriggers
    self.variables = variables
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivDataTemplate?) -> DeserializationResult<DivData> {
    let logIdValue = { parent?.logId?.resolveValue(context: context) ?? .noValue }()
    let statesValue = { parent?.states?.resolveValue(context: context, validator: ResolvedValue.statesValidator, useOnlyLinks: true) ?? .noValue }()
    let timersValue = { parent?.timers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionAnimationSelectorValue = { parent?.transitionAnimationSelector?.resolveOptionalValue(context: context) ?? .noValue }()
    let variableTriggersValue = { parent?.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let variablesValue = { parent?.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      statesValue.errorsOrWarnings?.map { .nestedObjectError(field: "states", error: $0) },
      timersValue.errorsOrWarnings?.map { .nestedObjectError(field: "timers", error: $0) },
      transitionAnimationSelectorValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_animation_selector", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) }
    )
    if case .noValue = logIdValue {
      errors.append(.requiredFieldIsMissing(field: "log_id"))
    }
    if case .noValue = statesValue {
      errors.append(.requiredFieldIsMissing(field: "states"))
    }
    guard
      let logIdNonNil = logIdValue.value,
      let statesNonNil = statesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivData(
      logId: { logIdNonNil }(),
      states: { statesNonNil }(),
      timers: { timersValue.value }(),
      transitionAnimationSelector: { transitionAnimationSelectorValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivDataTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivData> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var logIdValue: DeserializationResult<String> = { parent?.logId?.value() ?? .noValue }()
    var statesValue: DeserializationResult<[DivData.State]> = .noValue
    var timersValue: DeserializationResult<[DivTimer]> = .noValue
    var transitionAnimationSelectorValue: DeserializationResult<Expression<DivTransitionSelector>> = { parent?.transitionAnimationSelector?.value() ?? .noValue }()
    var variableTriggersValue: DeserializationResult<[DivTrigger]> = .noValue
    var variablesValue: DeserializationResult<[DivVariable]> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "log_id" {
           logIdValue = deserialize(__dictValue).merged(with: logIdValue)
          }
        }()
        _ = {
          if key == "states" {
           statesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.statesValidator, type: DivDataTemplate.StateTemplate.self).merged(with: statesValue)
          }
        }()
        _ = {
          if key == "timers" {
           timersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTimerTemplate.self).merged(with: timersValue)
          }
        }()
        _ = {
          if key == "transition_animation_selector" {
           transitionAnimationSelectorValue = deserialize(__dictValue).merged(with: transitionAnimationSelectorValue)
          }
        }()
        _ = {
          if key == "variable_triggers" {
           variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).merged(with: variableTriggersValue)
          }
        }()
        _ = {
          if key == "variables" {
           variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).merged(with: variablesValue)
          }
        }()
        _ = {
         if key == parent?.logId?.link {
           logIdValue = logIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.states?.link {
           statesValue = statesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.statesValidator, type: DivDataTemplate.StateTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.timers?.link {
           timersValue = timersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTimerTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionAnimationSelector?.link {
           transitionAnimationSelectorValue = transitionAnimationSelectorValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.variableTriggers?.link {
           variableTriggersValue = variableTriggersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.variables?.link {
           variablesValue = variablesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { statesValue = statesValue.merged(with: { parent.states?.resolveValue(context: context, validator: ResolvedValue.statesValidator, useOnlyLinks: true) }) }()
      _ = { timersValue = timersValue.merged(with: { parent.timers?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variableTriggersValue = variableTriggersValue.merged(with: { parent.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variablesValue = variablesValue.merged(with: { parent.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      logIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "log_id", error: $0) },
      statesValue.errorsOrWarnings?.map { .nestedObjectError(field: "states", error: $0) },
      timersValue.errorsOrWarnings?.map { .nestedObjectError(field: "timers", error: $0) },
      transitionAnimationSelectorValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_animation_selector", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) }
    )
    if case .noValue = logIdValue {
      errors.append(.requiredFieldIsMissing(field: "log_id"))
    }
    if case .noValue = statesValue {
      errors.append(.requiredFieldIsMissing(field: "states"))
    }
    guard
      let logIdNonNil = logIdValue.value,
      let statesNonNil = statesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivData(
      logId: { logIdNonNil }(),
      states: { statesNonNil }(),
      timers: { timersValue.value }(),
      transitionAnimationSelector: { transitionAnimationSelectorValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivDataTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivDataTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivDataTemplate(
      logId: merged.logId,
      states: try merged.states?.resolveParent(templates: templates),
      timers: merged.timers?.tryResolveParent(templates: templates),
      transitionAnimationSelector: merged.transitionAnimationSelector,
      variableTriggers: merged.variableTriggers?.tryResolveParent(templates: templates),
      variables: merged.variables?.tryResolveParent(templates: templates)
    )
  }
}
