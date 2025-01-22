// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class StartDestination: Sendable {
  public static let type: String = "start"

  init() {}
}

#if DEBUG
extension StartDestination: Equatable {
  public static func ==(lhs: StartDestination, rhs: StartDestination) -> Bool {
    return true
  }
}
#endif

extension StartDestination: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
