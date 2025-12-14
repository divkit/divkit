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
    let cache = CacheFactory.makeLRUDiskCache(
      name: "divkit.common.cache-queue",
      ioQueue: OperationQueue.serialQueue(
        name: "DivKitCommonResourcesCache",
        qos: .userInitiated
      ),
      maxCapacity: 100 * 1024 * 1024, // 100MB
      fileManager: FileManager.default,
      reportError: { error in
        print("Cache error: \(error)")
      }
    )
    let requestPerformer = URLRequestPerformer(urlTransform: nil)
    let networkRequester = NetworkURLResourceRequester(performer: requestPerformer)
    let cachedRequester = CachedURLResourceRequester(
      cache: cache,
      cachemissRequester: networkRequester
    )
    let lottieExtensionHanlder = LottieExtensionHandler(
      factory: LottieAnimationFactory(),
      requester: cachedRequester
    )
    let variablesStorage = DivVariablesStorage(outerStorage: variableStorage)
    let sizeProviderExtensionHandler = SizeProviderExtensionHandler(
      variablesStorage: variablesStorage
    )

    let holderFactory = SVGImageHolderFactory(requester: cachedRequester)

    return DivKitComponents(
      divCustomBlockFactory: PlaygroundDivCustomBlockFactory(requester: networkRequester),
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
      imageHolderFactory: holderFactory,
      layoutDirection: layoutDirection,
      patchProvider: PlaygroundPatchProvider(requestPerformer: requestPerformer),
      reporter: reporter,
      resourcesPreloader: DivDataResourcesPreloader(resourceRequester: cachedRequester),
      playerFactory: DefaultPlayerFactory(itemsProvider: cachedRequester),
      urlHandler: urlHandler,
      variablesStorage: variablesStorage
    )
  }
}

extension FileManager: @retroactive @unchecked Sendable {}
