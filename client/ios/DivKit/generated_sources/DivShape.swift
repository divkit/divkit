// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

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
    value.toDictionary()
  }
}
