// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivDrawable {
  case divShapeDrawable(DivShapeDrawable)

  public var value: Serializable {
    switch self {
    case let .divShapeDrawable(value):
      return value
    }
  }
}

#if DEBUG
extension DivDrawable: Equatable {
  public static func ==(lhs: DivDrawable, rhs: DivDrawable) -> Bool {
    switch (lhs, rhs) {
    case let (.divShapeDrawable(l), .divShapeDrawable(r)):
      return l == r
    }
  }
}
#endif

extension DivDrawable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
