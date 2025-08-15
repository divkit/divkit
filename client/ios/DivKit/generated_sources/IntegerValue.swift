// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class IntegerValue: Sendable {
  public static let type: String = "integer"
  public let value: Expression<Int>

  public func resolveValue(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(value)
  }

  init(
    value: Expression<Int>
  ) {
    self.value = value
  }
}

#if DEBUG
extension IntegerValue: Equatable {
  public static func ==(lhs: IntegerValue, rhs: IntegerValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension IntegerValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
