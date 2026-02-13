// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivStrokeStyleDashed: Sendable {
  public static let type: String = "dashed"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

  init() {}
}

#if DEBUG
extension DivStrokeStyleDashed: Equatable {
  public static func ==(lhs: DivStrokeStyleDashed, rhs: DivStrokeStyleDashed) -> Bool {
    return true
  }
}
#endif

extension DivStrokeStyleDashed: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
