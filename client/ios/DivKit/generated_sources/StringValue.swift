// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class StringValue: Sendable {
  public static let type: String = "string"
  public let value: Expression<String>

  public func resolveValue(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      value: try dictionary.getExpressionField("value", context: context)
    )
  }

  init(
    value: Expression<String>
  ) {
    self.value = value
  }
}

#if DEBUG
extension StringValue: Equatable {
  public static func ==(lhs: StringValue, rhs: StringValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension StringValue: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
