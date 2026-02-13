// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivAppearanceTransition: Sendable {
  case divAppearanceSetTransition(DivAppearanceSetTransition)
  case divFadeTransition(DivFadeTransition)
  case divScaleTransition(DivScaleTransition)
  case divSlideTransition(DivSlideTransition)

  public var value: Serializable {
    switch self {
    case let .divAppearanceSetTransition(value):
      return value
    case let .divFadeTransition(value):
      return value
    case let .divScaleTransition(value):
      return value
    case let .divSlideTransition(value):
      return value
    }
  }
}

extension DivAppearanceTransition {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivAppearanceSetTransition.type:
      self = .divAppearanceSetTransition(try DivAppearanceSetTransition(dictionary: dictionary, context: context))
    case DivFadeTransition.type:
      self = .divFadeTransition(try DivFadeTransition(dictionary: dictionary, context: context))
    case DivScaleTransition.type:
      self = .divScaleTransition(try DivScaleTransition(dictionary: dictionary, context: context))
    case DivSlideTransition.type:
      self = .divSlideTransition(try DivSlideTransition(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-appearance-transition", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivAppearanceTransition: Equatable {
  public static func ==(lhs: DivAppearanceTransition, rhs: DivAppearanceTransition) -> Bool {
    switch (lhs, rhs) {
    case let (.divAppearanceSetTransition(l), .divAppearanceSetTransition(r)):
      return l == r
    case let (.divFadeTransition(l), .divFadeTransition(r)):
      return l == r
    case let (.divScaleTransition(l), .divScaleTransition(r)):
      return l == r
    case let (.divSlideTransition(l), .divSlideTransition(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivAppearanceTransition: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
