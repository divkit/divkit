import Foundation

import DivKit
import DivKitExtensions
import DivKitMarkdownExtension
import LayoutKit
import VGSL

enum AppComponents {
  static let fontProvider = YSFontProvider()

  static func makeDivKitComponents(
    layoutDirection: UserInterfaceLayoutDirection = .system,
    reporter: DivReporter? = nil,
    urlHandler: DivUrlHandler = DivUrlHandlerDelegate { _, _ in },
    variableStorage: DivVariableStorage? = nil
  ) -> DivKitComponents {
    let requestPerformer = URLRequestPerformer(urlTransform: nil)
    let requester = NetworkURLResourceRequester(performer: requestPerformer)
    let lottieExtensionHanlder = LottieExtensionHandler(
      factory: LottieAnimationFactory(),
      requester: requester
    )
    let variablesStorage = DivVariablesStorage(outerStorage: variableStorage)
    let sizeProviderExtensionHandler = SizeProviderExtensionHandler(
      variablesStorage: variablesStorage
    )

    let playerFactory = makeCachingPlayerFactory(requester: requester)
    return DivKitComponents(
      divCustomBlockFactory: PlaygroundDivCustomBlockFactory(requester: requester),
      extensionHandlers: [
        lottieExtensionHanlder,
        sizeProviderExtensionHandler,
        InputAccessoryViewExtensionHandler(viewProvider: AccessoryViewProvider()),
        ShimmerImagePreviewExtension(),
        VideoDurationExtensionHandler(),
        GestureExtensionHandler(),
        MarkdownExtensionHandler(),
        ShineExtensionHandler(),
      ],
      flagsInfo: DivFlagsInfo(imageLoadingOptimizationEnabled: true),
      fontProvider: fontProvider,
      layoutDirection: layoutDirection,
      patchProvider: PlaygroundPatchProvider(requestPerformer: requestPerformer),
      reporter: reporter,
      trackVisibility: { logId, cardId in
        AppLogger.info("Visibility: cardId = \(cardId), logId = \(logId)")
      },
      trackDisappear: { logId, cardId in
        AppLogger.info("Disappear: cardId = \(cardId), logId = \(logId)")
      },
      playerFactory: playerFactory,
      urlHandler: urlHandler,
      variablesStorage: variablesStorage
    )
  }

  static var debugParams: DebugParams {
    DebugParams(
      isDebugInfoEnabled: true
    )
  }
}

private func makeCachingPlayerFactory(requester: URLResourceRequesting) -> PlayerFactory {
  let cacheQueue = OperationQueue.serialQueue(
    name: "divkit.playground.VideoAssetCache.ioqueue",
    qos: .userInitiated
  )
  let resourcesCache = CacheFactory.makeLRUDiskCache(
    name: "divkit.playground.VideoAssetCache",
    ioQueue: cacheQueue,
    maxCapacity: 10_485_760,
    reportError: nil
  )
  let requester = CachedURLResourceRequester(
    cache: resourcesCache,
    cachemissRequester: requester,
    waitForCacheWrite: true
  )
  return DefaultPlayerFactory(itemsProvider: requester)
}
