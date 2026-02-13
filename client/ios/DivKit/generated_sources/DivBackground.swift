// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivBackground: Sendable {
  case divLinearGradient(DivLinearGradient)
  case divRadialGradient(DivRadialGradient)
  case divImageBackground(DivImageBackground)
  case divSolidBackground(DivSolidBackground)
  case divNinePatchBackground(DivNinePatchBackground)

  public var value: Serializable {
    switch self {
    case let .divLinearGradient(value):
      return value
    case let .divRadialGradient(value):
      return value
    case let .divImageBackground(value):
      return value
    case let .divSolidBackground(value):
      return value
    case let .divNinePatchBackground(value):
      return value
    }
  }
}

extension DivBackground {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivLinearGradient.type:
      self = .divLinearGradient(try DivLinearGradient(dictionary: dictionary, context: context))
    case DivRadialGradient.type:
      self = .divRadialGradient(try DivRadialGradient(dictionary: dictionary, context: context))
    case DivImageBackground.type:
      self = .divImageBackground(try DivImageBackground(dictionary: dictionary, context: context))
    case DivSolidBackground.type:
      self = .divSolidBackground(try DivSolidBackground(dictionary: dictionary, context: context))
    case DivNinePatchBackground.type:
      self = .divNinePatchBackground(try DivNinePatchBackground(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-background", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivBackground: Equatable {
  public static func ==(lhs: DivBackground, rhs: DivBackground) -> Bool {
    switch (lhs, rhs) {
    case let (.divLinearGradient(l), .divLinearGradient(r)):
      return l == r
    case let (.divRadialGradient(l), .divRadialGradient(r)):
      return l == r
    case let (.divImageBackground(l), .divImageBackground(r)):
      return l == r
    case let (.divSolidBackground(l), .divSolidBackground(r)):
      return l == r
    case let (.divNinePatchBackground(l), .divNinePatchBackground(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivBackground: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
