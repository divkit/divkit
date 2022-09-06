// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivRadialGradient {
  public static let type: String = "radial_gradient"
  public let centerX: Expression<Double> // default value: 0.5
  public let centerY: Expression<Double> // default value: 0.5
  public let colors: [Expression<Color>] // at least 2 elements
  public let radius: Expression<Int> // constraint: number > 0

  public func resolveCenterX(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: centerX) ?? 0.5
  }

  public func resolveCenterY(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: centerY) ?? 0.5
  }

  public func resolveColors(_ resolver: ExpressionResolver) -> [Color]? {
    colors.map { resolver.resolveStringBasedValue(expression: $0, initializer: Color.color(withHexString:)) }.compactMap { $0 }
  }

  public func resolveRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: radius)
  }

  static let colorsValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 2)

  static let radiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    centerX: Expression<Double>? = nil,
    centerY: Expression<Double>? = nil,
    colors: [Expression<Color>],
    radius: Expression<Int>
  ) {
    self.centerX = centerX ?? .value(0.5)
    self.centerY = centerY ?? .value(0.5)
    self.colors = colors
    self.radius = radius
  }
}

#if DEBUG
extension DivRadialGradient: Equatable {
  public static func ==(lhs: DivRadialGradient, rhs: DivRadialGradient) -> Bool {
    guard
      lhs.centerX == rhs.centerX,
      lhs.centerY == rhs.centerY,
      lhs.colors == rhs.colors
    else {
      return false
    }
    guard
      lhs.radius == rhs.radius
    else {
      return false
    }
    return true
  }
}
#endif

extension DivRadialGradient: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["center_x"] = centerX.toValidSerializationValue()
    result["center_y"] = centerY.toValidSerializationValue()
    result["colors"] = colors.map { $0.toValidSerializationValue() }
    result["radius"] = radius.toValidSerializationValue()
    return result
  }
}
