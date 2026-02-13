// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTransformation: Sendable {
  case divRotationTransformation(DivRotationTransformation)
  case divTranslationTransformation(DivTranslationTransformation)

  public var value: Serializable {
    switch self {
    case let .divRotationTransformation(value):
      return value
    case let .divTranslationTransformation(value):
      return value
    }
  }
}

extension DivTransformation {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivRotationTransformation.type:
      self = .divRotationTransformation(try DivRotationTransformation(dictionary: dictionary, context: context))
    case DivTranslationTransformation.type:
      self = .divTranslationTransformation(try DivTranslationTransformation(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-transformation", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivTransformation: Equatable {
  public static func ==(lhs: DivTransformation, rhs: DivTransformation) -> Bool {
    switch (lhs, rhs) {
    case let (.divRotationTransformation(l), .divRotationTransformation(r)):
      return l == r
    case let (.divTranslationTransformation(l), .divTranslationTransformation(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTransformation: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
