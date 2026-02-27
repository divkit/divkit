// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivIndicatorItemPlacement: Sendable {
  case divDefaultIndicatorItemPlacement(DivDefaultIndicatorItemPlacement)
  case divStretchIndicatorItemPlacement(DivStretchIndicatorItemPlacement)

  public var value: Serializable {
    switch self {
    case let .divDefaultIndicatorItemPlacement(value):
      return value
    case let .divStretchIndicatorItemPlacement(value):
      return value
    }
  }
}

extension DivIndicatorItemPlacement {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivDefaultIndicatorItemPlacement.type:
      self = .divDefaultIndicatorItemPlacement(try DivDefaultIndicatorItemPlacement(dictionary: dictionary, context: context))
    case DivStretchIndicatorItemPlacement.type:
      self = .divStretchIndicatorItemPlacement(try DivStretchIndicatorItemPlacement(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivIndicatorItemPlacement: Equatable {
  public static func ==(lhs: DivIndicatorItemPlacement, rhs: DivIndicatorItemPlacement) -> Bool {
    switch (lhs, rhs) {
    case let (.divDefaultIndicatorItemPlacement(l), .divDefaultIndicatorItemPlacement(r)):
      return l == r
    case let (.divStretchIndicatorItemPlacement(l), .divStretchIndicatorItemPlacement(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivIndicatorItemPlacement: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
