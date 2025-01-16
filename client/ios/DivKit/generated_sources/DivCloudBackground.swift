// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCloudBackground: Sendable {
  public static let type: String = "cloud"
  public let color: Expression<Color>
  public let cornerRadius: Expression<Int> // constraint: number >= 0
  public let paddings: DivEdgeInsets?

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public func resolveCornerRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(cornerRadius)
  }

  static let cornerRadiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    color: Expression<Color>,
    cornerRadius: Expression<Int>,
    paddings: DivEdgeInsets? = nil
  ) {
    self.color = color
    self.cornerRadius = cornerRadius
    self.paddings = paddings
  }
}

#if DEBUG
extension DivCloudBackground: Equatable {
  public static func ==(lhs: DivCloudBackground, rhs: DivCloudBackground) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.cornerRadius == rhs.cornerRadius,
      lhs.paddings == rhs.paddings
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCloudBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    result["corner_radius"] = cornerRadius.toValidSerializationValue()
    result["paddings"] = paddings?.toDictionary()
    return result
  }
}
