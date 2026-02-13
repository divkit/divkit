// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivActionScrollDestination: Sendable {
  case offsetDestination(OffsetDestination)
  case indexDestination(IndexDestination)
  case startDestination(StartDestination)
  case endDestination(EndDestination)

  public var value: Serializable {
    switch self {
    case let .offsetDestination(value):
      return value
    case let .indexDestination(value):
      return value
    case let .startDestination(value):
      return value
    case let .endDestination(value):
      return value
    }
  }
}

extension DivActionScrollDestination {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case OffsetDestination.type:
      self = .offsetDestination(try OffsetDestination(dictionary: dictionary, context: context))
    case IndexDestination.type:
      self = .indexDestination(try IndexDestination(dictionary: dictionary, context: context))
    case StartDestination.type:
      self = .startDestination(try StartDestination(dictionary: dictionary, context: context))
    case EndDestination.type:
      self = .endDestination(try EndDestination(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-action-scroll-destination", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivActionScrollDestination: Equatable {
  public static func ==(lhs: DivActionScrollDestination, rhs: DivActionScrollDestination) -> Bool {
    switch (lhs, rhs) {
    case let (.offsetDestination(l), .offsetDestination(r)):
      return l == r
    case let (.indexDestination(l), .indexDestination(r)):
      return l == r
    case let (.startDestination(l), .startDestination(r)):
      return l == r
    case let (.endDestination(l), .endDestination(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivActionScrollDestination: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
