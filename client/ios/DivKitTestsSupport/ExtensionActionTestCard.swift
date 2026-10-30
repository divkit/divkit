@_spi(Internal) @_spi(Legacy) import DivKit
import LayoutKit
import Serialization
import UIKit

@MainActor
public final class ExtensionActionTestCard {
  public let components: DivKitComponents
  public let view: DivView
  public let cardId: DivCardID

  public init(
    root: [String: Any],
    handlers: [DivExtensionHandler],
    reporter: DivReporter,
    cardId: String = "extension_card",
    components: DivKitComponents? = nil
  ) {
    self.cardId = DivCardID(rawValue: cardId)
    self.components = components ?? DivKitComponents(
      extensionHandlers: handlers,
      reporter: reporter
    )
    view = DivView(divKitComponents: self.components)
    view.frame = CGRect(x: 0, y: 0, width: 320, height: 480)
    setRoot(root)
  }

  public func setRoot(_ root: [String: Any]) {
    view.setSource(DivViewSource(
      kind: .json(["card": [
        "log_id": "extension_test",
        "states": [["state_id": 0, "div": root]],
      ]]),
      cardId: cardId
    ))
    view.layoutIfNeeded()
  }

  public func send(
    _ command: String,
    extensionId: String = "test",
    targetId: String = "target",
    scopeId: String? = nil,
    sourcePath: String? = nil,
    source: UserInterfaceAction.DivActionSource = .tap,
    payload: [String: Any]? = nil,
    isEnabled: Bool = true
  ) throws {
    var dictionary: [String: Any] = [
      "log_id": "extension_command",
      "typed": ["type": "extension_action", "extension_id": extensionId, "div_id": targetId],
      "payload": payload ?? ["type": command],
      "is_enabled": isEnabled,
    ]
    dictionary["scope_id"] = scopeId
    let action: DivActionBase = switch source {
    case .visibility: try DivVisibilityAction(dictionary: dictionary, context: ParsingContext())
    case .disappear: try DivDisappearAction(dictionary: dictionary, context: ParsingContext())
    default: try DivAction(dictionary: dictionary, context: ParsingContext())
    }
    components.actionHandler.handle(
      action,
      path: sourcePath.map(UIElementPath.parse) ?? UIElementPath(cardId.rawValue),
      source: source,
      sender: nil
    )
  }
}
