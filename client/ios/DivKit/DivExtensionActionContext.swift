import LayoutKit

public struct DivExtensionActionContext {
  /// Runtime path of the target; its view may not exist yet.
  public let path: UIElementPath
  public let extensionId: String
  public let info: DivActionInfo
  /// The handler evaluates payload expressions in the live scope of the action source.
  public let expressionResolver: ExpressionResolver

  let blockStateStorage: DivBlockStateStorage
  let updateCard: DivActionHandler.UpdateCardAction

  public func getState<T: ElementState>() -> T? {
    blockStateStorage.getExtensionState(path: path, extensionId: extensionId)
  }

  /// Stores this extension's state at the target and schedules a card update to apply it.
  public func setState(_ state: ElementState) {
    blockStateStorage.setExtensionState(path: path, extensionId: extensionId, state: state)
    updateCard(.state(path.cardId))
  }
}
