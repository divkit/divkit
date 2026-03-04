// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivPageTransformation: Sendable {
  case divPageTransformationSlide(DivPageTransformationSlide)
  case divPageTransformationOverlap(DivPageTransformationOverlap)

  public var value: Serializable {
    switch self {
    case let .divPageTransformationSlide(value):
      return value
    case let .divPageTransformationOverlap(value):
      return value
    }
  }
}

extension DivPageTransformation {
  public init(dictionary: [String: Any], context: ParsingContext) throws {
    let dictionary = context.templateResolver?(dictionary) ?? dictionary
    let blockType = try dictionary.getField("type") as String
    switch blockType {
    case DivPageTransformationSlide.type:
      self = .divPageTransformationSlide(try DivPageTransformationSlide(dictionary: dictionary, context: context))
    case DivPageTransformationOverlap.type:
      self = .divPageTransformationOverlap(try DivPageTransformationOverlap(dictionary: dictionary, context: context))
    default:
      throw DeserializationError.requiredFieldIsMissing(field: "type")
    }
  }
}

#if DEBUG
extension DivPageTransformation: Equatable {
  public static func ==(lhs: DivPageTransformation, rhs: DivPageTransformation) -> Bool {
    switch (lhs, rhs) {
    case let (.divPageTransformationSlide(l), .divPageTransformationSlide(r)):
      return l == r
    case let (.divPageTransformationOverlap(l), .divPageTransformationOverlap(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivPageTransformation: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
