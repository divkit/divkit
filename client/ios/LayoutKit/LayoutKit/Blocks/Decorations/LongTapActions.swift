import Foundation

import CommonCorePublic
import LayoutKitInterface

public enum LongTapActions: Equatable {
  case actions(NonEmptyArray<UserInterfaceAction>)
  case contextMenu(ContextMenu)
}

extension LongTapActions: CustomDebugStringConvertible {
  public var debugDescription: String {
    var description = "Long tap actions:\n"
    switch self {
    case let .actions(actions):
      description += "Type - actions\n"
      for action in actions.asArray() {
        description += "   UIAction:\n"
        description += "      path: \(action.path)\n"
        description +=
          "      payload: \(action.payload.debugDescription.indented())\n"
      }

    case let .contextMenu(menu):
      description += "Type - context menu\n"
      description += "    menu: \(menu.debugDescription.indented())\n"
    }

    return description
  }
}
