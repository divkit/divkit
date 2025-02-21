// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivLinearGradient: Sendable {
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

  public static let type: String = "gradient"
  public let angle: Expression<Int> // constraint: number >= 0 && number <= 360; default value: 0
  public let colorMap: [ColorPoint]? // at least 2 elements
  public let colors: [Expression<Color>]? // at least 2 elements

  public func resolveAngle(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(angle) ?? 0
  }

  public func resolveColors(_ resolver: ExpressionResolver) -> [Color]? {
    colors?.map { resolver.resolveColor($0) }.compactMap { $0 }
  }

  static let angleValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 && $0 <= 360 })

  static let colorMapValidator: AnyArrayValueValidator<DivLinearGradient.ColorPoint> =
    makeArrayValidator(minItems: 2)

  static let colorsValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 2)

  init(
    angle: Expression<Int>? = nil,
    colorMap: [ColorPoint]? = nil,
    colors: [Expression<Color>]? = nil
  ) {
    self.angle = angle ?? .value(0)
    self.colorMap = colorMap
    self.colors = colors
  }
}

#if DEBUG
extension DivLinearGradient: Equatable {
  public static func ==(lhs: DivLinearGradient, rhs: DivLinearGradient) -> Bool {
    guard
      lhs.angle == rhs.angle,
      lhs.colorMap == rhs.colorMap,
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
    result["color_map"] = colorMap?.map { $0.toDictionary() }
    result["colors"] = colors?.map { $0.toValidSerializationValue() }
    return result
  }
}

#if DEBUG
extension DivLinearGradient.ColorPoint: Equatable {
  public static func ==(lhs: DivLinearGradient.ColorPoint, rhs: DivLinearGradient.ColorPoint) -> Bool {
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

extension DivLinearGradient.ColorPoint: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["color"] = color.toValidSerializationValue()
    result["position"] = position.toValidSerializationValue()
    return result
  }
}
