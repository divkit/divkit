// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRotationTransformation: Sendable {
  public static let type: String = "rotation"
  public let angle: Expression<Double>
  public let pivotX: DivPivot // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let pivotY: DivPivot // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))

  public func resolveAngle(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(angle)
  }

  init(
    angle: Expression<Double>,
    pivotX: DivPivot? = nil,
    pivotY: DivPivot? = nil
  ) {
    self.angle = angle
    self.pivotX = pivotX ?? .divPivotPercentage(DivPivotPercentage(value: .value(50)))
    self.pivotY = pivotY ?? .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  }
}

#if DEBUG
extension DivRotationTransformation: Equatable {
  public static func ==(lhs: DivRotationTransformation, rhs: DivRotationTransformation) -> Bool {
    guard
      lhs.angle == rhs.angle,
      lhs.pivotX == rhs.pivotX,
      lhs.pivotY == rhs.pivotY
    else {
      return false
    }
    return true
  }
}
#endif

extension DivRotationTransformation: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["angle"] = angle.toValidSerializationValue()
    result["pivot_x"] = pivotX.toDictionary()
    result["pivot_y"] = pivotY.toDictionary()
    return result
  }
}
