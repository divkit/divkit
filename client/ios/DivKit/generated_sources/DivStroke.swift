// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivStroke {
  public let color: Expression<Color>
  public let unit: Expression<DivSizeUnit> // default value: dp
  public let width: Expression<Int> // constraint: number >= 0; default value: 1

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: color, initializer: Color.color(withHexString:))
  }

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveStringBasedValue(expression: unit, initializer: DivSizeUnit.init(rawValue:)) ?? DivSizeUnit.dp
  }

  public func resolveWidth(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: width) ?? 1
  }

  static let unitValidator: AnyValueValidator<DivSizeUnit> =
    makeNoOpValueValidator()

  static let widthValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    color: Expression<Color>,
    unit: Expression<DivSizeUnit>? = nil,
    width: Expression<Int>? = nil
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
