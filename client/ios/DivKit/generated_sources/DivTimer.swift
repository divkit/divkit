// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTimer: Sendable {
  public let duration: Expression<Int> // constraint: number >= 0; default value: 0
  public let endActions: [DivAction]?
  public let id: String
  public let tickActions: [DivAction]?
  public let tickInterval: Expression<Int>? // constraint: number > 0
  public let valueVariable: String?

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(duration) ?? 0
  }

  public func resolveTickInterval(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(tickInterval)
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let tickIntervalValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      duration: try dictionary.getOptionalExpressionField("duration", validator: Self.durationValidator, context: context),
      endActions: try dictionary.getOptionalArray("end_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      id: try dictionary.getField("id", context: context),
      tickActions: try dictionary.getOptionalArray("tick_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      tickInterval: try dictionary.getOptionalExpressionField("tick_interval", validator: Self.tickIntervalValidator, context: context),
      valueVariable: try dictionary.getOptionalField("value_variable", context: context)
    )
  }

  init(
    duration: Expression<Int>? = nil,
    endActions: [DivAction]? = nil,
    id: String,
    tickActions: [DivAction]? = nil,
    tickInterval: Expression<Int>? = nil,
    valueVariable: String? = nil
  ) {
    self.duration = duration ?? .value(0)
    self.endActions = endActions
    self.id = id
    self.tickActions = tickActions
    self.tickInterval = tickInterval
    self.valueVariable = valueVariable
  }
}

#if DEBUG
extension DivTimer: Equatable {
  public static func ==(lhs: DivTimer, rhs: DivTimer) -> Bool {
    guard
      lhs.duration == rhs.duration,
      lhs.endActions == rhs.endActions,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.tickActions == rhs.tickActions,
      lhs.tickInterval == rhs.tickInterval,
      lhs.valueVariable == rhs.valueVariable
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTimer: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["duration"] = duration.toValidSerializationValue()
    result["end_actions"] = endActions?.map { $0.toDictionary() }
    result["id"] = id
    result["tick_actions"] = tickActions?.map { $0.toDictionary() }
    result["tick_interval"] = tickInterval?.toValidSerializationValue()
    result["value_variable"] = valueVariable
    return result
  }
}
