// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivBlur: Sendable {
  public static let type: String = "blur"
  public let radius: Expression<Int> // constraint: number >= 0

  public func resolveRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(radius)
  }

  static let radiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      radius: try dictionary.getExpressionField("radius", validator: Self.radiusValidator, context: context)
    )
  }

  init(
    radius: Expression<Int>
  ) {
    self.radius = radius
  }
}

#if DEBUG
extension DivBlur: Equatable {
  public static func ==(lhs: DivBlur, rhs: DivBlur) -> Bool {
    guard
      lhs.radius == rhs.radius
    else {
      return false
    }
    return true
  }
}
#endif

extension DivBlur: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["radius"] = radius.toValidSerializationValue()
    return result
  }
}
