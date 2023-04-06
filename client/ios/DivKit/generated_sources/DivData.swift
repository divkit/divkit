// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivData {
  public final class State {
    public let div: Div
    public let stateId: Int

    init(
      div: Div,
      stateId: Int
    ) {
      self.div = div
      self.stateId = stateId
    }
  }

  public let logId: String // at least 1 char
  public let states: [State] // at least 1 elements; all received elements must be valid
  public let timers: [DivTimer]? // at least 1 elements
  public let transitionAnimationSelector: Expression<DivTransitionSelector> // default value: none
  public let variableTriggers: [DivTrigger]? // at least 1 elements
  public let variables: [DivVariable]? // at least 1 elements

  public func resolveTransitionAnimationSelector(_ resolver: ExpressionResolver) -> DivTransitionSelector {
    resolver.resolveStringBasedValue(expression: transitionAnimationSelector, initializer: DivTransitionSelector.init(rawValue:)) ?? DivTransitionSelector.none
  }

  static let logIdValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  static let statesValidator: AnyArrayValueValidator<DivData.State> =
    makeStrictArrayValidator(minItems: 1)

  static let timersValidator: AnyArrayValueValidator<DivTimer> =
    makeArrayValidator(minItems: 1)

  static let transitionAnimationSelectorValidator: AnyValueValidator<DivTransitionSelector> =
    makeNoOpValueValidator()

  static let variableTriggersValidator: AnyArrayValueValidator<DivTrigger> =
    makeArrayValidator(minItems: 1)

  static let variablesValidator: AnyArrayValueValidator<DivVariable> =
    makeArrayValidator(minItems: 1)

  init(
    logId: String,
    states: [State],
    timers: [DivTimer]?,
    transitionAnimationSelector: Expression<DivTransitionSelector>?,
    variableTriggers: [DivTrigger]?,
    variables: [DivVariable]?
  ) {
    self.logId = logId
    self.states = states
    self.timers = timers
    self.transitionAnimationSelector = transitionAnimationSelector ?? .value(.none)
    self.variableTriggers = variableTriggers
    self.variables = variables
  }
}

#if DEBUG
extension DivData: Equatable {
  public static func ==(lhs: DivData, rhs: DivData) -> Bool {
    guard
      lhs.logId == rhs.logId,
      lhs.states == rhs.states,
      lhs.timers == rhs.timers
    else {
      return false
    }
    guard
      lhs.transitionAnimationSelector == rhs.transitionAnimationSelector,
      lhs.variableTriggers == rhs.variableTriggers,
      lhs.variables == rhs.variables
    else {
      return false
    }
    return true
  }
}
#endif

extension DivData: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["log_id"] = logId
    result["states"] = states.map { $0.toDictionary() }
    result["timers"] = timers?.map { $0.toDictionary() }
    result["transition_animation_selector"] = transitionAnimationSelector.toValidSerializationValue()
    result["variable_triggers"] = variableTriggers?.map { $0.toDictionary() }
    result["variables"] = variables?.map { $0.toDictionary() }
    return result
  }
}

#if DEBUG
extension DivData.State: Equatable {
  public static func ==(lhs: DivData.State, rhs: DivData.State) -> Bool {
    guard
      lhs.div == rhs.div,
      lhs.stateId == rhs.stateId
    else {
      return false
    }
    return true
  }
}
#endif

extension DivData.State: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["div"] = div.toDictionary()
    result["state_id"] = stateId
    return result
  }
}
