// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInputFilterRegex {
  public static let type: String = "regex"
  public let pattern: Expression<String>

  public func resolvePattern(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(pattern)
  }

  init(
    pattern: Expression<String>
  ) {
    self.pattern = pattern
  }
}

#if DEBUG
extension DivInputFilterRegex: Equatable {
  public static func ==(lhs: DivInputFilterRegex, rhs: DivInputFilterRegex) -> Bool {
    guard
      lhs.pattern == rhs.pattern
    else {
      return false
    }
    return true
  }
}
#endif

extension DivInputFilterRegex: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["pattern"] = pattern.toValidSerializationValue()
    return result
  }
}
