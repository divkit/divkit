// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class ArrayValue {
  public static let type: String = "array"
  public let value: Expression<[Any]>

  public func resolveValue(_ resolver: ExpressionResolver) -> [Any]? {
    resolver.resolveArray(value)
  }

  init(
    value: Expression<[Any]>
  ) {
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [Any] in class fields
extension ArrayValue: Equatable {
  public static func ==(lhs: ArrayValue, rhs: ArrayValue) -> Bool {
    return true
  }
}
#endif

extension ArrayValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
