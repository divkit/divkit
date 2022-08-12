// Copyright 2020 Yandex LLC. All rights reserved.

import UIKit

import CommonCore
import LayoutKitInterface

extension CommonHandler {
  public func perform(sendingFrom sender: UIResponder) {
    switch self {
    case let .action(action):
      action.perform(sendingFrom: sender)
    case let .block(block):
      block()
    }
  }
}
