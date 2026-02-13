// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class UrlVariable: Sendable {
  public static let type: String = "url"
  public let name: String
  public let value: Expression<URL>

  public func resolveValue(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      name: try dictionary.getField("name", context: context),
      value: try dictionary.getExpressionField("value", transform: URL.makeFromNonEncodedString, context: context)
    )
  }

  init(
    name: String,
    value: Expression<URL>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension UrlVariable: Equatable {
  public static func ==(lhs: UrlVariable, rhs: UrlVariable) -> Bool {
    guard
      lhs.name == rhs.name,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension UrlVariable: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
