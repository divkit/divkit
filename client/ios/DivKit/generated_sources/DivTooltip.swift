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
  public let bringToTopId: String?
  public let closeByTapOutside: Expression<Bool> // default value: true
  public let div: Div
  public let duration: Expression<Int> // constraint: number >= 0; default value: 5000
  public let id: String
  public let mode: DivTooltipMode // default value: .divTooltipModeModal(DivTooltipModeModal())
  public let offset: DivPoint?
  public let position: Expression<Position>
  public let substrateDiv: Div?
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

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      animationIn: try dictionary.getOptionalField("animation_in", transform: { (dict: [String: Any]) in try DivAnimation(dictionary: dict, context: context) }),
      animationOut: try dictionary.getOptionalField("animation_out", transform: { (dict: [String: Any]) in try DivAnimation(dictionary: dict, context: context) }),
      backgroundAccessibilityDescription: try dictionary.getOptionalExpressionField("background_accessibility_description", context: context),
      bringToTopId: try dictionary.getOptionalField("bring_to_top_id", context: context),
      closeByTapOutside: try dictionary.getOptionalExpressionField("close_by_tap_outside", context: context),
      div: try dictionary.getField("div", transform: { (dict: [String: Any]) in try Div(dictionary: dict, context: context) }, context: context),
      duration: try dictionary.getOptionalExpressionField("duration", validator: Self.durationValidator, context: context),
      id: try dictionary.getField("id", context: context),
      mode: try dictionary.getOptionalField("mode", transform: { (dict: [String: Any]) in try DivTooltipMode(dictionary: dict, context: context) }),
      offset: try dictionary.getOptionalField("offset", transform: { (dict: [String: Any]) in try DivPoint(dictionary: dict, context: context) }),
      position: try dictionary.getExpressionField("position", context: context),
      substrateDiv: try dictionary.getOptionalField("substrate_div", transform: { (dict: [String: Any]) in try Div(dictionary: dict, context: context) }),
      tapOutsideActions: try dictionary.getOptionalArray("tap_outside_actions", transform: { (dict: [String: Any]) in try? DivAction(dictionary: dict, context: context) })
    )
  }

  init(
    animationIn: DivAnimation? = nil,
    animationOut: DivAnimation? = nil,
    backgroundAccessibilityDescription: Expression<String>? = nil,
    bringToTopId: String? = nil,
    closeByTapOutside: Expression<Bool>? = nil,
    div: Div,
    duration: Expression<Int>? = nil,
    id: String,
    mode: DivTooltipMode? = nil,
    offset: DivPoint? = nil,
    position: Expression<Position>,
    substrateDiv: Div? = nil,
    tapOutsideActions: [DivAction]? = nil
  ) {
    self.animationIn = animationIn
    self.animationOut = animationOut
    self.backgroundAccessibilityDescription = backgroundAccessibilityDescription
    self.bringToTopId = bringToTopId
    self.closeByTapOutside = closeByTapOutside ?? .value(true)
    self.div = div
    self.duration = duration ?? .value(5000)
    self.id = id
    self.mode = mode ?? .divTooltipModeModal(DivTooltipModeModal())
    self.offset = offset
    self.position = position
    self.substrateDiv = substrateDiv
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
      lhs.bringToTopId == rhs.bringToTopId,
      lhs.closeByTapOutside == rhs.closeByTapOutside,
      lhs.div == rhs.div
    else {
      return false
    }
    guard
      lhs.duration == rhs.duration,
      lhs.id == rhs.id,
      lhs.mode == rhs.mode
    else {
      return false
    }
    guard
      lhs.offset == rhs.offset,
      lhs.position == rhs.position,
      lhs.substrateDiv == rhs.substrateDiv
    else {
      return false
    }
    guard
      lhs.tapOutsideActions == rhs.tapOutsideActions
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTooltip: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["animation_in"] = animationIn?.toDictionary()
    result["animation_out"] = animationOut?.toDictionary()
    result["background_accessibility_description"] = backgroundAccessibilityDescription?.toValidSerializationValue()
    result["bring_to_top_id"] = bringToTopId
    result["close_by_tap_outside"] = closeByTapOutside.toValidSerializationValue()
    result["div"] = div.toDictionary()
    result["duration"] = duration.toValidSerializationValue()
    result["id"] = id
    result["mode"] = mode.toDictionary()
    result["offset"] = offset?.toDictionary()
    result["position"] = position.toValidSerializationValue()
    result["substrate_div"] = substrateDiv?.toDictionary()
    result["tap_outside_actions"] = tapOutsideActions?.map { $0.toDictionary() }
    return result
  }
}
