// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivActionArrayInsertValue {
  public static let type: String = "array_insert_value"
  public let index: Expression<Int>?
  public let value: DivTypedValue
  public let variableName: Expression<String> // at least 1 char

  public func resolveIndex(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: index)
  }

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: variableName, initializer: { $0 })
  }

  static let variableNameValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    index: Expression<Int>? = nil,
    value: DivTypedValue,
    variableName: Expression<String>
  ) {
    self.index = index
    self.value = value
    self.variableName = variableName
  }
}

#if DEBUG
extension DivActionArrayInsertValue: Equatable {
  public static func ==(lhs: DivActionArrayInsertValue, rhs: DivActionArrayInsertValue) -> Bool {
    guard
      lhs.index == rhs.index,
      lhs.value == rhs.value,
      lhs.variableName == rhs.variableName
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionArrayInsertValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["index"] = index?.toValidSerializationValue()
    result["value"] = value.toDictionary()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
