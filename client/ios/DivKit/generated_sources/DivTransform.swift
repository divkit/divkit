// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTransform: Sendable {
  public let pivotX: DivPivot // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let pivotY: DivPivot // default value: .divPivotPercentage(DivPivotPercentage(value: .value(50)))
  public let rotation: Expression<Double>?

  public func resolveRotation(_ resolver: ExpressionResolver) -> Double? {
    resolver.resolveNumeric(rotation)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      pivotX: try dictionary.getOptionalField("pivot_x", transform: { (dict: [String: Any]) in try DivPivot(dictionary: dict, context: context) }),
      pivotY: try dictionary.getOptionalField("pivot_y", transform: { (dict: [String: Any]) in try DivPivot(dictionary: dict, context: context) }),
      rotation: try dictionary.getOptionalExpressionField("rotation", context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["pivot_x"] = pivotX.toDictionary()
    result["pivot_y"] = pivotY.toDictionary()
    result["rotation"] = rotation?.toValidSerializationValue()
    return result
  }
}
