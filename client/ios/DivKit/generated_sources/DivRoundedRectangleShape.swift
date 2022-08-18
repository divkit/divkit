// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivRoundedRectangleShape {
  public static let type: String = "rounded_rectangle"
  public let cornerRadius: DivFixedSize // default value: DivFixedSize(value: .value(5))
  public let itemHeight: DivFixedSize // default value: DivFixedSize(value: .value(10))
  public let itemWidth: DivFixedSize // default value: DivFixedSize(value: .value(10))

  static let cornerRadiusValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let itemHeightValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let itemWidthValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  init(
    cornerRadius: DivFixedSize? = nil,
    itemHeight: DivFixedSize? = nil,
    itemWidth: DivFixedSize? = nil
  ) {
    self.cornerRadius = cornerRadius ?? DivFixedSize(value: .value(5))
    self.itemHeight = itemHeight ?? DivFixedSize(value: .value(10))
    self.itemWidth = itemWidth ?? DivFixedSize(value: .value(10))
  }
}

#if DEBUG
extension DivRoundedRectangleShape: Equatable {
  public static func ==(lhs: DivRoundedRectangleShape, rhs: DivRoundedRectangleShape) -> Bool {
    guard
      lhs.cornerRadius == rhs.cornerRadius,
      lhs.itemHeight == rhs.itemHeight,
      lhs.itemWidth == rhs.itemWidth
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
    result["corner_radius"] = cornerRadius.toDictionary()
    result["item_height"] = itemHeight.toDictionary()
    result["item_width"] = itemWidth.toDictionary()
    return result
  }
}
