import Foundation

import CommonCorePublic
import LayoutKitInterface

enum DivActionIntent {
  case showTooltip(id: String, multiple: Bool)
  case hideTooltip(id: String)
  case download(patchUrl: URL)
  case setState(divStatePath: DivStatePath, lifetime: DivStateLifetime)
  case setVariable(name: String, value: String)
  case setCurrentItem(id: String, index: Int)
  case setNextItem(id: String, overflow: OverflowMode)
  case setPreviousItem(id: String, overflow: OverflowMode)
  case timer(id: String, action: DivTimerAction)

  public static let scheme = "div-action"

  public init?(url: URL) {
    guard url.scheme == Self.scheme else {
      return nil
    }

    switch url.host {
    case "show_tooltip":
      guard let id = url.id else {
        return nil
      }
      self = .showTooltip(id: id, multiple: url.multiple)
    case "hide_tooltip":
      guard let id = url.id else {
        return nil
      }
      self = .hideTooltip(id: id)
    case "download":
      guard let patchUrl = url.patchUrl else {
        return nil
      }
      self = .download(patchUrl: patchUrl)
    case "set_state":
      guard let divStatePath = url.divStatePath else {
        return nil
      }
      self = .setState(divStatePath: divStatePath, lifetime: url.lifetime)
    case "set_variable":
      guard let variable = url.variableInfo else {
        return nil
      }
      self = .setVariable(name: variable.name, value: variable.value)
    case "set_current_item":
      guard let id = url.id, let index = url.item else {
        return nil
      }
      self = .setCurrentItem(id: id, index: index)
    case "set_next_item":
      guard let id = url.id else {
        return nil
      }
      self = .setNextItem(id: id, overflow: url.overflow)
    case "set_previous_item":
      guard let id = url.id else {
        return nil
      }
      self = .setPreviousItem(id: id, overflow: url.overflow)
    case "timer":
      guard let id = url.id, let action = url.timerAction else {
        return nil
      }
      self = .timer(id: id, action: action)
    default:
      return nil
    }
  }
}

extension URL {
  fileprivate var id: String? {
    queryParamValue(forName: "id")
  }

  fileprivate var item: Int? {
    queryParamValue(forName: "item").flatMap(Int.init)
  }

  fileprivate var multiple: Bool {
    queryParamValue(forName: "multiple").flatMap(Bool.init) ?? false
  }

  fileprivate var patchUrl: URL? {
    queryParamValue(forName: "url").flatMap(URL.init(string:))
  }

  fileprivate var divStatePath: DivStatePath? {
    queryParamValue(forName: "state_id")
      .flatMap(DivStatePath.makeDivStatePath(from:))
  }

  fileprivate var lifetime: DivStateLifetime {
    let isTemporary = queryParamValue(forName: "temporary").flatMap(Bool.init)
      ?? true
    return isTemporary ? .short : .long
  }

  fileprivate var variableInfo: (name: String, value: String)? {
    guard let name = queryParamValue(forName: "name"),
          let value = queryParamValue(forName: "value") else { return nil }
    return (name: name, value: value)
  }

  fileprivate var timerAction: DivTimerAction? {
    guard let action = queryParamValue(forName: "action") else {
      return nil
    }
    switch action {
    case "start":
      return .start
    case "stop":
      return .stop
    case "pause":
      return .pause
    case "resume":
      return .resume
    case "cancel":
      return .cancel
    case "reset":
      return .reset
    default:
      DivKitLogger.failure("Unknown action '\(action)' for timer.")
      return nil
    }
  }

  fileprivate var overflow: OverflowMode {
    guard let overflow = queryParamValue(forName: "overflow") else {
      return .clamp
    }
    switch overflow {
    case "clamp":
      return .clamp
    case "ring":
      return .ring
    default:
      DivKitLogger.failure("Unknown overflow: '\(overflow)'.")
      return .clamp
    }
  }
}
