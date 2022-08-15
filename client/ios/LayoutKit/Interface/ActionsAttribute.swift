import Foundation

public final class ActionsAttribute {
  public static let Key = NSAttributedString.Key("DKActions")

  public let actions: [UserInterfaceAction]

  public init(actions: [UserInterfaceAction]) {
    self.actions = actions
  }

  public func apply(to str: CFMutableAttributedString, at range: CFRange) {
    if !actions.isEmpty {
      CFAttributedStringSetAttribute(str, range, ActionsAttribute.Key as CFString, self)
    }
  }
}
