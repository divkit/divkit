// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DictValue: @unchecked Sendable {
  public static let type: String = "dict"
  public let value: Expression<[String: Any]>

  public func resolveValue(_ resolver: ExpressionResolver) -> [String: Any]? {
    resolver.resolveDict(value)
  }

  init(
    value: Expression<[String: Any]>
  ) {
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DictValue: Equatable {
  public static func ==(lhs: DictValue, rhs: DictValue) -> Bool {
    return true
  }
}
#endif

extension DictValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
