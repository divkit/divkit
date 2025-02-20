// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltip: Sendable {
  @frozen
  public enum Position: String, CaseIterable, Sendable {
    case left = "left"
    case topLeft = "top-left"
    case top = "top"
    case topRight = "top-right"
    case right = "right"
    case bottomRight = "bottom-right"
    case bottom = "bottom"
    case bottomLeft = "bottom-left"
    case center = "center"
  }

  public let animationIn: DivAnimation?
  public let animationOut: DivAnimation?
  public let backgroundAccessibilityDescription: Expression<String>?
  public let closeByTapOutside: Expression<Bool> // default value: true
  public let div: Div
  public let duration: Expression<Int> // constraint: number >= 0; default value: 5000
  public let id: String
  public let mode: DivTooltipMode // default value: .divTooltipModeModal(DivTooltipModeModal())
  public let offset: DivPoint?
  public let position: Expression<Position>
  public let tapOutsideActions: [DivAction]?

  public func resolveBackgroundAccessibilityDescription(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(backgroundAccessibilityDescription)
  }

  public func resolveCloseByTapOutside(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(closeByTapOutside) ?? true
  }

  public func resolveDuration(_ resolver: ExpressionResolver) -> Int {
    resolver.resolveNumeric(duration) ?? 5000
  }

  public func resolvePosition(_ resolver: ExpressionResolver) -> Position? {
    resolver.resolveEnum(position)
  }

  static let durationValidator: AnyValueValidator<Int> =
    makeValueValidator(valueValidator: { $0 >= 0 })

  init(
    animationIn: DivAnimation? = nil,
    animationOut: DivAnimation? = nil,
    backgroundAccessibilityDescription: Expression<String>? = nil,
    closeByTapOutside: Expression<Bool>? = nil,
    div: Div,
    duration: Expression<Int>? = nil,
    id: String,
    mode: DivTooltipMode? = nil,
    offset: DivPoint? = nil,
    position: Expression<Position>,
    tapOutsideActions: [DivAction]? = nil
  ) {
    self.animationIn = animationIn
    self.animationOut = animationOut
    self.backgroundAccessibilityDescription = backgroundAccessibilityDescription
    self.closeByTapOutside = closeByTapOutside ?? .value(true)
    self.div = div
    self.duration = duration ?? .value(5000)
    self.id = id
    self.mode = mode ?? .divTooltipModeModal(DivTooltipModeModal())
    self.offset = offset
    self.position = position
    self.tapOutsideActions = tapOutsideActions
  }
}

#if DEBUG
extension DivTooltip: Equatable {
  public static func ==(lhs: DivTooltip, rhs: DivTooltip) -> Bool {
    guard
      lhs.animationIn == rhs.animationIn,
      lhs.animationOut == rhs.animationOut,
      lhs.backgroundAccessibilityDescription == rhs.backgroundAccessibilityDescription
    else {
      return false
    }
    guard
      lhs.closeByTapOutside == rhs.closeByTapOutside,
      lhs.div == rhs.div,
      lhs.duration == rhs.duration
    else {
      return false
    }
    guard
      lhs.id == rhs.id,
      lhs.mode == rhs.mode,
      lhs.offset == rhs.offset
    else {
      return false
    }
    guard
      lhs.position == rhs.position,
      lhs.tapOutsideActions == rhs.tapOutsideActions
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTooltip: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["animation_in"] = animationIn?.toDictionary()
    result["animation_out"] = animationOut?.toDictionary()
    result["background_accessibility_description"] = backgroundAccessibilityDescription?.toValidSerializationValue()
    result["close_by_tap_outside"] = closeByTapOutside.toValidSerializationValue()
    result["div"] = div.toDictionary()
    result["duration"] = duration.toValidSerializationValue()
    result["id"] = id
    result["mode"] = mode.toDictionary()
    result["offset"] = offset?.toDictionary()
    result["position"] = position.toValidSerializationValue()
    result["tap_outside_actions"] = tapOutsideActions?.map { $0.toDictionary() }
    return result
  }
}
