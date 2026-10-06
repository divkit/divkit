// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivStaticTextGradient: Sendable {
  case divLinearGradient(DivLinearGradient)
  case divRadialGradient(DivRadialGradient)

  public var value: Serializable {
    switch self {
    case let .divLinearGradient(value):
      return value
    case let .divRadialGradient(value):
      return value
    }
  }
}

extension DivStaticTextGradient {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivLinearGradient.type:
      self = .divLinearGradient(try DivLinearGradient(dictionary: dictionary, context: context))
    case DivRadialGradient.type:
      self = .divRadialGradient(try DivRadialGradient(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivStaticTextGradient: Equatable {
  public static func ==(lhs: DivStaticTextGradient, rhs: DivStaticTextGradient) -> Bool {
    switch (lhs, rhs) {
    case let (.divLinearGradient(l), .divLinearGradient(r)):
      return l == r
    case let (.divRadialGradient(l), .divRadialGradient(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivStaticTextGradient: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
