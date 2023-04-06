// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivScaleTransition: DivTransitionBase {
  public static let type: String = "scale"
  public let duration: Expression<Int> // constraint: number >= 0; default value: 200
  public let interpolator: Expression<DivAnimationInterpolator> // default value: ease_in_out
  public let pivotX: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  public let pivotY: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 0.5
  public let scale: Expression<Double> // constraint: number >= 0.0; default value: 0.0
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: duration) ?? 200
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveStringBasedValue(expression: interpolator, initializer: DivAnimationInterpolator.init(rawValue:)) ?? DivAnimationInterpolator.easeInOut
  }

  public func resolvePivotX(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: pivotX) ?? 0.5
  }

  public func resolvePivotY(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: pivotY) ?? 0.5
  }

  public func resolveScale(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: scale) ?? 0.0
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: startDelay) ?? 0
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let interpolatorValidator: AnyValueValidator<DivAnimationInterpolator> =
    makeNoOpValueValidator()

  static let pivotXValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let pivotYValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let scaleValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 })

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    duration: Expression<Int>? = nil,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    pivotX: Expression<Double>? = nil,
    pivotY: Expression<Double>? = nil,
    scale: Expression<Double>? = nil,
    startDelay: Expression<Int>? = nil
  ) {
    self.duration = duration ?? .value(200)
    self.interpolator = interpolator ?? .value(.easeInOut)
    self.pivotX = pivotX ?? .value(0.5)
    self.pivotY = pivotY ?? .value(0.5)
    self.scale = scale ?? .value(0.0)
    self.startDelay = startDelay ?? .value(0)
  }
}

#if DEBUG
extension DivScaleTransition: Equatable {
  public static func ==(lhs: DivScaleTransition, rhs: DivScaleTransition) -> Bool {
    guard
      lhs.duration == rhs.duration,
      lhs.interpolator == rhs.interpolator,
      lhs.pivotX == rhs.pivotX
    else {
      return false
    }
    guard
      lhs.pivotY == rhs.pivotY,
      lhs.scale == rhs.scale,
      lhs.startDelay == rhs.startDelay
    else {
      return false
    }
    return true
  }
}
#endif

extension DivScaleTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["duration"] = duration.toValidSerializationValue()
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["pivot_x"] = pivotX.toValidSerializationValue()
    result["pivot_y"] = pivotY.toValidSerializationValue()
    result["scale"] = scale.toValidSerializationValue()
    result["start_delay"] = startDelay.toValidSerializationValue()
    return result
  }
}
