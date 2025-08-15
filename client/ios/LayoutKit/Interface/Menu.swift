import Foundation

public struct Menu: Equatable, Codable {
  public struct Item: Equatable, Codable {
    public let actions: [UserInterfaceAction]
    public let text: String

    public init(
      actions: [UserInterfaceAction],
      text: String
    ) {
      self.actions = actions
      self.text = text
    }

    public init(action: UserInterfaceAction, text: String) {
      self.init(actions: [action], text: text)
    }
  }

  public let items: [Item]

  public init?(items: [Item]) {
    guard !items.isEmpty else {
      return nil
    }
    self.items = items
  }
}

extension Menu.Item: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Text: \(text), actions: \(actions)"
  }
}

extension Menu: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Menu: \(items)"
  }
}
