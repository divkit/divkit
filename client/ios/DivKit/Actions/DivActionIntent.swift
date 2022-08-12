// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

import CommonCore
import LayoutKitInterface

public enum DivActionIntent {
  case showTooltip(id: String, multiple: Bool)
  case hideTooltip(id: String)
  case download(patchUrl: URL)
  case setState(divStatePath: DivStatePath, lifetime: DivStateLifetime)
  case setVariable(name: String, value: String)
  case setCurrentItem(id: String, index: Int)
  case setNextItem(id: String)
  case setPreviousItem(id: String)

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
      self = .setNextItem(id: id)
    case "set_previous_item":
      guard let id = url.id else {
        return nil
      }
      self = .setPreviousItem(id: id)
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
      ?? false
    return isTemporary ? .short : .long
  }

  fileprivate var variableInfo: (name: String, value: String)? {
    guard let name = queryParamValue(forName: "name"),
          let value = queryParamValue(forName: "value") else { return nil }
    return (name: name, value: value)
  }
}
