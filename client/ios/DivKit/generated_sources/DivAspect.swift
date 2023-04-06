// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAspect {
  public let ratio: Expression<Double> // constraint: number > 0

  public func resolveRatio(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: ratio)
  }

  static let ratioValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    ratio: Expression<Double>
  ) {
    self.ratio = ratio
  }
}

#if DEBUG
extension DivAspect: Equatable {
  public static func ==(lhs: DivAspect, rhs: DivAspect) -> Bool {
    guard
      lhs.ratio == rhs.ratio
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAspect: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["ratio"] = ratio.toValidSerializationValue()
    return result
  }
}
