// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivInfinityCount {
  public static let type: String = "infinity"

  init() {}
}

#if DEBUG
extension DivInfinityCount: Equatable {
  public static func ==(_: DivInfinityCount, _: DivInfinityCount) -> Bool {
    true
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
