// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivFilter: Sendable {
  case divBlur(DivBlur)
  case divFilterRtlMirror(DivFilterRtlMirror)

  public var value: Serializable {
    switch self {
    case let .divBlur(value):
      return value
    case let .divFilterRtlMirror(value):
      return value
    }
  }
}

#if DEBUG
extension DivFilter: Equatable {
  public static func ==(lhs: DivFilter, rhs: DivFilter) -> Bool {
    switch (lhs, rhs) {
    case let (.divBlur(l), .divBlur(r)):
      return l == r
    case let (.divFilterRtlMirror(l), .divFilterRtlMirror(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivFilter: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
