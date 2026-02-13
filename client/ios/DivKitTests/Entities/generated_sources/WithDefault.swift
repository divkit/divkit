// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class WithDefault: Sendable {
  public static let type: String = "default"

  public init(dictionary: [String: Any], context: ParsingContext) throws {}

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    return result
  }
}
