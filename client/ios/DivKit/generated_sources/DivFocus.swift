// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivFocus: Sendable {
  public final class NextFocusIds: Sendable {
    public let down: Expression<String>?
    public let forward: Expression<String>?
    public let left: Expression<String>?
    public let right: Expression<String>?
    public let up: Expression<String>?

    public func resolveDown(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(down)
    }

    public func resolveForward(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(forward)
    }

    public func resolveLeft(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(left)
    }

    public func resolveRight(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(right)
    }

    public func resolveUp(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveString(up)
    }

    public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
      self.init(
        down: try dictionary.getOptionalExpressionField("down", context: context),
        forward: try dictionary.getOptionalExpressionField("forward", context: context),
        left: try dictionary.getOptionalExpressionField("left", context: context),
        right: try dictionary.getOptionalExpressionField("right", context: context),
        up: try dictionary.getOptionalExpressionField("up", context: context)
      )
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

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      background: try dictionary.getOptionalArray("background", transform: { (dict: [String: Any]) in try? DivBackground(dictionary: dict, context: context) }),
      border: try dictionary.getOptionalField("border", transform: { (dict: [String: Any]) in try DivBorder(dictionary: dict, context: context) }),
      nextFocusIds: try dictionary.getOptionalField("next_focus_ids", transform: { (dict: [String: Any]) in try DivFocus.NextFocusIds(dictionary: dict, context: context) }),
      onBlur: try dictionary.getOptionalArray("on_blur", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) }),
      onFocus: try dictionary.getOptionalArray("on_focus", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) })
    )
  }

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
  @_optimize(size)
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
  @_optimize(size)
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
