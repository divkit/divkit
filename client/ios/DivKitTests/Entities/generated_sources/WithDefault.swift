// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class WithDefault: Sendable {
  public static let type: String = "default"

  init() {}
}

#if DEBUG
extension WithDefault: Equatable {
  public static func ==(lhs: WithDefault, rhs: WithDefault) -> Bool {
    return true
  }
}
#endif

extension WithDefault: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
