// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivStrokeStyle: Sendable {
  case divStrokeStyleSolid(DivStrokeStyleSolid)
  case divStrokeStyleDashed(DivStrokeStyleDashed)

  public var value: Serializable {
    switch self {
    case let .divStrokeStyleSolid(value):
      return value
    case let .divStrokeStyleDashed(value):
      return value
    }
  }
}

extension DivStrokeStyle {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivStrokeStyleSolid.type:
      self = .divStrokeStyleSolid(try DivStrokeStyleSolid(dictionary: dictionary, context: context))
    case DivStrokeStyleDashed.type:
      self = .divStrokeStyleDashed(try DivStrokeStyleDashed(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivStrokeStyle: Equatable {
  public static func ==(lhs: DivStrokeStyle, rhs: DivStrokeStyle) -> Bool {
    switch (lhs, rhs) {
    case let (.divStrokeStyleSolid(l), .divStrokeStyleSolid(r)):
      return l == r
    case let (.divStrokeStyleDashed(l), .divStrokeStyleDashed(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivStrokeStyle: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
