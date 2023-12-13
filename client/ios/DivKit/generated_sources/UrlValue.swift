// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class UrlValue {
  public static let type: String = "url"
  public let value: Expression<URL>

  public func resolveValue(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveUrl(value)
  }

  init(
    value: Expression<URL>
  ) {
    self.value = value
  }
}

#if DEBUG
extension UrlValue: Equatable {
  public static func ==(lhs: UrlValue, rhs: UrlValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension UrlValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
