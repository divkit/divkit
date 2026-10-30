import DivKit
import LayoutKit

final class RecordingExtensionActionHandler: DivExtensionHandler, DivExtensionActionHandler {
  let id = "test"
  private(set) var payload: [String: Any]?
  private(set) var context: DivExtensionActionContext?
  private(set) var acceptCount = 0
  private(set) var acceptedPaths: Set<String> = []
  var handlesAction = true
  private(set) var commands: [String] = []
  private(set) var paths: [String] = []
  var onAction: (() -> Void)?

  var actionHandler: DivExtensionActionHandler? { self }

  func accept(div _: DivBase, context: DivBlockModelingContext) {
    acceptCount += 1
    acceptedPaths.insert(context.path.description)
  }

  func handleAction(context: DivExtensionActionContext) -> Bool {
    let payload = context.info.payload
    commands.append(payload?["type"] as? String ?? "")
    paths.append(context.path.description)
    onAction?()
    self.payload = payload
    self.context = context
    return handlesAction
  }
}
