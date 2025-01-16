// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTextRangeBackground: Sendable {
  case divSolidBackground(DivSolidBackground)
  case divCloudBackground(DivCloudBackground)

  public var value: Serializable {
    switch self {
    case let .divSolidBackground(value):
      return value
    case let .divCloudBackground(value):
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
    case let (.divCloudBackground(l), .divCloudBackground(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTextRangeBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
