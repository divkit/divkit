// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

@frozen
public enum DivInputMethod {
  case divKeyboardInput(DivKeyboardInput)
  case divSelectionInput(DivSelectionInput)

  public var value: Serializable {
    switch self {
    case let .divKeyboardInput(value):
      return value
    case let .divSelectionInput(value):
      return value
    }
  }
}

#if DEBUG
extension DivInputMethod: Equatable {
  public static func ==(lhs: DivInputMethod, rhs: DivInputMethod) -> Bool {
    switch (lhs, rhs) {
    case let (.divKeyboardInput(l), .divKeyboardInput(r)):
      return l == r
    case let (.divSelectionInput(l), .divSelectionInput(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivInputMethod: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
