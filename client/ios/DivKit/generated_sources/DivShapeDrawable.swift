// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivShapeDrawable {
  public static let type: String = "shape_drawable"
  public let color: Expression<Color>
  public let shape: DivShape
  public let stroke: DivStroke?

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: color, initializer: Color.color(withHexString:))
  }

  static let strokeValidator: AnyValueValidator<DivStroke> =
    makeNoOpValueValidator()

  init(
    color: Expression<Color>,
    shape: DivShape,
    stroke: DivStroke? = nil
  ) {
    self.color = color
    self.shape = shape
    self.stroke = stroke
  }
}

#if DEBUG
extension DivShapeDrawable: Equatable {
  public static func ==(lhs: DivShapeDrawable, rhs: DivShapeDrawable) -> Bool {
    guard
      lhs.color == rhs.color,
      lhs.shape == rhs.shape,
      lhs.stroke == rhs.stroke
    else {
      return false
    }
    return true
  }
}
#endif

extension DivShapeDrawable: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    result["shape"] = shape.toDictionary()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
