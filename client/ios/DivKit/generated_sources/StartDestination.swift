// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class StartDestination: Sendable {
  public static let type: String = "start"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
