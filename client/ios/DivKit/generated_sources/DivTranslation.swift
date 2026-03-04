// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTranslation: Sendable {
  case divFixedTranslation(DivFixedTranslation)
  case divPercentageTranslation(DivPercentageTranslation)

  public var value: Serializable {
    switch self {
    case let .divFixedTranslation(value):
      return value
    case let .divPercentageTranslation(value):
      return value
    }
  }
}

extension DivTranslation {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivFixedTranslation.type:
      self = .divFixedTranslation(try DivFixedTranslation(dictionary: dictionary, context: context))
    case DivPercentageTranslation.type:
      self = .divPercentageTranslation(try DivPercentageTranslation(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivTranslation: Equatable {
  public static func ==(lhs: DivTranslation, rhs: DivTranslation) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedTranslation(l), .divFixedTranslation(r)):
      return l == r
    case let (.divPercentageTranslation(l), .divPercentageTranslation(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTranslation: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
