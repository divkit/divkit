// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivPivot: Sendable {
  case divPivotFixed(DivPivotFixed)
  case divPivotPercentage(DivPivotPercentage)

  public var value: Serializable {
    switch self {
    case let .divPivotFixed(value):
      return value
    case let .divPivotPercentage(value):
      return value
    }
  }
}

extension DivPivot {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivPivotFixed.type:
      self = .divPivotFixed(try DivPivotFixed(dictionary: dictionary, context: context))
    case DivPivotPercentage.type:
      self = .divPivotPercentage(try DivPivotPercentage(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-pivot", representation: dictionary)
    }
  }
}

#if DEBUG
extension DivPivot: Equatable {
  public static func ==(lhs: DivPivot, rhs: DivPivot) -> Bool {
    switch (lhs, rhs) {
    case let (.divPivotFixed(l), .divPivotFixed(r)):
      return l == r
    case let (.divPivotPercentage(l), .divPivotPercentage(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivPivot: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
