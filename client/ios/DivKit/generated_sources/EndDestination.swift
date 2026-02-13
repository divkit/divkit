// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class EndDestination: Sendable {
  public static let type: String = "end"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
