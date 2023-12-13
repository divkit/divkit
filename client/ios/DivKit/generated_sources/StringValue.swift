// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class StringValue {
  public static let type: String = "string"
  public let value: Expression<String>

  public func resolveValue(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(value, initializer: { $0 })
  }

  init(
    value: Expression<String>
  ) {
    self.value = value
  }
}

#if DEBUG
extension StringValue: Equatable {
  public static func ==(lhs: StringValue, rhs: StringValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension StringValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
