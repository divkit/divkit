// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivChangeBoundsTransition: DivTransitionBase {
  public static let type: String = "change_bounds"
  public let duration: Expression<Int> // constraint: number >= 0; default value: 200
  public let interpolator: Expression<DivAnimationInterpolator> // default value: ease_in_out
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: duration) ?? 200
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveStringBasedValue(expression: interpolator, initializer: DivAnimationInterpolator.init(rawValue:)) ?? DivAnimationInterpolator.easeInOut
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: startDelay) ?? 0
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let interpolatorValidator: AnyValueValidator<DivAnimationInterpolator> =
    makeNoOpValueValidator()

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    duration: Expression<Int>? = nil,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    startDelay: Expression<Int>? = nil
  ) {
    self.duration = duration ?? .value(200)
    self.interpolator = interpolator ?? .value(.easeInOut)
    self.startDelay = startDelay ?? .value(0)
  }
}

#if DEBUG
extension DivChangeBoundsTransition: Equatable {
  public static func ==(lhs: DivChangeBoundsTransition, rhs: DivChangeBoundsTransition) -> Bool {
    guard
      lhs.duration == rhs.duration,
      lhs.interpolator == rhs.interpolator,
      lhs.startDelay == rhs.startDelay
    else {
      return false
    }
    return true
  }
}
#endif

extension DivChangeBoundsTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["duration"] = duration.toValidSerializationValue()
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["start_delay"] = startDelay.toValidSerializationValue()
    return result
  }
}
