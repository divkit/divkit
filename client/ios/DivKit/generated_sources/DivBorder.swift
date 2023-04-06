// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivBorder {
  public let cornerRadius: Expression<Int>? // constraint: number >= 0
  public let cornersRadius: DivCornersRadius?
  public let hasShadow: Expression<Bool> // default value: false
  public let shadow: DivShadow?
  public let stroke: DivStroke?

  public func resolveCornerRadius(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: cornerRadius)
  }

  public func resolveHasShadow(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumericValue(expression: hasShadow) ?? false
  }

  static let cornerRadiusValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let cornersRadiusValidator: AnyValueValidator<DivCornersRadius> =
    makeNoOpValueValidator()

  static let hasShadowValidator: AnyValueValidator<Bool> =
    makeNoOpValueValidator()

  static let shadowValidator: AnyValueValidator<DivShadow> =
    makeNoOpValueValidator()

  static let strokeValidator: AnyValueValidator<DivStroke> =
    makeNoOpValueValidator()

  init(
    cornerRadius: Expression<Int>? = nil,
    cornersRadius: DivCornersRadius? = nil,
    hasShadow: Expression<Bool>? = nil,
    shadow: DivShadow? = nil,
    stroke: DivStroke? = nil
  ) {
    self.cornerRadius = cornerRadius
    self.cornersRadius = cornersRadius
    self.hasShadow = hasShadow ?? .value(false)
    self.shadow = shadow
    self.stroke = stroke
  }
}

#if DEBUG
extension DivBorder: Equatable {
  public static func ==(lhs: DivBorder, rhs: DivBorder) -> Bool {
    guard
      lhs.cornerRadius == rhs.cornerRadius,
      lhs.cornersRadius == rhs.cornersRadius,
      lhs.hasShadow == rhs.hasShadow
    else {
      return false
    }
    guard
      lhs.shadow == rhs.shadow,
      lhs.stroke == rhs.stroke
    else {
      return false
    }
    return true
  }
}
#endif

extension DivBorder: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["corner_radius"] = cornerRadius?.toValidSerializationValue()
    result["corners_radius"] = cornersRadius?.toDictionary()
    result["has_shadow"] = hasShadow.toValidSerializationValue()
    result["shadow"] = shadow?.toDictionary()
    result["stroke"] = stroke?.toDictionary()
    return result
  }
}
