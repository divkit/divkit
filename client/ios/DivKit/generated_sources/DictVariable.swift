// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DictVariable: @unchecked Sendable {
  public static let type: String = "dict"
  public let name: String
  public let value: Expression<[String: Any]>

  public func resolveValue(_ resolver: ExpressionResolver) -> [String: Any]? {
    resolver.resolveDict(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      name: try dictionary.getField("name", context: context),
      value: try dictionary.getExpressionField("value", context: context)
    )
  }

  init(
    name: String,
    value: Expression<[String: Any]>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [String: Any] in class fields
extension DictVariable: Equatable {
  public static func ==(lhs: DictVariable, rhs: DictVariable) -> Bool {
    guard
      lhs.name == rhs.name
    else {
      return false
    }
    return true
  }
}
#endif

extension DictVariable: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
