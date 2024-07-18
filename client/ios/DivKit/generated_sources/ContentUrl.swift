// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class ContentUrl {
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
extension ContentUrl: Equatable {
  public static func ==(lhs: ContentUrl, rhs: ContentUrl) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension ContentUrl: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
