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
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
