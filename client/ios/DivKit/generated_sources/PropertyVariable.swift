// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class PropertyVariable: Sendable {
  public static let type: String = "property"
  public let get: Expression<String>
  public let name: String
  public let newValueVariableName: String // default value: new_value
  public let set: [DivAction]?
  public let valueType: Expression<DivEvaluableType>

  public func resolveGet(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(get)
  }

  public func resolveValueType(_ resolver: ExpressionResolver) -> DivEvaluableType? {
    resolver.resolveEnum(valueType)
  }

  init(
    get: Expression<String>,
    name: String,
    newValueVariableName: String? = nil,
    set: [DivAction]? = nil,
    valueType: Expression<DivEvaluableType>
  ) {
    self.get = get
    self.name = name
    self.newValueVariableName = newValueVariableName ?? "new_value"
    self.set = set
    self.valueType = valueType
  }
}

#if DEBUG
extension PropertyVariable: Equatable {
  public static func ==(lhs: PropertyVariable, rhs: PropertyVariable) -> Bool {
    guard
      lhs.get == rhs.get,
      lhs.name == rhs.name,
      lhs.newValueVariableName == rhs.newValueVariableName
    else {
      return false
    }
    guard
      lhs.set == rhs.set,
      lhs.valueType == rhs.valueType
    else {
      return false
    }
    return true
  }
}
#endif

extension PropertyVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["get"] = get.toValidSerializationValue()
    result["name"] = name
    result["new_value_variable_name"] = newValueVariableName
    result["set"] = set?.map { $0.toDictionary() }
    result["value_type"] = valueType.toValidSerializationValue()
    return result
  }
}
