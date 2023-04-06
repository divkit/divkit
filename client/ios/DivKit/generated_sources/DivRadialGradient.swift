// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivRadialGradient {
  public static let type: String = "radial_gradient"
  public let centerX: DivRadialGradientCenter // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let centerY: DivRadialGradientCenter // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let colors: [Expression<Color>] // at least 2 elements
  public let radius: DivRadialGradientRadius // default value: .divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius(value: .value(.farthestCorner)))

  public func resolveColors(_ resolver: ExpressionResolver) -> [Color]? {
    colors.map { resolver.resolveStringBasedValue(expression: $0, initializer: Color.color(withHexString:)) }.compactMap { $0 }
  }

  static let centerXValidator: AnyValueValidator<DivRadialGradientCenter> =
    makeNoOpValueValidator()

  static let centerYValidator: AnyValueValidator<DivRadialGradientCenter> =
    makeNoOpValueValidator()

  static let colorsValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 2)

  static let radiusValidator: AnyValueValidator<DivRadialGradientRadius> =
    makeNoOpValueValidator()

  init(
    centerX: DivRadialGradientCenter? = nil,
    centerY: DivRadialGradientCenter? = nil,
    colors: [Expression<Color>],
    radius: DivRadialGradientRadius? = nil
  ) {
    self.centerX = centerX ?? .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
    self.centerY = centerY ?? .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
    self.colors = colors
    self.radius = radius ?? .divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius(value: .value(.farthestCorner)))
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
    result["center_x"] = centerX.toDictionary()
    result["center_y"] = centerY.toDictionary()
    result["colors"] = colors.map { $0.toValidSerializationValue() }
    result["radius"] = radius.toDictionary()
    return result
  }
}
