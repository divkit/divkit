// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoDataStream {
  public static let type: String = "stream"
  public let url: Expression<URL>

  public func resolveUrl(_ resolver: ExpressionResolver) -> URL? {
    resolver.resolveStringBasedValue(expression: url, initializer: URL.init(string:))
  }

  init(
    url: Expression<URL>
  ) {
    self.url = url
  }
}

#if DEBUG
extension DivVideoDataStream: Equatable {
  public static func ==(lhs: DivVideoDataStream, rhs: DivVideoDataStream) -> Bool {
    guard
      lhs.url == rhs.url
    else {
      return false
    }
    return true
  }
}
#endif

extension DivVideoDataStream: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["url"] = url.toValidSerializationValue()
    return result
  }
}
