// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionDictSetValue: Sendable {
  public static let type: String = "dict_set_value"
  public let key: Expression<String>
  public let value: DivTypedValue?
  public let variableName: Expression<String>

  public func resolveKey(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(key)
  }

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(variableName)
  }

  init(
    key: Expression<String>,
    value: DivTypedValue? = nil,
    variableName: Expression<String>
  ) {
    self.key = key
    self.value = value
    self.variableName = variableName
  }
}

#if DEBUG
extension DivActionDictSetValue: Equatable {
  public static func ==(lhs: DivActionDictSetValue, rhs: DivActionDictSetValue) -> Bool {
    guard
      lhs.key == rhs.key,
      lhs.value == rhs.value,
      lhs.variableName == rhs.variableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionDictSetValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["key"] = key.toValidSerializationValue()
    result["value"] = value?.toDictionary()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
