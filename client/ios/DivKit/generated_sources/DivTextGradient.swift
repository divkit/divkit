// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTextGradient: Sendable {
  case divLinearGradient(DivLinearGradient)
  case divRadialGradient(DivRadialGradient)

  public var value: Serializable {
    switch self {
    case let .divLinearGradient(value):
      return value
    case let .divRadialGradient(value):
      return value
    }
  }
}

extension DivTextGradient {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivLinearGradient.type:
      self = .divLinearGradient(try DivLinearGradient(dictionary: dictionary, context: context))
    case DivRadialGradient.type:
      self = .divRadialGradient(try DivRadialGradient(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-text-gradient", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivTextGradient: Equatable {
  public static func ==(lhs: DivTextGradient, rhs: DivTextGradient) -> Bool {
    switch (lhs, rhs) {
    case let (.divLinearGradient(l), .divLinearGradient(r)):
      return l == r
    case let (.divRadialGradient(l), .divRadialGradient(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTextGradient: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
