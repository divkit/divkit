// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivAnimator {
  case divColorAnimator(DivColorAnimator)
  case divNumberAnimator(DivNumberAnimator)

  public var value: Serializable & DivAnimatorBase {
    switch self {
    case let .divColorAnimator(value):
      return value
    case let .divNumberAnimator(value):
      return value
    }
  }

  public var id: String {
    switch self {
    case let .divColorAnimator(value):
      return value.id
    case let .divNumberAnimator(value):
      return value.id
    }
  }
}

#if DEBUG
extension DivAnimator: Equatable {
  public static func ==(lhs: DivAnimator, rhs: DivAnimator) -> Bool {
    switch (lhs, rhs) {
    case let (.divColorAnimator(l), .divColorAnimator(r)):
      return l == r
    case let (.divNumberAnimator(l), .divNumberAnimator(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivAnimator: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
