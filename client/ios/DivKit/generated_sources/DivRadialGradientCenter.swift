// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivRadialGradientCenter: Sendable {
  case divRadialGradientFixedCenter(DivRadialGradientFixedCenter)
  case divRadialGradientRelativeCenter(DivRadialGradientRelativeCenter)

  public var value: Serializable {
    switch self {
    case let .divRadialGradientFixedCenter(value):
      return value
    case let .divRadialGradientRelativeCenter(value):
      return value
    }
  }
}

extension DivRadialGradientCenter {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivRadialGradientFixedCenter.type:
      self = .divRadialGradientFixedCenter(try DivRadialGradientFixedCenter(dictionary: dictionary, context: context))
    case DivRadialGradientRelativeCenter.type:
      self = .divRadialGradientRelativeCenter(try DivRadialGradientRelativeCenter(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivRadialGradientCenter: Equatable {
  public static func ==(lhs: DivRadialGradientCenter, rhs: DivRadialGradientCenter) -> Bool {
    switch (lhs, rhs) {
    case let (.divRadialGradientFixedCenter(l), .divRadialGradientFixedCenter(r)):
      return l == r
    case let (.divRadialGradientRelativeCenter(l), .divRadialGradientRelativeCenter(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivRadialGradientCenter: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
