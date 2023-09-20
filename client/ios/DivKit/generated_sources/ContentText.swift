// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class ContentText {
  public static let type: String = "text"
  public let value: Expression<String>

  public func resolveValue(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: value, initializer: { $0 })
  }

  init(
    value: Expression<String>
  ) {
    self.value = value
  }
}

#if DEBUG
extension ContentText: Equatable {
  public static func ==(lhs: ContentText, rhs: ContentText) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension ContentText: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
