// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCircleShape: Sendable {
  public static let type: String = "circle"
  public let backgroundColor: Expression<Color>?
  public let radius: DivFixedSize // default value: DivFixedSize(value: .value(10))
  public let stroke: DivStroke?

  public func resolveBackgroundColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(backgroundColor)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      backgroundColor: try dictionary.getOptionalExpressionField("background_color", transform: Color.color(withHexString:), context: context),
      radius: try dictionary.getOptionalField("radius", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) }),
      stroke: try dictionary.getOptionalField("stroke", transform: { (dict: [String: Any]) in try DivStroke(dictionary: dict, context: context) })
    )
  }

  init(
    backgroundColor: Expression<Color>? = nil,
    radius: DivFixedSize? = nil,
    stroke: DivStroke? = nil
  ) {
    self.backgroundColor = backgroundColor
    self.radius = radius ?? DivFixedSize(value: .value(10))
    self.stroke = stroke
  }
}

#if DEBUG
extension DivCircleShape: Equatable {
  public static func ==(lhs: DivCircleShape, rhs: DivCircleShape) -> Bool {
    guard
      lhs.backgroundColor == rhs.backgroundColor,
      lhs.radius == rhs.radius,
      lhs.stroke == rhs.stroke
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCircleShape: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["background_color"] = backgroundColor?.toValidSerializationValue()
    result["radius"] = radius.toDictionary()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
