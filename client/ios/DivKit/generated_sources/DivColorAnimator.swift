// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivColorAnimator: DivAnimatorBase, Sendable {
  public static let type: String = "color_animator"
  public let cancelActions: [DivAction]?
  public let direction: Expression<DivAnimationDirection> // default value: normal
  public let duration: Expression<Int> // constraint: number >= 0
  public let endActions: [DivAction]?
  public let endValue: Expression<Color>
  public let id: String
  public let interpolator: Expression<DivAnimationInterpolator> // default value: linear
  public let repeatCount: DivCount // default value: .divFixedCount(DivFixedCount(value: .value(1)))
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0
  public let startValue: Expression<Color>?
  public let variableName: String

  public func resolveDirection(_ resolver: ExpressionResolver) -> DivAnimationDirection {
    resolver.resolveEnum(direction) ?? DivAnimationDirection.normal
  }

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(duration)
  }

  public func resolveEndValue(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(endValue)
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveEnum(interpolator) ?? DivAnimationInterpolator.linear
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(startDelay) ?? 0
  }

  public func resolveStartValue(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(startValue)
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      cancelActions: try dictionary.getOptionalArray("cancel_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      direction: try dictionary.getOptionalExpressionField("direction", context: context),
      duration: try dictionary.getExpressionField("duration", validator: Self.durationValidator, context: context),
      endActions: try dictionary.getOptionalArray("end_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      endValue: try dictionary.getExpressionField("end_value", transform: Color.color(withHexString:), context: context),
      id: try dictionary.getField("id", context: context),
      interpolator: try dictionary.getOptionalExpressionField("interpolator", context: context),
      repeatCount: try dictionary.getOptionalField("repeat_count", transform: { (dict: [String: Any]) in try DivCount(dictionary: dict, context: context) }),
      startDelay: try dictionary.getOptionalExpressionField("start_delay", validator: Self.startDelayValidator, context: context),
      startValue: try dictionary.getOptionalExpressionField("start_value", transform: Color.color(withHexString:), context: context),
      variableName: try dictionary.getField("variable_name", context: context)
    )
  }

  init(
    cancelActions: [DivAction]? = nil,
    direction: Expression<DivAnimationDirection>? = nil,
    duration: Expression<Int>,
    endActions: [DivAction]? = nil,
    endValue: Expression<Color>,
    id: String,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    repeatCount: DivCount? = nil,
    startDelay: Expression<Int>? = nil,
    startValue: Expression<Color>? = nil,
    variableName: String
  ) {
    self.cancelActions = cancelActions
    self.direction = direction ?? .value(.normal)
    self.duration = duration
    self.endActions = endActions
    self.endValue = endValue
    self.id = id
    self.interpolator = interpolator ?? .value(.linear)
    self.repeatCount = repeatCount ?? .divFixedCount(DivFixedCount(value: .value(1)))
    self.startDelay = startDelay ?? .value(0)
    self.startValue = startValue
    self.variableName = variableName
  }
}

#if DEBUG
extension DivColorAnimator: Equatable {
  public static func ==(lhs: DivColorAnimator, rhs: DivColorAnimator) -> Bool {
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

extension DivColorAnimator: Serializable {
  @_optimize(size)
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
    result["repeat_count"] = repeatCount.toDictionary()
    result["start_delay"] = startDelay.toValidSerializationValue()
    result["start_value"] = startValue?.toValidSerializationValue()
    result["variable_name"] = variableName
    return result
  }
}
