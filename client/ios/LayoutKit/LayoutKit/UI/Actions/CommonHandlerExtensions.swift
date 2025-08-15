#if os(iOS)
import UIKit
import VGSL

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
#endif
