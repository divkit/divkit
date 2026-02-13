// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionCustom: Sendable {
  public static let type: String = "custom"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

  init() {}
}

#if DEBUG
extension DivActionCustom: Equatable {
  public static func ==(lhs: DivActionCustom, rhs: DivActionCustom) -> Bool {
    return true
  }
}
#endif

extension DivActionCustom: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
