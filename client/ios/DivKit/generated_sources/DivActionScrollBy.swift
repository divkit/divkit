// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionScrollBy: Sendable {
  @frozen
  public enum Overflow: String, CaseIterable, Sendable {
    case clamp = "clamp"
    case ring = "ring"
  }

  public static let type: String = "scroll_by"
  public let animated: Expression<Bool> // default value: true
  public let duration: Expression<Int>? // constraint: number >= 0
  public let id: Expression<String>
  public let itemCount: Expression<Int> // default value: 0
  public let offset: Expression<Int> // default value: 0
  public let overflow: Expression<Overflow> // default value: clamp

  public func resolveAnimated(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(animated) ?? true
  }

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int? {
    resolver.resolveNumeric(duration)
  }

  public func resolveId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(id)
  }

  public func resolveItemCount(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(itemCount) ?? 0
  }

  public func resolveOffset(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(offset) ?? 0
  }

  public func resolveOverflow(_ resolver: ExpressionResolver) -> Overflow {
    resolver.resolveEnum(overflow) ?? Overflow.clamp
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      animated: try dictionary.getOptionalExpressionField("animated", context: context),
      duration: try dictionary.getOptionalExpressionField("duration", validator: Self.durationValidator, context: context),
      id: try dictionary.getExpressionField("id", context: context),
      itemCount: try dictionary.getOptionalExpressionField("item_count", context: context),
      offset: try dictionary.getOptionalExpressionField("offset", context: context),
      overflow: try dictionary.getOptionalExpressionField("overflow", context: context)
    )
  }

  init(
    animated: Expression<Bool>? = nil,
    duration: Expression<Int>? = nil,
    id: Expression<String>,
    itemCount: Expression<Int>? = nil,
    offset: Expression<Int>? = nil,
    overflow: Expression<Overflow>? = nil
  ) {
    self.animated = animated ?? .value(true)
    self.duration = duration
    self.id = id
    self.itemCount = itemCount ?? .value(0)
    self.offset = offset ?? .value(0)
    self.overflow = overflow ?? .value(.clamp)
  }
}

#if DEBUG
extension DivActionScrollBy: Equatable {
  public static func ==(lhs: DivActionScrollBy, rhs: DivActionScrollBy) -> Bool {
    guard
      lhs.animated == rhs.animated,
      lhs.duration == rhs.duration,
      lhs.id == rhs.id
    else {
      return false
    }
    guard
      lhs.itemCount == rhs.itemCount,
      lhs.offset == rhs.offset,
      lhs.overflow == rhs.overflow
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionScrollBy: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["animated"] = animated.toValidSerializationValue()
    result["duration"] = duration?.toValidSerializationValue()
    result["id"] = id.toValidSerializationValue()
    result["item_count"] = itemCount.toValidSerializationValue()
    result["offset"] = offset.toValidSerializationValue()
    result["overflow"] = overflow.toValidSerializationValue()
    return result
  }
}
