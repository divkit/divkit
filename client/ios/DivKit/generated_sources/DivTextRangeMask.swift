// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTextRangeMask: Sendable {
  case divTextRangeMaskParticles(DivTextRangeMaskParticles)
  case divTextRangeMaskSolid(DivTextRangeMaskSolid)

  public var value: Serializable {
    switch self {
    case let .divTextRangeMaskParticles(value):
      return value
    case let .divTextRangeMaskSolid(value):
      return value
    }
  }
}

#if DEBUG
extension DivTextRangeMask: Equatable {
  public static func ==(lhs: DivTextRangeMask, rhs: DivTextRangeMask) -> Bool {
    switch (lhs, rhs) {
    case let (.divTextRangeMaskParticles(l), .divTextRangeMaskParticles(r)):
      return l == r
    case let (.divTextRangeMaskSolid(l), .divTextRangeMaskSolid(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTextRangeMask: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
