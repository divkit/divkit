// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFocus {
  public final class NextFocusIds {
    public let down: Expression<String>? // at least 1 char
    public let forward: Expression<String>? // at least 1 char
    public let left: Expression<String>? // at least 1 char
    public let right: Expression<String>? // at least 1 char
    public let up: Expression<String>? // at least 1 char

    public func resolveDown(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: down, initializer: { $0 })
    }

    public func resolveForward(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: forward, initializer: { $0 })
    }

    public func resolveLeft(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: left, initializer: { $0 })
    }

    public func resolveRight(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: right, initializer: { $0 })
    }

    public func resolveUp(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: up, initializer: { $0 })
    }

    static let downValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let forwardValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let leftValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let rightValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let upValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    init(
      down: Expression<String>? = nil,
      forward: Expression<String>? = nil,
      left: Expression<String>? = nil,
      right: Expression<String>? = nil,
      up: Expression<String>? = nil
    ) {
      self.down = down
      self.forward = forward
      self.left = left
      self.right = right
      self.up = up
    }
  }

  public let background: [DivBackground]? // at least 1 elements
  public let border: DivBorder
  public let nextFocusIds: NextFocusIds?
  public let onBlur: [DivAction]? // at least 1 elements
  public let onFocus: [DivAction]? // at least 1 elements

  static let backgroundValidator: AnyArrayValueValidator<DivBackground> =
    makeArrayValidator(minItems: 1)

  static let borderValidator: AnyValueValidator<DivBorder> =
    makeNoOpValueValidator()

  static let nextFocusIdsValidator: AnyValueValidator<DivFocus.NextFocusIds> =
    makeNoOpValueValidator()

  static let onBlurValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  static let onFocusValidator: AnyArrayValueValidator<DivAction> =
    makeArrayValidator(minItems: 1)

  init(
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    nextFocusIds: NextFocusIds? = nil,
    onBlur: [DivAction]? = nil,
    onFocus: [DivAction]? = nil
  ) {
    self.background = background
    self.border = border ?? DivBorder()
    self.nextFocusIds = nextFocusIds
    self.onBlur = onBlur
    self.onFocus = onFocus
  }
}

#if DEBUG
extension DivFocus: Equatable {
  public static func ==(lhs: DivFocus, rhs: DivFocus) -> Bool {
    guard
      lhs.background == rhs.background,
      lhs.border == rhs.border,
      lhs.nextFocusIds == rhs.nextFocusIds
    else {
      return false
    }
    guard
      lhs.onBlur == rhs.onBlur,
      lhs.onFocus == rhs.onFocus
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFocus: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["background"] = background?.map { $0.toDictionary() }
    result["border"] = border.toDictionary()
    result["next_focus_ids"] = nextFocusIds?.toDictionary()
    result["on_blur"] = onBlur?.map { $0.toDictionary() }
    result["on_focus"] = onFocus?.map { $0.toDictionary() }
    return result
  }
}

#if DEBUG
extension DivFocus.NextFocusIds: Equatable {
  public static func ==(lhs: DivFocus.NextFocusIds, rhs: DivFocus.NextFocusIds) -> Bool {
    guard
      lhs.down == rhs.down,
      lhs.forward == rhs.forward,
      lhs.left == rhs.left
    else {
      return false
    }
    guard
      lhs.right == rhs.right,
      lhs.up == rhs.up
    else {
      return false
    }
    return true
  }
}
#endif

extension DivFocus.NextFocusIds: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["down"] = down?.toValidSerializationValue()
    result["forward"] = forward?.toValidSerializationValue()
    result["left"] = left?.toValidSerializationValue()
    result["right"] = right?.toValidSerializationValue()
    result["up"] = up?.toValidSerializationValue()
    return result
  }
}
