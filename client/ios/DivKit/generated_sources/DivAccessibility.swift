// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAccessibility {
  @frozen
  public enum Kind: String, CaseIterable {
    case none = "none"
    case button = "button"
    case image = "image"
    case text = "text"
    case editText = "edit_text"
    case header = "header"
    case tabBar = "tab_bar"
    case list = "list"
    case select = "select"
    case auto = "auto"
  }

  @frozen
  public enum Mode: String, CaseIterable {
    case `default` = "default"
    case merge = "merge"
    case exclude = "exclude"
  }

  public let description: Expression<String>?
  public let hint: Expression<String>?
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
    mode: Expression<Mode>? = nil,
    muteAfterAction: Expression<Bool>? = nil,
    stateDescription: Expression<String>? = nil,
    type: Kind? = nil
  ) {
    self.description = description
    self.hint = hint
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
      lhs.mode == rhs.mode
    else {
      return false
    }
    guard
      lhs.muteAfterAction == rhs.muteAfterAction,
      lhs.stateDescription == rhs.stateDescription,
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
    result["mode"] = mode.toValidSerializationValue()
    result["mute_after_action"] = muteAfterAction.toValidSerializationValue()
    result["state_description"] = stateDescription?.toValidSerializationValue()
    result["type"] = type.rawValue
    return result
  }
}
