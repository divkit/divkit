// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivMatchParentSize: Sendable {
  public static let type: String = "match_parent"
  public let maxSize: DivSizeUnitValue?
  public let minSize: DivSizeUnitValue?
  public let weight: Expression<Double>? // constraint: number > 0

  public func resolveWeight(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(weight)
  }

  static let weightValidator: AnyValueValidator<Double> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    maxSize: DivSizeUnitValue? = nil,
    minSize: DivSizeUnitValue? = nil,
    weight: Expression<Double>? = nil
  ) {
    self.maxSize = maxSize
    self.minSize = minSize
    self.weight = weight
  }
}

#if DEBUG
extension DivMatchParentSize: Equatable {
  public static func ==(lhs: DivMatchParentSize, rhs: DivMatchParentSize) -> Bool {
    guard
      lhs.maxSize == rhs.maxSize,
      lhs.minSize == rhs.minSize,
      lhs.weight == rhs.weight
    else {
      return false
    }
    return true
  }
}
#endif

extension DivMatchParentSize: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["max_size"] = maxSize?.toDictionary()
    result["min_size"] = minSize?.toDictionary()
    result["weight"] = weight?.toValidSerializationValue()
    return result
  }
}
