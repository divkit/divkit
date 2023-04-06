// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivBlur {
  public static let type: String = "blur"
  public let radius: Expression<Int> // constraint: number >= 0

  public func resolveRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: radius)
  }

  static let radiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["radius"] = radius.toValidSerializationValue()
    return result
  }
}
