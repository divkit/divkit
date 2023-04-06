// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivLinearGradient {
  public static let type: String = "gradient"
  public let angle: Expression<Int> // constraint: number >= 0 && number <= 360; default value: 0
  public let colors: [Expression<Color>] // at least 2 elements

  public func resolveAngle(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: angle) ?? 0
  }

  public func resolveColors(_ resolver: ExpressionResolver) -> [Color]? {
    colors.map { resolver.resolveStringBasedValue(expression: $0, initializer: Color.color(withHexString:)) }.compactMap { $0 }
  }

  static let angleValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 && $0 <= 360 })

  static let colorsValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 2)

  init(
    angle: Expression<Int>? = nil,
    colors: [Expression<Color>]
  ) {
    self.angle = angle ?? .value(0)
    self.colors = colors
  }
}

#if DEBUG
extension DivLinearGradient: Equatable {
  public static func ==(lhs: DivLinearGradient, rhs: DivLinearGradient) -> Bool {
    guard
      lhs.angle == rhs.angle,
      lhs.colors == rhs.colors
    else {
      return false
    }
    return true
  }
}
#endif

extension DivLinearGradient: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["angle"] = angle.toValidSerializationValue()
    result["colors"] = colors.map { $0.toValidSerializationValue() }
    return result
  }
}
