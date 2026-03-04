// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivData: Sendable {
  public final class State: Sendable {
    public let div: Div
    public let stateId: Int

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        div: try dictionary.getField("div", transform: { (dict: [String: Any]) in try Div(dictionary: dict, context: context) }, context: context),
        stateId: try dictionary.getField("state_id", context: context)
      )
    }

    init(
      div: Div,
      stateId: Int
    ) {
      self.div = div
      self.stateId = stateId
    }
  }

  public let functions: [DivFunction]?
  public let logId: String
  public let states: [State] // at least 1 elements
  public let timers: [DivTimer]?
  public let transitionAnimationSelector: Expression<DivTransitionSelector> // default value: none
  public let variableTriggers: [DivTrigger]?
  public let variables: [DivVariable]?

  public func resolveTransitionAnimationSelector(_ resolver: ExpressionResolver) -> DivTransitionSelector {
    resolver.resolveEnum(transitionAnimationSelector) ?? DivTransitionSelector.none
  }

  static let statesValidator: AnyArrayValueValidator<DivData.State> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      functions: try dictionary.getOptionalArray("functions", transform: { (dict: [String: Any]) in try? DivFunction(dictionary: dict, context: context) }),
      logId: try dictionary.getField("log_id", context: context),
      states: try dictionary.getArray("states", transform: { (dict: [String: Any]) in try? DivData.State(dictionary: dict, context: context) }, validator: Self.statesValidator, context: context),
      timers: try dictionary.getOptionalArray("timers", transform: { (dict: [String: Any]) in try? DivTimer(dictionary: dict, context: context) }),
      transitionAnimationSelector: try dictionary.getOptionalExpressionField("transition_animation_selector", context: context),
      variableTriggers: try dictionary.getOptionalArray("variable_triggers", transform: { (dict: [String: Any]) in try? DivTrigger(dictionary: dict, context: context) }),
      variables: try dictionary.getOptionalArray("variables", transform: { (dict: [String: Any]) in try? DivVariable(dictionary: dict, context: context) })
    )
  }

  init(
    functions: [DivFunction]?,
    logId: String,
    states: [State],
    timers: [DivTimer]?,
    transitionAnimationSelector: Expression<DivTransitionSelector>?,
    variableTriggers: [DivTrigger]?,
    variables: [DivVariable]?
  ) {
    self.functions = functions
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
      lhs.functions == rhs.functions,
      lhs.logId == rhs.logId,
      lhs.states == rhs.states
    else {
      return false
    }
    guard
      lhs.timers == rhs.timers,
      lhs.transitionAnimationSelector == rhs.transitionAnimationSelector,
      lhs.variableTriggers == rhs.variableTriggers
    else {
      return false
    }
    guard
      lhs.variables == rhs.variables
    else {
      return false
    }
    return true
  }
}
#endif

extension DivData: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["functions"] = functions?.map { $0.toDictionary() }
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["div"] = div.toDictionary()
    result["state_id"] = stateId
    return result
  }
}
