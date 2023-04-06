// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTextRangeBorder {
  public let cornerRadius: Expression<Int>? // constraint: number >= 0
  public let stroke: DivStroke?

  public func resolveCornerRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: cornerRadius)
  }

  static let cornerRadiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let strokeValidator: AnyValueValidator<DivStroke> =
    makeNoOpValueValidator()

  init(
    cornerRadius: Expression<Int>? = nil,
    stroke: DivStroke? = nil
  ) {
    self.cornerRadius = cornerRadius
    self.stroke = stroke
  }
}

#if DEBUG
extension DivTextRangeBorder: Equatable {
  public static func ==(lhs: DivTextRangeBorder, rhs: DivTextRangeBorder) -> Bool {
    guard
      lhs.cornerRadius == rhs.cornerRadius,
      lhs.stroke == rhs.stroke
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTextRangeBorder: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["corner_radius"] = cornerRadius?.toValidSerializationValue()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
