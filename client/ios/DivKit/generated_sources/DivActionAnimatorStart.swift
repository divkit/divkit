// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionAnimatorStart {
  public static let type: String = "animator_start"
  public let animatorId: Expression<String>
  public let direction: Expression<DivAnimationDirection>?
  public let duration: Expression<Int>? // constraint: number >= 0
  public let endValue: DivTypedValue?
  public let interpolator: Expression<DivAnimationInterpolator>?
  public let repeatCount: Expression<Int>? // constraint: number >= 0
  public let startDelay: Expression<Int>? // constraint: number >= 0
  public let startValue: DivTypedValue?

  public func resolveAnimatorId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(animatorId)
  }

  public func resolveDirection(_ resolver: ExpressionResolver) -> DivAnimationDirection? {
    resolver.resolveEnum(direction)
  }

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(duration)
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator? {
    resolver.resolveEnum(interpolator)
  }

  public func resolveRepeatCount(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(repeatCount)
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(startDelay)
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let repeatCountValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    animatorId: Expression<String>,
    direction: Expression<DivAnimationDirection>? = nil,
    duration: Expression<Int>? = nil,
    endValue: DivTypedValue? = nil,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    repeatCount: Expression<Int>? = nil,
    startDelay: Expression<Int>? = nil,
    startValue: DivTypedValue? = nil
  ) {
    self.animatorId = animatorId
    self.direction = direction
    self.duration = duration
    self.endValue = endValue
    self.interpolator = interpolator
    self.repeatCount = repeatCount
    self.startDelay = startDelay
    self.startValue = startValue
  }
}

#if DEBUG
extension DivActionAnimatorStart: Equatable {
  public static func ==(lhs: DivActionAnimatorStart, rhs: DivActionAnimatorStart) -> Bool {
    guard
      lhs.animatorId == rhs.animatorId,
      lhs.direction == rhs.direction,
      lhs.duration == rhs.duration
    else {
      return false
    }
    guard
      lhs.endValue == rhs.endValue,
      lhs.interpolator == rhs.interpolator,
      lhs.repeatCount == rhs.repeatCount
    else {
      return false
    }
    guard
      lhs.startDelay == rhs.startDelay,
      lhs.startValue == rhs.startValue
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionAnimatorStart: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["animator_id"] = animatorId.toValidSerializationValue()
    result["direction"] = direction?.toValidSerializationValue()
    result["duration"] = duration?.toValidSerializationValue()
    result["end_value"] = endValue?.toDictionary()
    result["interpolator"] = interpolator?.toValidSerializationValue()
    result["repeat_count"] = repeatCount?.toValidSerializationValue()
    result["start_delay"] = startDelay?.toValidSerializationValue()
    result["start_value"] = startValue?.toDictionary()
    return result
  }
}
