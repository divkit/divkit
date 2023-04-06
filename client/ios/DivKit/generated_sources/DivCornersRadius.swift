// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCornersRadius {
  public let bottomLeft: Expression<Int>? // constraint: number >= 0
  public let bottomRight: Expression<Int>? // constraint: number >= 0
  public let topLeft: Expression<Int>? // constraint: number >= 0
  public let topRight: Expression<Int>? // constraint: number >= 0

  public func resolveBottomLeft(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: bottomLeft)
  }

  public func resolveBottomRight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: bottomRight)
  }

  public func resolveTopLeft(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: topLeft)
  }

  public func resolveTopRight(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumericValue(expression: topRight)
  }

  static let bottomLeftValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let bottomRightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let topLeftValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let topRightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    bottomLeft: Expression<Int>? = nil,
    bottomRight: Expression<Int>? = nil,
    topLeft: Expression<Int>? = nil,
    topRight: Expression<Int>? = nil
  ) {
    self.bottomLeft = bottomLeft
    self.bottomRight = bottomRight
    self.topLeft = topLeft
    self.topRight = topRight
  }
}

#if DEBUG
extension DivCornersRadius: Equatable {
  public static func ==(lhs: DivCornersRadius, rhs: DivCornersRadius) -> Bool {
    guard
      lhs.bottomLeft == rhs.bottomLeft,
      lhs.bottomRight == rhs.bottomRight,
      lhs.topLeft == rhs.topLeft
    else {
      return false
    }
    guard
      lhs.topRight == rhs.topRight
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCornersRadius: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["bottom-left"] = bottomLeft?.toValidSerializationValue()
    result["bottom-right"] = bottomRight?.toValidSerializationValue()
    result["top-left"] = topLeft?.toValidSerializationValue()
    result["top-right"] = topRight?.toValidSerializationValue()
    return result
  }
}
