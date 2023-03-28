import Foundation

import BasePublic
import CommonCorePublic
import LayoutKitInterface

public struct ContextMenu: Equatable {
  public struct Item: Equatable {
    public let image: Image
    public let text: String
    public let action: UserInterfaceAction
    public let isDestructive: Bool

    public init(
      image: Image,
      text: String,
      action: UserInterfaceAction,
      isDestructive: Bool = false
    ) {
      self.image = image
      self.text = text
      self.action = action
      self.isDestructive = isDestructive
    }

    public static func ==(lhs: Item, rhs: Item) -> Bool {
      lhs.action == rhs.action && lhs.text == rhs.text && lhs.image === rhs.image &&
        lhs.isDestructive == rhs.isDestructive
    }
  }

  public let title: String?
  public let cancelTitle: String
  public let items: NonEmptyArray<ContextMenu.Item>
  public let preview: Block

  public init(
    title: String? = nil,
    cancelTitle: String,
    items: NonEmptyArray<ContextMenu.Item>,
    preview: Block
  ) {
    self.title = title
    self.items = items
    self.preview = preview
    self.cancelTitle = cancelTitle
  }

  public static func ==(lhs: ContextMenu, rhs: ContextMenu) -> Bool {
    lhs.preview == rhs.preview && lhs.items == rhs.items
  }
}

extension ContextMenu: CustomDebugStringConvertible {
  public var debugDescription: String {
    var description = "items\n"
    for item in items.asArray() {
      description += "    UIAction:\n"
      description += "       path: \(item.action.path)\n"
      description += "       payload: \(item.action.payload.debugDescription.indented())\n"
    }
    description += "Block:\n"
    description += "preview: " + preview.debugDescription
    return description
  }
}
