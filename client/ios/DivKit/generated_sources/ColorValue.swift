// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class ColorValue: Sendable {
  public static let type: String = "color"
  public let value: Expression<Color>

  public func resolveValue(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(value)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      value: try dictionary.getExpressionField("value", transform: Color.color(withHexString:), context: context)
    )
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["value"] = value.toValidSerializationValue()
    return result
  }
}
