// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFocus {
  public final class NextFocusIds {
    public let down: Expression<String>?
    public let forward: Expression<String>?
    public let left: Expression<String>?
    public let right: Expression<String>?
    public let up: Expression<String>?

    public func resolveDown(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(down, initializer: { $0 })
    }

    public func resolveForward(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(forward, initializer: { $0 })
    }

    public func resolveLeft(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(left, initializer: { $0 })
    }

    public func resolveRight(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(right, initializer: { $0 })
    }

    public func resolveUp(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(up, initializer: { $0 })
    }

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

  public let background: [DivBackground]?
  public let border: DivBorder?
  public let nextFocusIds: NextFocusIds?
  public let onBlur: [DivAction]?
  public let onFocus: [DivAction]?

  init(
    background: [DivBackground]? = nil,
    border: DivBorder? = nil,
    nextFocusIds: NextFocusIds? = nil,
    onBlur: [DivAction]? = nil,
    onFocus: [DivAction]? = nil
  ) {
    self.background = background
    self.border = border
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
    result["border"] = border?.toDictionary()
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
