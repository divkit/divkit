import CommonCore
import DivKit

enum AppComponents {
  static func makeDivKitComponents(
    updateCardAction: DivActionURLHandler.UpdateCardAction? = nil
  ) -> DivKitComponents {
    DivKitComponents(
      divCustomBlockFactory: DemoDivCustomBlockFactory(),
      flagsInfo: DivFlagsInfo(isTextSelectingEnabled: true, appendVariablesEnabled: true),
      patchProvider: DemoPatchProvider(),
      updateCardAction: updateCardAction,
      urlOpener: DemoUrlOpener.openUrl(_:)
    )
  }
}
