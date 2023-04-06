// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivInfinityCount {
  public static let type: String = "infinity"

  init() {}
}

#if DEBUG
extension DivInfinityCount: Equatable {
  public static func ==(lhs: DivInfinityCount, rhs: DivInfinityCount) -> Bool {
    return true
  }
}
#endif

extension DivInfinityCount: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
