// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class EndDestination: Sendable {
  public static let type: String = "end"

  init() {}
}

#if DEBUG
extension EndDestination: Equatable {
  public static func ==(lhs: EndDestination, rhs: EndDestination) -> Bool {
    return true
  }
}
#endif

extension EndDestination: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
