// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetVariable: Sendable {
  public static let type: String = "set_variable"
  public let value: DivTypedValue
  public let variableName: Expression<String>

  public func resolveVariableName(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(variableName)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      value: try dictionary.getField("value", transform: { (dict: [String: Any]) in try DivTypedValue(dictionary: dict, context: context) }, context: context),
      variableName: try dictionary.getExpressionField("variable_name", context: context)
    )
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toDictionary()
    result["variable_name"] = variableName.toValidSerializationValue()
    return result
  }
}
