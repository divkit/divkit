// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class WithDefault {
  public static let type: String = "default"

  public init(dictionary: [String: Any]) throws {}

  init() {}
}

#if DEBUG
extension WithDefault: Equatable {
  public static func ==(lhs: WithDefault, rhs: WithDefault) -> Bool {
    return true
  }
}
#endif

extension WithDefault: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
