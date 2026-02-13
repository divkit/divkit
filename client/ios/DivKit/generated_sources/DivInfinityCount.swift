// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivInfinityCount: Sendable {
  public static let type: String = "infinity"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
