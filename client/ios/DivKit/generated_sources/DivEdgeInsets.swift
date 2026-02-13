// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivEdgeInsets: Sendable {
  public let bottom: Expression<Int> // constraint: number >= 0; default value: 0
  public let end: Expression<Int>? // constraint: number >= 0
  public let left: Expression<Int> // constraint: number >= 0; default value: 0
  public let right: Expression<Int> // constraint: number >= 0; default value: 0
  public let start: Expression<Int>? // constraint: number >= 0
  public let top: Expression<Int> // constraint: number >= 0; default value: 0
  public let unit: Expression<DivSizeUnit> // default value: dp

  public func resolveBottom(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(bottom) ?? 0
  }

  public func resolveEnd(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(end)
  }

  public func resolveLeft(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(left) ?? 0
  }

  public func resolveRight(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(right) ?? 0
  }

  public func resolveStart(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(start)
  }

  public func resolveTop(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(top) ?? 0
  }

  public func resolveUnit(_ resolver: ExpressionResolver) -> DivSizeUnit {
    resolver.resolveEnum(unit) ?? DivSizeUnit.dp
  }

  static let bottomValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let endValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let leftValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let rightValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let startValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  static let topValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      bottom: try dictionary.getOptionalExpressionField("bottom", validator: Self.bottomValidator, context: context),
      end: try dictionary.getOptionalExpressionField("end", validator: Self.endValidator, context: context),
      left: try dictionary.getOptionalExpressionField("left", validator: Self.leftValidator, context: context),
      right: try dictionary.getOptionalExpressionField("right", validator: Self.rightValidator, context: context),
      start: try dictionary.getOptionalExpressionField("start", validator: Self.startValidator, context: context),
      top: try dictionary.getOptionalExpressionField("top", validator: Self.topValidator, context: context),
      unit: try dictionary.getOptionalExpressionField("unit", context: context)
    )
  }

  init(
    bottom: Expression<Int>? = nil,
    end: Expression<Int>? = nil,
    left: Expression<Int>? = nil,
    right: Expression<Int>? = nil,
    start: Expression<Int>? = nil,
    top: Expression<Int>? = nil,
    unit: Expression<DivSizeUnit>? = nil
  ) {
    self.bottom = bottom ?? .value(0)
    self.end = end
    self.left = left ?? .value(0)
    self.right = right ?? .value(0)
    self.start = start
    self.top = top ?? .value(0)
    self.unit = unit ?? .value(.dp)
  }
}

#if DEBUG
extension DivEdgeInsets: Equatable {
  public static func ==(lhs: DivEdgeInsets, rhs: DivEdgeInsets) -> Bool {
    guard
      lhs.bottom == rhs.bottom,
      lhs.end == rhs.end,
      lhs.left == rhs.left
    else {
      return false
    }
    guard
      lhs.right == rhs.right,
      lhs.start == rhs.start,
      lhs.top == rhs.top
    else {
      return false
    }
    guard
      lhs.unit == rhs.unit
    else {
      return false
    }
    return true
  }
}
#endif

extension DivEdgeInsets: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["bottom"] = bottom.toValidSerializationValue()
    result["end"] = end?.toValidSerializationValue()
    result["left"] = left.toValidSerializationValue()
    result["right"] = right.toValidSerializationValue()
    result["start"] = start?.toValidSerializationValue()
    result["top"] = top.toValidSerializationValue()
    result["unit"] = unit.toValidSerializationValue()
    return result
  }
}
