// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionUpdateStructure: Sendable {
  public static let type: String = "update_structure"
  public let path: Expression<String>
  public let value: DivTypedValue
  public let variableName: Expression<String>

  public func resolvePath(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(path)
  }

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(variableName)
  }

  init(
    path: Expression<String>,
    value: DivTypedValue,
    variableName: Expression<String>
  ) {
    self.path = path
    self.value = value
    self.variableName = variableName
  }
}

#if DEBUG
extension DivActionUpdateStructure: Equatable {
  public static func ==(lhs: DivActionUpdateStructure, rhs: DivActionUpdateStructure) -> Bool {
    guard
      lhs.path == rhs.path,
      lhs.value == rhs.value,
      lhs.variableName == rhs.variableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionUpdateStructure: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["path"] = path.toValidSerializationValue()
    result["value"] = value.toDictionary()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
