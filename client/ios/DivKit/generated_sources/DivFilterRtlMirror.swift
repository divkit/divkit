// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFilterRtlMirror: Sendable {
  public static let type: String = "rtl_mirror"

  init() {}
}

#if DEBUG
extension DivFilterRtlMirror: Equatable {
  public static func ==(lhs: DivFilterRtlMirror, rhs: DivFilterRtlMirror) -> Bool {
    return true
  }
}
#endif

extension DivFilterRtlMirror: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
