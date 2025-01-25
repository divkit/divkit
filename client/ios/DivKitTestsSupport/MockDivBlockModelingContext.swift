import DivKit
import VGSL

extension DivBlockModelingContext {
  public static let testCardId = DivCardID(rawValue: "test_card_id")

  public static let `default` = DivBlockModelingContext()

  public init(
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    extensionHandlers: [DivExtensionHandler] = [],
    scheduler: Scheduling? = nil,
    variableStorage: DivVariableStorage? = nil
  ) {
    self.init(
      cardId: Self.testCardId,
      cardLogId: Self.testCardId.rawValue,
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
