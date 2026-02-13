// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivShape: Sendable {
  case divRoundedRectangleShape(DivRoundedRectangleShape)
  case divCircleShape(DivCircleShape)

  public var value: Serializable {
    switch self {
    case let .divRoundedRectangleShape(value):
      return value
    case let .divCircleShape(value):
      return value
    }
  }
}

extension DivShape {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivRoundedRectangleShape.type:
      self = .divRoundedRectangleShape(try DivRoundedRectangleShape(dictionary: dictionary, context: context))
    case DivCircleShape.type:
      self = .divCircleShape(try DivCircleShape(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-shape", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivShape: Equatable {
  public static func ==(lhs: DivShape, rhs: DivShape) -> Bool {
    switch (lhs, rhs) {
    case let (.divRoundedRectangleShape(l), .divRoundedRectangleShape(r)):
      return l == r
    case let (.divCircleShape(l), .divCircleShape(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivShape: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
