// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivActionSetVariable {
  public static let type: String = "set_variable"
  public let value: DivTypedValue
  public let variableName: Expression<String>

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(variableName, initializer: { $0 })
  }

  init(
    value: DivTypedValue,
    variableName: Expression<String>
  ) {
    self.value = value
    self.variableName = variableName
  }
}

#if DEBUG
extension DivActionSetVariable: Equatable {
  public static func ==(lhs: DivActionSetVariable, rhs: DivActionSetVariable) -> Bool {
    guard
      lhs.value == rhs.value,
      lhs.variableName == rhs.variableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionSetVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toDictionary()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
