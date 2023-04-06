// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTrigger {
  @frozen
  public enum Mode: String, CaseIterable {
    case onCondition = "on_condition"
    case onVariable = "on_variable"
  }

  public let actions: [DivAction] // at least 1 elements
  public let condition: Expression<Bool>
  public let mode: Expression<Mode> // default value: on_condition

  public func resolveCondition(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumericValue(expression: condition)
  }

  public func resolveMode(_ resolver: ExpressionResolver) -> Mode {
    resolver.resolveStringBasedValue(expression: mode, initializer: Mode.init(rawValue:)) ?? Mode.onCondition
  }

  static let actionsValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let modeValidator: AnyValueValidator<DivTrigger.Mode> =
    makeNoOpValueValidator()

  init(
    actions: [DivAction],
    condition: Expression<Bool>,
    mode: Expression<Mode>? = nil
  ) {
    self.actions = actions
    self.condition = condition
    self.mode = mode ?? .value(.onCondition)
  }
}

#if DEBUG
extension DivTrigger: Equatable {
  public static func ==(lhs: DivTrigger, rhs: DivTrigger) -> Bool {
    guard
      lhs.actions == rhs.actions,
      lhs.condition == rhs.condition,
      lhs.mode == rhs.mode
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTrigger: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["actions"] = actions.map { $0.toDictionary() }
    result["condition"] = condition.toValidSerializationValue()
    result["mode"] = mode.toValidSerializationValue()
    return result
  }
}
