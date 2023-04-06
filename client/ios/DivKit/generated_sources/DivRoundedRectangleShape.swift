// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivRoundedRectangleShape {
  public static let type: String = "rounded_rectangle"
  public let backgroundColor: Expression<Color>?
  public let cornerRadius: DivFixedSize // default value: DivFixedSize(value: .value(5))
  public let itemHeight: DivFixedSize // default value: DivFixedSize(value: .value(10))
  public let itemWidth: DivFixedSize // default value: DivFixedSize(value: .value(10))
  public let stroke: DivStroke?

  public func resolveBackgroundColor(_ resolver: ExpressionResolver) -> Color? {
    resolver.resolveStringBasedValue(expression: backgroundColor, initializer: Color.color(withHexString:))
  }

  static let backgroundColorValidator: AnyValueValidator<Color> =
    makeNoOpValueValidator()

  static let cornerRadiusValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let itemHeightValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let itemWidthValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let strokeValidator: AnyValueValidator<DivStroke> =
    makeNoOpValueValidator()

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
