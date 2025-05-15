// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class ColorVariable: Sendable {
  public static let type: String = "color"
  public let name: String
  public let value: Expression<Color>

  public func resolveValue(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(value)
  }

  init(
    name: String,
    value: Expression<Color>
  ) {
    self.name = name
    self.value = value
  }
}

#if DEBUG
extension ColorVariable: Equatable {
  public static func ==(lhs: ColorVariable, rhs: ColorVariable) -> Bool {
    guard
      lhs.name == rhs.name,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension ColorVariable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["name"] = name
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
