// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public enum DivAppearanceTransition {
  case divAppearanceSetTransition(DivAppearanceSetTransition)
  case divFadeTransition(DivFadeTransition)
  case divScaleTransition(DivScaleTransition)
  case divSlideTransition(DivSlideTransition)

  public var value: Serializable {
    switch self {
    case let .divAppearanceSetTransition(value):
      return value
    case let .divFadeTransition(value):
      return value
    case let .divScaleTransition(value):
      return value
    case let .divSlideTransition(value):
      return value
    }
  }
}

#if DEBUG
extension DivAppearanceTransition: Equatable {
  public static func ==(lhs: DivAppearanceTransition, rhs: DivAppearanceTransition) -> Bool {
    switch (lhs, rhs) {
    case let (.divAppearanceSetTransition(l), .divAppearanceSetTransition(r)):
      return l == r
    case let (.divFadeTransition(l), .divFadeTransition(r)):
      return l == r
    case let (.divScaleTransition(l), .divScaleTransition(r)):
      return l == r
    case let (.divSlideTransition(l), .divSlideTransition(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivAppearanceTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    value.toDictionary()
  }
}
