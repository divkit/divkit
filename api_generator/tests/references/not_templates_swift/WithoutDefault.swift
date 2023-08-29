// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class WithoutDefault {
  public static let type: String = "non_default"

  init() {}
}

#if DEBUG
extension WithoutDefault: Equatable {
  public static func ==(lhs: WithoutDefault, rhs: WithoutDefault) -> Bool {
    return true
  }
}
#endif

extension WithoutDefault: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
