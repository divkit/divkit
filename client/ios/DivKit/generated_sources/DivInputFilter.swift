// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivInputFilter: Sendable {
  case divInputFilterRegex(DivInputFilterRegex)
  case divInputFilterExpression(DivInputFilterExpression)

  public var value: Serializable {
    switch self {
    case let .divInputFilterRegex(value):
      return value
    case let .divInputFilterExpression(value):
      return value
    }
  }
}

extension DivInputFilter {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivInputFilterRegex.type:
      self = .divInputFilterRegex(try DivInputFilterRegex(dictionary: dictionary, context: context))
    case DivInputFilterExpression.type:
      self = .divInputFilterExpression(try DivInputFilterExpression(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivInputFilter: Equatable {
  public static func ==(lhs: DivInputFilter, rhs: DivInputFilter) -> Bool {
    switch (lhs, rhs) {
    case let (.divInputFilterRegex(l), .divInputFilterRegex(r)):
      return l == r
    case let (.divInputFilterExpression(l), .divInputFilterExpression(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivInputFilter: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
