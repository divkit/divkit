@_spi(Internal) @testable import DivKit
import VGSL

extension DivBlockModelingContext {
  public static let testCardId = DivCardID(rawValue: "test_card_id")

  public static let `default` = DivBlockModelingContext()

  public init(
    cardId: DivCardID = Self.testCardId,
    additionalId: String? = nil,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    extensionHandlers: [DivExtensionHandler] = [],
    scheduler: Scheduling? = nil,
    variableStorage: DivVariableStorage? = nil
  ) {
    self = DivBlockModelingContext(
      cardId: cardId,
      additionalId: additionalId,
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
