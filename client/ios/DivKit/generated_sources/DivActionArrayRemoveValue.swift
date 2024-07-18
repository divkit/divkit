// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionArrayRemoveValue {
  public static let type: String = "array_remove_value"
  public let index: Expression<Int>
  public let variableName: Expression<String>

  public func resolveIndex(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(index)
  }

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(variableName)
  }

  init(
    index: Expression<Int>,
    variableName: Expression<String>
  ) {
    self.index = index
    self.variableName = variableName
  }
}

#if DEBUG
extension DivActionArrayRemoveValue: Equatable {
  public static func ==(lhs: DivActionArrayRemoveValue, rhs: DivActionArrayRemoveValue) -> Bool {
    guard
      lhs.index == rhs.index,
      lhs.variableName == rhs.variableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionArrayRemoveValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["index"] = index.toValidSerializationValue()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
