// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPageContentSize: Sendable {
  public static let type: String = "wrap_content"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

  init() {}
}

#if DEBUG
extension DivPageContentSize: Equatable {
  public static func ==(lhs: DivPageContentSize, rhs: DivPageContentSize) -> Bool {
    return true
  }
}
#endif

extension DivPageContentSize: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
