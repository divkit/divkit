// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivShapeDrawable: Sendable {
  public static let type: String = "shape_drawable"
  public let color: Expression<Color>
  public let shape: DivShape
  public let stroke: DivStroke?

  public func resolveColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(color)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      color: try dictionary.getExpressionField("color", transform: Color.color(withHexString:), context: context),
      shape: try dictionary.getField("shape", transform: { (dict: [String: Any]) in try DivShape(dictionary: dict, context: context) }, context: context),
      stroke: try dictionary.getOptionalField("stroke", transform: { (dict: [String: Any]) in try DivStroke(dictionary: dict, context: context) })
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["color"] = color.toValidSerializationValue()
    result["shape"] = shape.toDictionary()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
