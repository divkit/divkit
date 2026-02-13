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

extension DivTextRangeMask {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivTextRangeMaskParticles.type:
      self = .divTextRangeMaskParticles(try DivTextRangeMaskParticles(dictionary: dictionary, context: context))
    case DivTextRangeMaskSolid.type:
      self = .divTextRangeMaskSolid(try DivTextRangeMaskSolid(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-text-range-mask", representation: dictionary)
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
