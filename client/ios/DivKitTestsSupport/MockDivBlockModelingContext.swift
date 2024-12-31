import DivKit
import VGSL

extension DivBlockModelingContext {
  public static let testCardId = DivCardID(rawValue: "test_card_id")

  public static let `default` = DivBlockModelingContext()

  public init(
    cardId: DivCardID = testCardId,
    additionalId: String? = nil,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    extensionHandlers: [DivExtensionHandler] = [],
    scheduler: Scheduling? = nil,
    variableStorage: DivVariableStorage? = nil
  ) {
    self.init(
      cardId: cardId,
      additionalId: additionalId,
      cardLogId: cardId.rawValue,
      stateManager: DivStateManager(),
      blockStateStorage: blockStateStorage,
      imageHolderFactory: FakeImageHolderFactory(),
      extensionHandlers: extensionHandlers,
      variablesStorage: DivVariablesStorage(outerStorage: variableStorage),
      scheduler: scheduler,
      persistentValuesStorage: DivPersistentValuesStorage()
    )
  }
}
