// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivTextRangeBackground {
  case divSolidBackground(DivSolidBackground)

  public var value: Serializable {
    switch self {
    case let .divSolidBackground(value):
      return value
    }
  }
}

#if DEBUG
extension DivTextRangeBackground: Equatable {
  public static func ==(lhs: DivTextRangeBackground, rhs: DivTextRangeBackground) -> Bool {
    switch (lhs, rhs) {
    case let (.divSolidBackground(l), .divSolidBackground(r)):
      return l == r
    }
  }
}
#endif

extension DivTextRangeBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
