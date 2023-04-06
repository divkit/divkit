// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivSlideTransition: DivTransitionBase {
  @frozen
  public enum Edge: String, CaseIterable {
    case left = "left"
    case top = "top"
    case right = "right"
    case bottom = "bottom"
  }

  public static let type: String = "slide"
  public let distance: DivDimension?
  public let duration: Expression<Int> // constraint: number >= 0; default value: 200
  public let edge: Expression<Edge> // default value: bottom
  public let interpolator: Expression<DivAnimationInterpolator> // default value: ease_in_out
  public let startDelay: Expression<Int> // constraint: number >= 0; default value: 0

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: duration) ?? 200
  }

  public func resolveEdge(_ resolver: ExpressionResolver) -> Edge {
    resolver.resolveStringBasedValue(expression: edge, initializer: Edge.init(rawValue:)) ?? Edge.bottom
  }

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveStringBasedValue(expression: interpolator, initializer: DivAnimationInterpolator.init(rawValue:)) ?? DivAnimationInterpolator.easeInOut
  }

  public func resolveStartDelay(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: startDelay) ?? 0
  }

  static let distanceValidator: AnyValueValidator<DivDimension> =
    makeNoOpValueValidator()

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let edgeValidator: AnyValueValidator<DivSlideTransition.Edge> =
    makeNoOpValueValidator()

  static let interpolatorValidator: AnyValueValidator<DivAnimationInterpolator> =
    makeNoOpValueValidator()

  static let startDelayValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    distance: DivDimension? = nil,
    duration: Expression<Int>? = nil,
    edge: Expression<Edge>? = nil,
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    startDelay: Expression<Int>? = nil
  ) {
    self.distance = distance
    self.duration = duration ?? .value(200)
    self.edge = edge ?? .value(.bottom)
    self.interpolator = interpolator ?? .value(.easeInOut)
    self.startDelay = startDelay ?? .value(0)
  }
}

#if DEBUG
extension DivSlideTransition: Equatable {
  public static func ==(lhs: DivSlideTransition, rhs: DivSlideTransition) -> Bool {
    guard
      lhs.distance == rhs.distance,
      lhs.duration == rhs.duration,
      lhs.edge == rhs.edge
    else {
      return false
    }
    guard
      lhs.interpolator == rhs.interpolator,
      lhs.startDelay == rhs.startDelay
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSlideTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["distance"] = distance?.toDictionary()
    result["duration"] = duration.toValidSerializationValue()
    result["edge"] = edge.toValidSerializationValue()
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["start_delay"] = startDelay.toValidSerializationValue()
    return result
  }
}
