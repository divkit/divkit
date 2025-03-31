// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRadialGradient: Sendable {
  public final class ColorPoint: Sendable {
    public let color: Expression<Color>
    public let position: Expression<Double> // constraint: number >= 0.0 && number <= 1.0

    public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
      resolver.resolveColor(color)
    }

    public func resolvePosition(_ resolver: ExpressionResolver) -> Double? {
      resolver.resolveNumeric(position)
    }

    static let positionValidator: AnyValueValidator<Double> =
      makeValueValidator(valueValidator: { $0 >= 0.0 && $0 <= 1.0 })

    init(
      color: Expression<Color>,
      position: Expression<Double>
    ) {
      self.color = color
      self.position = position
    }
  }

  public static let type: String = "radial_gradient"
  public let centerX: DivRadialGradientCenter // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let centerY: DivRadialGradientCenter // default value: .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
  public let colorMap: [ColorPoint]? // at least 2 elements
  public let colors: [Expression<Color>] // at least 2 elements
  public let radius: DivRadialGradientRadius // default value: .divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius(value: .value(.farthestCorner)))

  public func resolveColors(_ resolver: ExpressionResolver) -> [Color]? {
    colors.map { resolver.resolveColor($0) }.compactMap { $0 }
  }

  static let colorMapValidator: AnyArrayValueValidator<DivRadialGradient.ColorPoint> =
    makeArrayValidator(minItems: 2)

  static let colorsValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 2)

  init(
    centerX: DivRadialGradientCenter? = nil,
    centerY: DivRadialGradientCenter? = nil,
    colorMap: [ColorPoint]? = nil,
    colors: [Expression<Color>],
    radius: DivRadialGradientRadius? = nil
  ) {
    self.centerX = centerX ?? .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
    self.centerY = centerY ?? .divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter(value: .value(0.5)))
    self.colorMap = colorMap
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
      lhs.colorMap == rhs.colorMap
    else {
      return false
    }
    guard
      lhs.colors == rhs.colors,
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
    result["color_map"] = colorMap?.map { $0.toDictionary() }
    result["colors"] = colors.map { $0.toValidSerializationValue() }
    result["radius"] = radius.toDictionary()
    return result
  }
}

#if DEBUG
extension DivRadialGradient.ColorPoint: Equatable {
  public static func ==(lhs: DivRadialGradient.ColorPoint, rhs: DivRadialGradient.ColorPoint) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.position == rhs.position
    else {
      return false
    }
    return true
  }
}
#endif

extension DivRadialGradient.ColorPoint: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["color"] = color.toValidSerializationValue()
    result["position"] = position.toValidSerializationValue()
    return result
  }
}
