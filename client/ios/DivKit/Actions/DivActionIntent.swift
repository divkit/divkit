import Foundation
import VGSL

enum DivActionIntent {
  case showTooltip(id: String, multiple: Bool)
  case hideTooltip(id: String)
  case download(patchUrl: URL)
  case setState(divStatePath: DivStatePath, lifetime: DivStateLifetime)
  case setVariable(name: String, value: String)
  case setCurrentItem(id: String, index: Int)
  case setNextItem(id: String, step: Int, overflow: OverflowMode)
  case setPreviousItem(id: String, step: Int, overflow: OverflowMode)
  case scroll(id: String, mode: ScrollMode)
  case timer(id: String, action: DivTimerAction)
  case video(id: String, action: DivVideoAction)
  case setStoredValue(DivStoredValue)

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
      self = .setNextItem(id: id, step: url.step ?? 1, overflow: url.overflow)
    case "set_previous_item":
      guard let id = url.id else {
        return nil
      }
      self = .setPreviousItem(id: id, step: url.step ?? 1, overflow: url.overflow)
    case "scroll_forward":
      guard let id = url.id, let step = url.step else {
        return nil
      }
      self = .scroll(id: id, mode: .forward(step, overflow: url.overflow))
    case "scroll_backward":
      guard let id = url.id, let step = url.step else {
        return nil
      }
      self = .scroll(id: id, mode: .backward(step, overflow: url.overflow))
    case "scroll_to_position":
      guard let id = url.id, let step = url.step else {
        return nil
      }
      self = .scroll(id: id, mode: .position(step))
    case "scroll_to_start":
      guard let id = url.id else {
        return nil
      }
      self = .scroll(id: id, mode: .start)
    case "scroll_to_end":
      guard let id = url.id else {
        return nil
      }
      self = .scroll(id: id, mode: .end)
    case "timer":
      guard let id = url.id, let action = url.timerAction else {
        return nil
      }
      self = .timer(id: id, action: action)
    case "video":
      guard let id = url.id, let action = url.videoAction else {
        return nil
      }
      self = .video(id: id, action: action)
    case "set_stored_value":
      guard let storedValue = url.storedValue else {
        return nil
      }
      self = .setStoredValue(storedValue)
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
      DivKitLogger.error("Unknown action '\(action)' for timer.")
      return nil
    }
  }

  fileprivate var videoAction: DivVideoAction? {
    guard let action = queryParamValue(forName: "action") else {
      return nil
    }
    switch action {
    case "start":
      return .play
    case "pause":
      return .pause
    default:
      DivKitLogger.error("Unknown action '\(action)' for video.")
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
      DivKitLogger.error("Unknown overflow: '\(overflow)'.")
      return .clamp
    }
  }

  fileprivate var step: Int? {
    queryParamValue(forName: "step").flatMap(Int.init)
  }

  fileprivate var storedValue: DivStoredValue? {
    guard let name = getParam(forName: "name"),
          let value = getParam(forName: "value"),
          let lifetime = getParam(forName: "lifetime").flatMap(Int.init),
          let typeStr = getParam(forName: "type") else {
      return nil
    }
    guard let type = DivStoredValue.ValueType(rawValue: typeStr) else {
      DivKitLogger.error("Unsupported stored value type: \(typeStr)")
      return nil
    }
    let storedValue = DivStoredValue(
      name: name,
      value: value,
      type: type,
      lifetimeInSec: lifetime
    )
    guard storedValue.isValueValid else {
      DivKitLogger.error("Incorrect value: \(value) for type: \(type)")
      return nil
    }
    return storedValue
  }

  fileprivate func getParam(forName name: String) -> String? {
    guard let param = queryParamValue(forName: name) else {
      DivKitLogger.error("The required parameter \(name) is missing")
      return nil
    }
    return param
  }
}
