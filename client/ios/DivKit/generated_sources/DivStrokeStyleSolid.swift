// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivStrokeStyleSolid: Sendable {
  public static let type: String = "solid"

  init() {}
}

#if DEBUG
extension DivStrokeStyleSolid: Equatable {
  public static func ==(lhs: DivStrokeStyleSolid, rhs: DivStrokeStyleSolid) -> Bool {
    return true
  }
}
#endif

extension DivStrokeStyleSolid: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
