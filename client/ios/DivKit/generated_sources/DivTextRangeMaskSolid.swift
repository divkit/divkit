// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeMaskSolid {
  public static let type: String = "solid"
  public let color: Expression<Color>
  public let isEnabled: Expression<Bool> // default value: true

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  init(
    color: Expression<Color>,
    isEnabled: Expression<Bool>? = nil
  ) {
    self.color = color
    self.isEnabled = isEnabled ?? .value(true)
  }
}

#if DEBUG
extension DivTextRangeMaskSolid: Equatable {
  public static func ==(lhs: DivTextRangeMaskSolid, rhs: DivTextRangeMaskSolid) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.isEnabled == rhs.isEnabled
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTextRangeMaskSolid: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    return result
  }
}
