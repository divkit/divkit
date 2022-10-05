// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

@frozen
public enum DivShape {
  case divRoundedRectangleShape(DivRoundedRectangleShape)

  public var value: Serializable {
    switch self {
    case let .divRoundedRectangleShape(value):
      return value
    }
  }
}

#if DEBUG
extension DivShape: Equatable {
  public static func ==(lhs: DivShape, rhs: DivShape) -> Bool {
    switch (lhs, rhs) {
    case let (.divRoundedRectangleShape(l), .divRoundedRectangleShape(r)):
      return l == r
    }
  }
}
#endif

extension DivShape: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
