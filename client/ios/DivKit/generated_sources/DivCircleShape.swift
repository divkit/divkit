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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["background_color"] = backgroundColor?.toValidSerializationValue()
    result["radius"] = radius.toDictionary()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
