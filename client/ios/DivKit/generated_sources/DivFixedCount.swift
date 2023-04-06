// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFixedCount {
  public static let type: String = "fixed"
  public let value: Expression<Int> // constraint: number >= 0

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: value)
  }

  static let valueValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    value: Expression<Int>
  ) {
    self.value = value
  }
}

#if DEBUG
extension DivFixedCount: Equatable {
  public static func ==(lhs: DivFixedCount, rhs: DivFixedCount) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFixedCount: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
