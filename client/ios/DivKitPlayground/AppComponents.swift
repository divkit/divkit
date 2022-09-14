import CommonCore
import DivKit
import DivKitExtensions
import Networking

enum AppComponents {
  static func makeDivKitComponents(
    updateCardAction: DivActionURLHandler.UpdateCardAction? = nil
  ) -> DivKitComponents {
    let performer = URLRequestPerformer(urlTransform: nil)
    let lottieExtensionHanlder = LottiesExtensionHandler(
      factory: LottieAnimationFactory(),
      requester: NetworkURLResourceRequester(performer: performer)
    )
    return DivKitComponents(
      divCustomBlockFactory: DemoDivCustomBlockFactory(),
      extensionHandlers: [lottieExtensionHanlder],
      flagsInfo: DivFlagsInfo(isTextSelectingEnabled: true, appendVariablesEnabled: true),
      patchProvider: DemoPatchProvider(),
      updateCardAction: updateCardAction,
      urlOpener: DemoUrlOpener.openUrl(_:)
    )
  }
  
  static var debugParams: DebugParams {
    DebugParams(isDebugInfoEnabled: true)
  }
}
