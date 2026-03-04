// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivFilter: Sendable {
  case divBlur(DivBlur)
  case divFilterRtlMirror(DivFilterRtlMirror)

  public var value: Serializable {
    switch self {
    case let .divBlur(value):
      return value
    case let .divFilterRtlMirror(value):
      return value
    }
  }
}

extension DivFilter {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivBlur.type:
      self = .divBlur(try DivBlur(dictionary: dictionary, context: context))
    case DivFilterRtlMirror.type:
      self = .divFilterRtlMirror(try DivFilterRtlMirror(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivFilter: Equatable {
  public static func ==(lhs: DivFilter, rhs: DivFilter) -> Bool {
    switch (lhs, rhs) {
    case let (.divBlur(l), .divBlur(r)):
      return l == r
    case let (.divFilterRtlMirror(l), .divFilterRtlMirror(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivFilter: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
