// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivMatchParentSize {
  public static let type: String = "match_parent"
  public let weight: Expression<Double>? // constraint: number > 0

  public func resolveWeight(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: weight)
  }

  static let weightValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    weight: Expression<Double>? = nil
  ) {
    self.weight = weight
  }
}

#if DEBUG
extension DivMatchParentSize: Equatable {
  public static func ==(lhs: DivMatchParentSize, rhs: DivMatchParentSize) -> Bool {
    guard
      lhs.weight == rhs.weight
    else {
      return false
    }
    return true
  }
}
#endif

extension DivMatchParentSize: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["weight"] = weight?.toValidSerializationValue()
    return result
  }
}
