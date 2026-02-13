// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivDrawable: Sendable {
  case divShapeDrawable(DivShapeDrawable)

  public var value: Serializable {
    switch self {
    case let .divShapeDrawable(value):
      return value
    }
  }
}

extension DivDrawable {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivShapeDrawable.type:
      self = .divShapeDrawable(try DivShapeDrawable(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-drawable", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivDrawable: Equatable {
  public static func ==(lhs: DivDrawable, rhs: DivDrawable) -> Bool {
    switch (lhs, rhs) {
    case let (.divShapeDrawable(l), .divShapeDrawable(r)):
      return l == r
    }
  }
}
#endif

extension DivDrawable: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
