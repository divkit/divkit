// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAnimation {
  @frozen
  public enum Name: String, CaseIterable {
    case fade = "fade"
    case translate = "translate"
    case scale = "scale"
    case native = "native"
    case set = "set"
    case noAnimation = "no_animation"
  }

  public let duration: Expression<Int> // constraint: number >= 0; default value: 300
  public let endValue: Expression<Double>?
  public let interpolator: Expression<DivAnimationInterpolator> // default value: spring
  public let items: [DivAnimation]? // at least 1 elements
  public let name: Expression<Name>
  public let repeatCount: DivCount // default value: .divInfinityCount(DivInfinityCount())
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0
  public let startValue: Expression<Double>?

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: duration) ?? 300
  }

  public func resolveEndValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: endValue)
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveStringBasedValue(expression: interpolator, initializer: DivAnimationInterpolator.init(rawValue:)) ?? DivAnimationInterpolator.spring
  }

  public func resolveName(_ resolver: ExpressionResolver) -> Name? {
    resolver.resolveStringBasedValue(expression: name, initializer: Name.init(rawValue:))
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: startDelay) ?? 0
  }

  public func resolveStartValue(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: startValue)
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let interpolatorValidator: AnyValueValidator<DivAnimationInterpolator> =
    makeNoOpValueValidator()

  static let itemsValidator: AnyArrayValueValidator<DivAnimation> =
    makeArrayValidator(minItems: 1)

  static let repeatCountValidator: AnyValueValidator<DivCount> =
    makeNoOpValueValidator()

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    duration: Expression<Int>? = nil,
    endValue: Expression<Double>? = nil,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    items: [DivAnimation]? = nil,
    name: Expression<Name>,
    repeatCount: DivCount? = nil,
    startDelay: Expression<Int>? = nil,
    startValue: Expression<Double>? = nil
  ) {
    self.duration = duration ?? .value(300)
    self.endValue = endValue
    self.interpolator = interpolator ?? .value(.spring)
    self.items = items
    self.name = name
    self.repeatCount = repeatCount ?? .divInfinityCount(DivInfinityCount())
    self.startDelay = startDelay ?? .value(0)
    self.startValue = startValue
  }
}

#if DEBUG
extension DivAnimation: Equatable {
  public static func ==(lhs: DivAnimation, rhs: DivAnimation) -> Bool {
    guard
      lhs.duration == rhs.duration,
      lhs.endValue == rhs.endValue,
      lhs.interpolator == rhs.interpolator
    else {
      return false
    }
    guard
      lhs.items == rhs.items,
      lhs.name == rhs.name,
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

extension DivAnimation: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["duration"] = duration.toValidSerializationValue()
    result["end_value"] = endValue?.toValidSerializationValue()
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["items"] = items?.map { $0.toDictionary() }
    result["name"] = name.toValidSerializationValue()
    result["repeat"] = repeatCount.toDictionary()
    result["start_delay"] = startDelay.toValidSerializationValue()
    result["start_value"] = startValue?.toValidSerializationValue()
    return result
  }
}
