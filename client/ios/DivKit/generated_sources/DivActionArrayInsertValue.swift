// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionArrayInsertValue: Sendable {
  public static let type: String = "array_insert_value"
  public let index: Expression<Int>?
  public let value: DivTypedValue
  public let variableName: Expression<String>

  public func resolveIndex(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(index)
  }

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(variableName)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      index: try dictionary.getOptionalExpressionField("index", context: context),
      value: try dictionary.getField("value", transform: { (dict: [String: Any]) in try DivTypedValue(dictionary: dict, context: context) }, context: context),
      variableName: try dictionary.getExpressionField("variable_name", context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["index"] = index?.toValidSerializationValue()
    result["value"] = value.toDictionary()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
