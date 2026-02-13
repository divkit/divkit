// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivRoundedRectangleShape: Sendable {
  public static let type: String = "rounded_rectangle"
  public let backgroundColor: Expression<Color>?
  public let cornerRadius: DivFixedSize // default value: DivFixedSize(value: .value(5))
  public let itemHeight: DivFixedSize // default value: DivFixedSize(value: .value(10))
  public let itemWidth: DivFixedSize // default value: DivFixedSize(value: .value(10))
  public let stroke: DivStroke?

  public func resolveBackgroundColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveColor(backgroundColor)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      backgroundColor: try dictionary.getOptionalExpressionField("background_color", transform: Color.color(withHexString:), context: context),
      cornerRadius: try dictionary.getOptionalField("corner_radius", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) }),
      itemHeight: try dictionary.getOptionalField("item_height", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) }),
      itemWidth: try dictionary.getOptionalField("item_width", transform: { (dict: [String: Any]) in try DivFixedSize(dictionary: dict, context: context) }),
      stroke: try dictionary.getOptionalField("stroke", transform: { (dict: [String: Any]) in try DivStroke(dictionary: dict, context: context) })
    )
  }

  init(
    backgroundColor: Expression<Color>? = nil,
    cornerRadius: DivFixedSize? = nil,
    itemHeight: DivFixedSize? = nil,
    itemWidth: DivFixedSize? = nil,
    stroke: DivStroke? = nil
  ) {
    self.backgroundColor = backgroundColor
    self.cornerRadius = cornerRadius ?? DivFixedSize(value: .value(5))
    self.itemHeight = itemHeight ?? DivFixedSize(value: .value(10))
    self.itemWidth = itemWidth ?? DivFixedSize(value: .value(10))
    self.stroke = stroke
  }
}

#if DEBUG
extension DivRoundedRectangleShape: Equatable {
  public static func ==(lhs: DivRoundedRectangleShape, rhs: DivRoundedRectangleShape) -> Bool {
    guard
      lhs.backgroundColor == rhs.backgroundColor,
      lhs.cornerRadius == rhs.cornerRadius,
      lhs.itemHeight == rhs.itemHeight
    else {
      return false
    }
    guard
      lhs.itemWidth == rhs.itemWidth,
      lhs.stroke == rhs.stroke
    else {
      return false
    }
    return true
  }
}
#endif

extension DivRoundedRectangleShape: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["background_color"] = backgroundColor?.toValidSerializationValue()
    result["corner_radius"] = cornerRadius.toDictionary()
    result["item_height"] = itemHeight.toDictionary()
    result["item_width"] = itemWidth.toDictionary()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
