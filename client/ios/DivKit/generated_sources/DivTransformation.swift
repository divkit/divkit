// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTransformation: Sendable {
  case divRotationTransformation(DivRotationTransformation)
  case divTranslationTransformation(DivTranslationTransformation)

  public var value: Serializable {
    switch self {
    case let .divRotationTransformation(value):
      return value
    case let .divTranslationTransformation(value):
      return value
    }
  }
}

#if DEBUG
extension DivTransformation: Equatable {
  public static func ==(lhs: DivTransformation, rhs: DivTransformation) -> Bool {
    switch (lhs, rhs) {
    case let (.divRotationTransformation(l), .divRotationTransformation(r)):
      return l == r
    case let (.divTranslationTransformation(l), .divTranslationTransformation(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivTransformation: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
