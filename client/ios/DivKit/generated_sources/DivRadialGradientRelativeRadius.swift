// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRadialGradientRelativeRadius: Sendable {
  @frozen
  public enum Value: String, CaseIterable, Sendable {
    case nearestCorner = "nearest_corner"
    case farthestCorner = "farthest_corner"
    case nearestSide = "nearest_side"
    case farthestSide = "farthest_side"
  }

  public static let type: String = "relative"
  public let value: Expression<Value>

  public func resolveValue(_ resolver: ExpressionResolver) -> Value? {
    resolver.resolveEnum(value)
  }

  init(
    value: Expression<Value>
  ) {
    self.value = value
  }
}

#if DEBUG
extension DivRadialGradientRelativeRadius: Equatable {
  public static func ==(lhs: DivRadialGradientRelativeRadius, rhs: DivRadialGradientRelativeRadius) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivRadialGradientRelativeRadius: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
