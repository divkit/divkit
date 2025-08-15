// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPageTransformationOverlap: Sendable {
  public static let type: String = "overlap"
  public let interpolator: Expression<DivAnimationInterpolator> // default value: ease_in_out
  public let nextPageAlpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let nextPageScale: Expression<Double> // constraint: number >= 0.0; default value: 1.0
  public let previousPageAlpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let previousPageScale: Expression<Double> // constraint: number >= 0.0; default value: 1.0
  public let reversedStackingOrder: Expression<Bool> // default value: false

  public func resolveInterpolator(_ resolver: ExpressionResolver) -> DivAnimationInterpolator {
    resolver.resolveEnum(interpolator) ?? DivAnimationInterpolator.easeInOut
  }

  public func resolveNextPageAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(nextPageAlpha) ?? 1.0
  }

  public func resolveNextPageScale(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(nextPageScale) ?? 1.0
  }

  public func resolvePreviousPageAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(previousPageAlpha) ?? 1.0
  }

  public func resolvePreviousPageScale(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(previousPageScale) ?? 1.0
  }

  public func resolveReversedStackingOrder(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(reversedStackingOrder) ?? false
  }

  static let nextPageAlphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let nextPageScaleValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 })

  static let previousPageAlphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let previousPageScaleValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 })

  init(
    interpolator: Expression<DivAnimationInterpolator>? = nil,
    nextPageAlpha: Expression<Double>? = nil,
    nextPageScale: Expression<Double>? = nil,
    previousPageAlpha: Expression<Double>? = nil,
    previousPageScale: Expression<Double>? = nil,
    reversedStackingOrder: Expression<Bool>? = nil
  ) {
    self.interpolator = interpolator ?? .value(.easeInOut)
    self.nextPageAlpha = nextPageAlpha ?? .value(1.0)
    self.nextPageScale = nextPageScale ?? .value(1.0)
    self.previousPageAlpha = previousPageAlpha ?? .value(1.0)
    self.previousPageScale = previousPageScale ?? .value(1.0)
    self.reversedStackingOrder = reversedStackingOrder ?? .value(false)
  }
}

#if DEBUG
extension DivPageTransformationOverlap: Equatable {
  public static func ==(lhs: DivPageTransformationOverlap, rhs: DivPageTransformationOverlap) -> Bool {
    guard
      lhs.interpolator == rhs.interpolator,
      lhs.nextPageAlpha == rhs.nextPageAlpha,
      lhs.nextPageScale == rhs.nextPageScale
    else {
      return false
    }
    guard
      lhs.previousPageAlpha == rhs.previousPageAlpha,
      lhs.previousPageScale == rhs.previousPageScale,
      lhs.reversedStackingOrder == rhs.reversedStackingOrder
    else {
      return false
    }
    return true
  }
}
#endif

extension DivPageTransformationOverlap: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["interpolator"] = interpolator.toValidSerializationValue()
    result["next_page_alpha"] = nextPageAlpha.toValidSerializationValue()
    result["next_page_scale"] = nextPageScale.toValidSerializationValue()
    result["previous_page_alpha"] = previousPageAlpha.toValidSerializationValue()
    result["previous_page_scale"] = previousPageScale.toValidSerializationValue()
    result["reversed_stacking_order"] = reversedStackingOrder.toValidSerializationValue()
    return result
  }
}
