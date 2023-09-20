// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class ColorValue {
  public static let type: String = "color"
  public let value: Expression<Color>

  public func resolveValue(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: value, initializer: Color.color(withHexString:))
  }

  init(
    value: Expression<Color>
  ) {
    self.value = value
  }
}

#if DEBUG
extension ColorValue: Equatable {
  public static func ==(lhs: ColorValue, rhs: ColorValue) -> Bool {
    guard
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension ColorValue: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
