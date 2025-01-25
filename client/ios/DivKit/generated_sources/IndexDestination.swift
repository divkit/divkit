// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class IndexDestination {
  public static let type: String = "index"
  public let value: Expression<Int> // constraint: number >= 0

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(value)
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
extension IndexDestination: Equatable {
  public static func ==(lhs: IndexDestination, rhs: IndexDestination) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension IndexDestination: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
