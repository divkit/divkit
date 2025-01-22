// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAccessibility: Sendable {
  @frozen
  public enum Kind: String, CaseIterable, Sendable {
    case none = "none"
    case button = "button"
    case image = "image"
    case text = "text"
    case editText = "edit_text"
    case header = "header"
    case tabBar = "tab_bar"
    case list = "list"
    case select = "select"
    case checkbox = "checkbox"
    case radio = "radio"
    case auto = "auto"
  }

  @frozen
  public enum Mode: String, CaseIterable, Sendable {
    case `default` = "default"
    case merge = "merge"
    case exclude = "exclude"
  }

  public let description: Expression<String>?
  public let hint: Expression<String>?
  public let isChecked: Expression<Bool>?
  public let mode: Expression<Mode> // default value: default
  public let muteAfterAction: Expression<Bool> // default value: false
  public let stateDescription: Expression<String>?
  public let type: Kind // default value: auto

  public func resolveDescription(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(description)
  }

  public func resolveHint(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(hint)
  }

  public func resolveIsChecked(_ resolver: ExpressionResolver) -> Bool? {
    resolver.resolveNumeric(isChecked)
  }

  public func resolveMode(_ resolver: ExpressionResolver) -> Mode {
    resolver.resolveEnum(mode) ?? Mode.default
  }

  public func resolveMuteAfterAction(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(muteAfterAction) ?? false
  }

  public func resolveStateDescription(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(stateDescription)
  }

  init(
    description: Expression<String>? = nil,
    hint: Expression<String>? = nil,
    isChecked: Expression<Bool>? = nil,
    mode: Expression<Mode>? = nil,
    muteAfterAction: Expression<Bool>? = nil,
    stateDescription: Expression<String>? = nil,
    type: Kind? = nil
  ) {
    self.description = description
    self.hint = hint
    self.isChecked = isChecked
    self.mode = mode ?? .value(.default)
    self.muteAfterAction = muteAfterAction ?? .value(false)
    self.stateDescription = stateDescription
    self.type = type ?? .auto
  }
}

#if DEBUG
extension DivAccessibility: Equatable {
  public static func ==(lhs: DivAccessibility, rhs: DivAccessibility) -> Bool {
    guard
      lhs.description == rhs.description,
      lhs.hint == rhs.hint,
      lhs.isChecked == rhs.isChecked
    else {
      return false
    }
    guard
      lhs.mode == rhs.mode,
      lhs.muteAfterAction == rhs.muteAfterAction,
      lhs.stateDescription == rhs.stateDescription
    else {
      return false
    }
    guard
      lhs.type == rhs.type
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAccessibility: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["description"] = description?.toValidSerializationValue()
    result["hint"] = hint?.toValidSerializationValue()
    result["is_checked"] = isChecked?.toValidSerializationValue()
    result["mode"] = mode.toValidSerializationValue()
    result["mute_after_action"] = muteAfterAction.toValidSerializationValue()
    result["state_description"] = stateDescription?.toValidSerializationValue()
    result["type"] = type.rawValue
    return result
  }
}
