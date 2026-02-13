// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivPagerLayoutMode: Sendable {
  case divPageSize(DivPageSize)
  case divNeighbourPageSize(DivNeighbourPageSize)
  case divPageContentSize(DivPageContentSize)

  public var value: Serializable {
    switch self {
    case let .divPageSize(value):
      return value
    case let .divNeighbourPageSize(value):
      return value
    case let .divPageContentSize(value):
      return value
    }
  }
}

extension DivPagerLayoutMode {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivPageSize.type:
      self = .divPageSize(try DivPageSize(dictionary: dictionary, context: context))
    case DivNeighbourPageSize.type:
      self = .divNeighbourPageSize(try DivNeighbourPageSize(dictionary: dictionary, context: context))
    case DivPageContentSize.type:
      self = .divPageContentSize(try DivPageContentSize(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-pager-layout-mode", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivPagerLayoutMode: Equatable {
  public static func ==(lhs: DivPagerLayoutMode, rhs: DivPagerLayoutMode) -> Bool {
    switch (lhs, rhs) {
    case let (.divPageSize(l), .divPageSize(r)):
      return l == r
    case let (.divNeighbourPageSize(l), .divNeighbourPageSize(r)):
      return l == r
    case let (.divPageContentSize(l), .divPageContentSize(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivPagerLayoutMode: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
