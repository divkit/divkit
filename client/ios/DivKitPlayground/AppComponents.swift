import CommonCore
import DivKit
import DivKitExtensions
import Networking

enum AppComponents {
  static func makeDivKitComponents(
    updateCardAction: DivActionURLHandler.UpdateCardAction? = nil
  ) -> DivKitComponents {
    let performer = URLRequestPerformer(urlTransform: nil)
    let requester = NetworkURLResourceRequester(performer: performer)
    let lottieExtensionHanlder = LottieExtensionHandler(
      factory: LottieAnimationFactory(),
      requester: requester
    )
    return DivKitComponents(
      divCustomBlockFactory: PlaygroundDivCustomBlockFactory(requester: requester),
      extensionHandlers: [lottieExtensionHanlder],
      flagsInfo: DivFlagsInfo(isTextSelectingEnabled: true,
                              appendVariablesEnabled: true,
                              metalImageRenderingEnabled: true),
      patchProvider: DemoPatchProvider(),
      updateCardAction: updateCardAction,
      urlOpener: DemoUrlOpener.openUrl(_:)
    )
  }
  
  static var debugParams: DebugParams {
    DebugParams(isDebugInfoEnabled: true)
  }
}
