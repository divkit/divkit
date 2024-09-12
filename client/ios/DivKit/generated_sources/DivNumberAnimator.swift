// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivNumberAnimator {
  public static let type: String = "number_animator"
  public let cancelActions: [DivAction]?
  public let direction: Expression<DivAnimationDirection> // default value: normal
  public let duration: Expression<Int> // constraint: number >= 0
  public let endActions: [DivAction]?
  public let endValue: Expression<Double>
  public let id: String
  public let interpolator: Expression<DivAnimationInterpolator> // default value: linear
  public let repeatCount: Expression<Int> // constraint: number >= 0; default value: 1
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0
  public let startValue: Expression<Double>?
  public let variableName: String

  public func resolveDirection(_ resolver: ExpressionResolver) -> DivAnimationDirection {
    resolver.resolveEnum(direction) ?? DivAnimationDirection.normal
  }

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(duration)
  }

  public func resolveEndValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(endValue)
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveEnum(interpolator) ?? DivAnimationInterpolator.linear
  }

  public func resolveRepeatCount(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(repeatCount) ?? 1
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(startDelay) ?? 0
  }

  public func resolveStartValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(startValue)
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let repeatCountValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    cancelActions: [DivAction]? = nil,
    direction: Expression<DivAnimationDirection>? = nil,
    duration: Expression<Int>,
    endActions: [DivAction]? = nil,
    endValue: Expression<Double>,
    id: String,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    repeatCount: Expression<Int>? = nil,
    startDelay: Expression<Int>? = nil,
    startValue: Expression<Double>? = nil,
    variableName: String
  ) {
    self.cancelActions = cancelActions
    self.direction = direction ?? .value(.normal)
    self.duration = duration
    self.endActions = endActions
    self.endValue = endValue
    self.id = id
    self.interpolator = interpolator ?? .value(.linear)
    self.repeatCount = repeatCount ?? .value(1)
    self.startDelay = startDelay ?? .value(0)
    self.startValue = startValue
    self.variableName = variableName
  }
}

#if DEBUG
extension DivNumberAnimator: Equatable {
  public static func ==(lhs: DivNumberAnimator, rhs: DivNumberAnimator) -> Bool {
    guard
      lhs.cancelActions == rhs.cancelActions,
      lhs.direction == rhs.direction,
      lhs.duration == rhs.duration
    else {
      return false
    }
    guard
      lhs.endActions == rhs.endActions,
      lhs.endValue == rhs.endValue,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.interpolator == rhs.interpolator,
      lhs.repeatCount == rhs.repeatCount,
      lhs.startDelay == rhs.startDelay
    else {
      return false
    }
    guard
      lhs.startValue == rhs.startValue,
      lhs.variableName == rhs.variableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivNumberAnimator: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["cancel_actions"] = cancelActions?.map { $0.toDictionary() }
    result["direction"] = direction.toValidSerializationValue()
    result["duration"] = duration.toValidSerializationValue()
    result["end_actions"] = endActions?.map { $0.toDictionary() }
    result["end_value"] = endValue.toValidSerializationValue()
    result["id"] = id
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["repeat_count"] = repeatCount.toValidSerializationValue()
    result["start_delay"] = startDelay.toValidSerializationValue()
    result["start_value"] = startValue?.toValidSerializationValue()
    result["variable_name"] = variableName
    return result
  }
}
