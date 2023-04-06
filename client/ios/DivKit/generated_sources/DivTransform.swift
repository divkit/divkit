// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTransform {
  public let pivotX: DivPivot // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let pivotY: DivPivot // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let rotation: Expression<Double>?

  public func resolveRotation(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumericValue(expression: rotation)
  }

  static let pivotXValidator: AnyValueValidator<DivPivot> =
    makeNoOpValueValidator()

  static let pivotYValidator: AnyValueValidator<DivPivot> =
    makeNoOpValueValidator()

  init(
    pivotX: DivPivot? = nil,
    pivotY: DivPivot? = nil,
    rotation: Expression<Double>? = nil
  ) {
    self.pivotX = pivotX ?? .divPivotPercentage(DivPivotPercentage(value: .value(50)))
    self.pivotY = pivotY ?? .divPivotPercentage(DivPivotPercentage(value: .value(50)))
    self.rotation = rotation
  }
}

#if DEBUG
extension DivTransform: Equatable {
  public static func ==(lhs: DivTransform, rhs: DivTransform) -> Bool {
    guard
      lhs.pivotX == rhs.pivotX,
      lhs.pivotY == rhs.pivotY,
      lhs.rotation == rhs.rotation
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTransform: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["pivot_x"] = pivotX.toDictionary()
    result["pivot_y"] = pivotY.toDictionary()
    result["rotation"] = rotation?.toValidSerializationValue()
    return result
  }
}
