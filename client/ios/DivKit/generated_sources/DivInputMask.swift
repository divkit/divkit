// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

@frozen
public enum DivInputMask {
  case divFixedLengthInputMask(DivFixedLengthInputMask)

  public var value: Serializable {
    switch self {
    case let .divFixedLengthInputMask(value):
      return value
    }
  }
}

#if DEBUG
extension DivInputMask: Equatable {
  public static func ==(lhs: DivInputMask, rhs: DivInputMask) -> Bool {
    switch (lhs, rhs) {
    case let (.divFixedLengthInputMask(l), .divFixedLengthInputMask(r)):
      return l == r
    }
  }
}
#endif

extension DivInputMask: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
