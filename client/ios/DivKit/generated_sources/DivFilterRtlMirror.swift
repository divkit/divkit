// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFilterRtlMirror {
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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
