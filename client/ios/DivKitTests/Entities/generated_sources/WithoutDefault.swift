// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class WithoutDefault: Sendable {
  public static let type: String = "non_default"

  init() {}
}

#if DEBUG
extension WithoutDefault: Equatable {
  public static func ==(lhs: WithoutDefault, rhs: WithoutDefault) -> Bool {
    return true
  }
}
#endif

extension WithoutDefault: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
