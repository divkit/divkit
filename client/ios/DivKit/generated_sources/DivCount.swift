// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivCount: Sendable {
  case divInfinityCount(DivInfinityCount)
  case divFixedCount(DivFixedCount)

  public var value: Serializable {
    switch self {
    case let .divInfinityCount(value):
      return value
    case let .divFixedCount(value):
      return value
    }
  }
}

extension DivCount {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivInfinityCount.type:
      self = .divInfinityCount(try DivInfinityCount(dictionary: dictionary, context: context))
    case DivFixedCount.type:
      self = .divFixedCount(try DivFixedCount(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-count", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivCount: Equatable {
  public static func ==(lhs: DivCount, rhs: DivCount) -> Bool {
    switch (lhs, rhs) {
    case let (.divInfinityCount(l), .divInfinityCount(r)):
      return l == r
    case let (.divFixedCount(l), .divFixedCount(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivCount: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
