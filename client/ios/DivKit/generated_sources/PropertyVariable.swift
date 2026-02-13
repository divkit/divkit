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
  public let valueType: DivEvaluableType

  public func resolveGet(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(get)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      get: try dictionary.getExpressionField("get", context: context),
      name: try dictionary.getField("name", context: context),
      newValueVariableName: try dictionary.getOptionalField("new_value_variable_name", context: context),
      set: try dictionary.getOptionalArray("set", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      valueType: try dictionary.getField("value_type", context: context)
    )
  }

  init(
    get: Expression<String>,
    name: String,
    newValueVariableName: String? = nil,
    set: [DivAction]? = nil,
    valueType: DivEvaluableType
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["get"] = get.toValidSerializationValue()
    result["name"] = name
    result["new_value_variable_name"] = newValueVariableName
    result["set"] = set?.map { $0.toDictionary() }
    result["value_type"] = valueType.rawValue
    return result
  }
}
