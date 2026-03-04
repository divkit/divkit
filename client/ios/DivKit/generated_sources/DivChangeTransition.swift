// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivChangeTransition: Sendable {
  case divChangeSetTransition(DivChangeSetTransition)
  case divChangeBoundsTransition(DivChangeBoundsTransition)

  public var value: Serializable {
    switch self {
    case let .divChangeSetTransition(value):
      return value
    case let .divChangeBoundsTransition(value):
      return value
    }
  }
}

extension DivChangeTransition {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivChangeSetTransition.type:
      self = .divChangeSetTransition(try DivChangeSetTransition(dictionary: dictionary, context: context))
    case DivChangeBoundsTransition.type:
      self = .divChangeBoundsTransition(try DivChangeBoundsTransition(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivChangeTransition: Equatable {
  public static func ==(lhs: DivChangeTransition, rhs: DivChangeTransition) -> Bool {
    switch (lhs, rhs) {
    case let (.divChangeSetTransition(l), .divChangeSetTransition(r)):
      return l == r
    case let (.divChangeBoundsTransition(l), .divChangeBoundsTransition(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivChangeTransition: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
