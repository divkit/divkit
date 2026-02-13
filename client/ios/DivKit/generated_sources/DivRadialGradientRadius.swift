// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivRadialGradientRadius: Sendable {
  case divFixedSize(DivFixedSize)
  case divRadialGradientRelativeRadius(DivRadialGradientRelativeRadius)

  public var value: Serializable {
    switch self {
    case let .divFixedSize(value):
      return value
    case let .divRadialGradientRelativeRadius(value):
      return value
    }
  }
}

extension DivRadialGradientRadius {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivFixedSize.type:
      self = .divFixedSize(try DivFixedSize(dictionary: dictionary, context: context))
    case DivRadialGradientRelativeRadius.type:
      self = .divRadialGradientRelativeRadius(try DivRadialGradientRelativeRadius(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-radial-gradient-radius", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivRadialGradientRadius: Equatable {
  public static func ==(lhs: DivRadialGradientRadius, rhs: DivRadialGradientRadius) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedSize(l), .divFixedSize(r)):
      return l == r
    case let (.divRadialGradientRelativeRadius(l), .divRadialGradientRelativeRadius(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivRadialGradientRadius: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
