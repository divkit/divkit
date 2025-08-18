import DivKit
import Foundation
@_spi(Legacy) import DivKitExtensions
import DivKitMarkdownExtension
import DivKitSVG
import LayoutKit
import VGSL

enum AppComponents {
  static let fontProvider = YSFontProvider()

  static var debugParams: DebugParams {
    DebugParams(
      isDebugInfoEnabled: true
    )
  }

  static func makeDivKitComponents(
    layoutDirection: UserInterfaceLayoutDirection = .system,
    reporter: DivReporter = PlaygroundReporter(),
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
        InputAutocorrectionExtensionHandler(),
        InputPropertiesExtensionHandler(),
      ],
      flagsInfo: DivFlagsInfo(initializeTriggerOnSet: false),
      fontProvider: fontProvider,
      imageHolderFactory: SVGImageHolderFactory(requestPerformer: requestPerformer),
      layoutDirection: layoutDirection,
      patchProvider: PlaygroundPatchProvider(requestPerformer: requestPerformer),
      reporter: reporter,
      resourcesPreloader: makeResourcesPreloader(requestPerformer: requestPerformer),
      playerFactory: playerFactory,
      urlHandler: urlHandler,
      variablesStorage: variablesStorage
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
    fileManager: FileManager(),
    reportError: nil
  )
  let requester = CachedURLResourceRequester(
    cache: resourcesCache,
    cachemissRequester: requester,
    waitForCacheWrite: true
  )
  return DefaultPlayerFactory(itemsProvider: requester)
}

private func makeResourcesPreloader(
  requestPerformer: URLRequestPerforming?
) -> DivDataResourcesPreloader? {
  guard let requestPerformer else {
    return nil
  }

  let cacheQueue = OperationQueue.serialQueue(
    name: "divkit.resources-preloader.cache-queue",
    qos: .utility
  )
  let diskCache = CacheFactory.makeLRUDiskCache(
    name: "DivKitPreloadResourcesCache",
    ioQueue: cacheQueue,
    maxCapacity: 100 * 1024 * 1024, // 100 MB
    fileManager: FileManager(),
    reportError: { _ in }
  )
  let networkRequester = NetworkURLResourceRequester(performer: requestPerformer)
  let cachedRequester = CachedURLResourceRequester(
    cache: diskCache,
    cachemissRequester: networkRequester
  )
  return DivDataResourcesPreloader(resourceRequester: cachedRequester)
}

extension FileManager: @retroactive @unchecked Sendable {}
