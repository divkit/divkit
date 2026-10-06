@_spi(Internal) @testable import DivKit
import VGSL

extension DivBlockModelingContext {
  public static let testCardId = DivCardID(rawValue: "test_card_id")

  public static var `default`: DivBlockModelingContext {
    DivBlockModelingContext()
  }

  public init(
    cardId: DivCardID = Self.testCardId,
    actionHandler: DivActionHandler? = nil,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    extensionHandlers: [DivExtensionHandler] = [],
    scheduler: Scheduling? = nil,
    variableStorage: DivVariableStorage? = nil
  ) {
    self = DivBlockModelingContext(
      cardId: cardId,
      actionHandler: actionHandler,
      blockStateStorage: blockStateStorage,
      imageHolderFactory: FakeImageHolderFactory(),
      extensionHandlers: extensionHandlers,
      variablesStorage: DivVariablesStorage(outerStorage: variableStorage),
      scheduler: scheduler
    ).modifying(
      cardLogId: cardId.rawValue
    )
  }
}
