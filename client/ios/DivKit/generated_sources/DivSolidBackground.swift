// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivSolidBackground: Sendable {
  public static let type: String = "solid"
  public let color: Expression<Color>

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      color: try dictionary.getExpressionField("color", transform: Color.color(withHexString:), context: context)
    )
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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    return result
  }
}
