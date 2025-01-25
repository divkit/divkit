// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivStroke: Sendable {
  public let color: Expression<Color>
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let width: Expression<Double> // constraint: number >= 0; default value: 1

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(unit) ?? DivSizeUnit.dp
  }

  public func resolveWidth(_ resolver: ExpressionResolver) -> Double {
    resolver.resolveNumeric(width) ?? 1
  }

  static let widthValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    color: Expression<Color>,
    unit: Expression<DivSizeUnit>? = nil,
    width: Expression<Double>? = nil
  ) {
    self.color = color
    self.unit = unit ?? .value(.dp)
    self.width = width ?? .value(1)
  }
}

#if DEBUG
extension DivStroke: Equatable {
  public static func ==(lhs: DivStroke, rhs: DivStroke) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.unit == rhs.unit,
      lhs.width == rhs.width
    else {
      return false
    }
    return true
  }
}
#endif

extension DivStroke: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["color"] = color.toValidSerializationValue()
    result["unit"] = unit.toValidSerializationValue()
    result["width"] = width.toValidSerializationValue()
    return result
  }
}
