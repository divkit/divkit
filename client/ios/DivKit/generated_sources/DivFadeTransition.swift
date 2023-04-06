// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFadeTransition: DivTransitionBase {
  public static let type: String = "fade"
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 0.0
  public let duration: Expression<Int> // constraint: number >= 0; default value: 200
  public let interpolator: Expression<DivAnimationInterpolator> // default value: ease_in_out
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: alpha) ?? 0.0
  }

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: duration) ?? 200
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveStringBasedValue(expression: interpolator, initializer: DivAnimationInterpolator.init(rawValue:)) ?? DivAnimationInterpolator.easeInOut
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: startDelay) ?? 0
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let interpolatorValidator: AnyValueValidator<DivAnimationInterpolator> =
    makeNoOpValueValidator()

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    alpha: Expression<Double>? = nil,
    duration: Expression<Int>? = nil,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    startDelay: Expression<Int>? = nil
  ) {
    self.alpha = alpha ?? .value(0.0)
    self.duration = duration ?? .value(200)
    self.interpolator = interpolator ?? .value(.easeInOut)
    self.startDelay = startDelay ?? .value(0)
  }
}

#if DEBUG
extension DivFadeTransition: Equatable {
  public static func ==(lhs: DivFadeTransition, rhs: DivFadeTransition) -> Bool {
    guard
      lhs.alpha == rhs.alpha,
      lhs.duration == rhs.duration,
      lhs.interpolator == rhs.interpolator
    else {
      return false
    }
    guard
      lhs.startDelay == rhs.startDelay
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFadeTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["alpha"] = alpha.toValidSerializationValue()
    result["duration"] = duration.toValidSerializationValue()
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["start_delay"] = startDelay.toValidSerializationValue()
    return result
  }
}
