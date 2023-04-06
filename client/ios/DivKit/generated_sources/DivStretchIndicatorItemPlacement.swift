// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivStretchIndicatorItemPlacement {
  public static let type: String = "stretch"
  public let itemSpacing: DivFixedSize // default value: DivFixedSize(value: .value(5))
  public let maxVisibleItems: Expression<Int> // constraint: number > 0; default value: 10

  public func resolveMaxVisibleItems(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumericValue(expression: maxVisibleItems) ?? 10
  }

  static let itemSpacingValidator: AnyValueValidator<DivFixedSize> =
    makeNoOpValueValidator()

  static let maxVisibleItemsValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 > 0 })

  init(
    itemSpacing: DivFixedSize? = nil,
    maxVisibleItems: Expression<Int>? = nil
  ) {
    self.itemSpacing = itemSpacing ?? DivFixedSize(value: .value(5))
    self.maxVisibleItems = maxVisibleItems ?? .value(10)
  }
}

#if DEBUG
extension DivStretchIndicatorItemPlacement: Equatable {
  public static func ==(lhs: DivStretchIndicatorItemPlacement, rhs: DivStretchIndicatorItemPlacement) -> Bool {
    guard
      lhs.itemSpacing == rhs.itemSpacing,
      lhs.maxVisibleItems == rhs.maxVisibleItems
    else {
      return false
    }
    return true
  }
}
#endif

extension DivStretchIndicatorItemPlacement: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["item_spacing"] = itemSpacing.toDictionary()
    result["max_visible_items"] = maxVisibleItems.toValidSerializationValue()
    return result
  }
}
