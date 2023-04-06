// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivFilter {
  case divBlur(DivBlur)

  public var value: Serializable {
    switch self {
    case let .divBlur(value):
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
    }
  }
}
#endif

extension DivFilter: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
