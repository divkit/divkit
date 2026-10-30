/// Handles an extension action for a modeled target, possibly before its view exists.
/// Called on the thread handling the action; implementations must synchronize shared state.
/// Missing targets retain the latest command per address until modeled or the card is reset.
/// Payload expressions are evaluated by the handler using the source resolver at delivery time.
public protocol DivExtensionActionHandler {
  /// Returns false when the payload is not a supported command.
  func handleAction(context: DivExtensionActionContext) -> Bool
}
