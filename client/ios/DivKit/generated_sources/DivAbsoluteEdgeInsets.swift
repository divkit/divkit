// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAbsoluteEdgeInsets: Sendable {
  public let bottom: Expression<Int> // constraint: number >= 0; default value: 0
  public let left: Expression<Int> // constraint: number >= 0; default value: 0
  public let right: Expression<Int> // constraint: number >= 0; default value: 0
  public let top: Expression<Int> // constraint: number >= 0; default value: 0

  public func resolveBottom(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(bottom) ?? 0
  }

  public func resolveLeft(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(left) ?? 0
  }

  public func resolveRight(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(right) ?? 0
  }

  public func resolveTop(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(top) ?? 0
  }

  static let bottomValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let leftValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let topValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      bottom: try dictionary.getOptionalExpressionField("bottom", validator: Self.bottomValidator, context: context),
      left: try dictionary.getOptionalExpressionField("left", validator: Self.leftValidator, context: context),
      right: try dictionary.getOptionalExpressionField("right", validator: Self.rightValidator, context: context),
      top: try dictionary.getOptionalExpressionField("top", validator: Self.topValidator, context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["bottom"] = bottom.toValidSerializationValue()
    result["left"] = left.toValidSerializationValue()
    result["right"] = right.toValidSerializationValue()
    result["top"] = top.toValidSerializationValue()
    return result
  }
}
