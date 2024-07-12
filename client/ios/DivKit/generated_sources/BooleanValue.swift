// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class BooleanValue {
  public static let type: String = "boolean"
  public let value: Expression<Bool>

  public func resolveValue(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(value)
  }

  init(
    value: Expression<Bool>
  ) {
    self.value = value
  }
}

#if DEBUG
extension BooleanValue: Equatable {
  public static func ==(lhs: BooleanValue, rhs: BooleanValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension BooleanValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
