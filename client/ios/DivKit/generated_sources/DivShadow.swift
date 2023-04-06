// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivShadow {
  public let alpha: Expression<Double> // constraint: number >= 0.0 && number <= 1.0; default value: 0.19
  public let blur: Expression<Int> // constraint: number >= 0; default value: 2
  public let color: Expression<Color> // default value: #000000
  public let offset: DivPoint

  public func resolveAlpha(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumericValue(expression: alpha) ?? 0.19
  }

  public func resolveBlur(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: blur) ?? 2
  }

  public func resolveColor(_ resolver: ExpressionResolver) -> Color {
    resolver.resolveStringBasedValue(expression: color, initializer: Color.color(withHexString:)) ?? Color.colorWithARGBHexCode(0xFF000000)
  }

  static let alphaValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

  static let blurValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let colorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  init(
    alpha: Expression<Double>? = nil,
    blur: Expression<Int>? = nil,
    color: Expression<Color>? = nil,
    offset: DivPoint
  ) {
    self.alpha = alpha ?? .value(0.19)
    self.blur = blur ?? .value(2)
    self.color = color ?? .value(Color.colorWithARGBHexCode(0xFF000000))
    self.offset = offset
  }
}

#if DEBUG
extension DivShadow: Equatable {
  public static func ==(lhs: DivShadow, rhs: DivShadow) -> Bool {
    guard
      lhs.alpha == rhs.alpha,
      lhs.blur == rhs.blur,
      lhs.color == rhs.color
    else {
      return false
    }
    guard
      lhs.offset == rhs.offset
    else {
      return false
    }
    return true
  }
}
#endif

extension DivShadow: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["alpha"] = alpha.toValidSerializationValue()
    result["blur"] = blur.toValidSerializationValue()
    result["color"] = color.toValidSerializationValue()
    result["offset"] = offset.toDictionary()
    return result
  }
}
