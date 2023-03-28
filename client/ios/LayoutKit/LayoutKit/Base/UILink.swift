import Foundation

import CommonCorePublic
import LayoutKitInterface

public struct UILink: Equatable {
  public let text: String
  public let path: UIElementPath
  public let url: URL?

  public init(text: String, url: URL? = nil, path: UIElementPath) {
    self.text = text
    self.path = path
    self.url = url
  }

  public init(text: String, action: UserInterfaceAction) {
    self.init(text: text, url: action.url, path: action.path)
  }
}

extension UILink {
  public var action: UserInterfaceAction? {
    url.map { UserInterfaceAction(url: $0, path: path) }
  }
}

extension UILink: CustomDebugStringConvertible {
  public var debugDescription: String {
    "{ text: \(text), path:\(path), url:\(dbgStr(url)) }"
  }
}
