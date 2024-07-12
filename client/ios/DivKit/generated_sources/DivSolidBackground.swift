// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSolidBackground {
  public static let type: String = "solid"
  public let color: Expression<Color>

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  init(
    color: Expression<Color>
  ) {
    self.color = color
  }
}

#if DEBUG
extension DivSolidBackground: Equatable {
  public static func ==(lhs: DivSolidBackground, rhs: DivSolidBackground) -> Bool {
    guard
      lhs.color == rhs.color
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSolidBackground: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    return result
  }
}
