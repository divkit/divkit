// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivAbsoluteEdgeInsets {
  public let bottom: Expression<Int> // constraint: number >= 0; default value: 0
  public let left: Expression<Int> // constraint: number >= 0; default value: 0
  public let right: Expression<Int> // constraint: number >= 0; default value: 0
  public let top: Expression<Int> // constraint: number >= 0; default value: 0

  public func resolveBottom(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: bottom) ?? 0
  }

  public func resolveLeft(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: left) ?? 0
  }

  public func resolveRight(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: right) ?? 0
  }

  public func resolveTop(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: top) ?? 0
  }

  static let bottomValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let leftValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let topValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    bottom: Expression<Int>? = nil,
    left: Expression<Int>? = nil,
    right: Expression<Int>? = nil,
    top: Expression<Int>? = nil
  ) {
    self.bottom = bottom ?? .value(0)
    self.left = left ?? .value(0)
    self.right = right ?? .value(0)
    self.top = top ?? .value(0)
  }
}

#if DEBUG
extension DivAbsoluteEdgeInsets: Equatable {
  public static func ==(lhs: DivAbsoluteEdgeInsets, rhs: DivAbsoluteEdgeInsets) -> Bool {
    guard
      lhs.bottom == rhs.bottom,
      lhs.left == rhs.left,
      lhs.right == rhs.right
    else {
      return false
    }
    guard
      lhs.top == rhs.top
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAbsoluteEdgeInsets: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["bottom"] = bottom.toValidSerializationValue()
    result["left"] = left.toValidSerializationValue()
    result["right"] = right.toValidSerializationValue()
    result["top"] = top.toValidSerializationValue()
    return result
  }
}
