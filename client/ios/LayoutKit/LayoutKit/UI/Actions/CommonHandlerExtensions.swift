import UIKit

import CommonCorePublic
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
