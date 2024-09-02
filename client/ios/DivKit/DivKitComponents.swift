import Foundation

import LayoutKit
import Serialization
import VGSL

#if os(iOS)
import UIKit
#else
import AppKit
#endif

public final class DivKitComponents {
  public typealias UpdateCardAction = (NonEmptyArray<DivActionURLHandler.UpdateReason>) -> Void

  public let actionHandler: DivActionHandler
  public let blockStateStorage = DivBlockStateStorage()
  public let divCustomBlockFactory: DivCustomBlockFactory
  public var extensionHandlers: [DivExtensionHandler]
  public let flagsInfo: DivFlagsInfo
  public let fontProvider: DivFontProvider
  public let imageHolderFactory: DivImageHolderFactory
  public let layoutDirection: UserInterfaceLayoutDirection
  public let patchProvider: DivPatchProvider
  public let playerFactory: PlayerFactory?
  public let reporter: DivReporter
  public let safeAreaManager: DivSafeAreaManager
  public let stateManagement: DivStateManagement
  public let showToolip: DivActionURLHandler.ShowTooltipAction?
  public let tooltipManager: TooltipManager
  public let triggersStorage: DivTriggersStorage
  public let urlHandler: DivUrlHandler
  public let variablesStorage: DivVariablesStorage
  public let visibilityCounter = DivVisibilityCounter()

  public var updateCardSignal: Signal<[DivActionURLHandler.UpdateReason]> {
    updateCardPipe.signal
  }

  private let disposePool = AutodisposePool()
  private let lastVisibleBoundsCache = DivLastVisibleBoundsCache()
  private let layoutProviderHandler: DivLayoutProviderHandler
  private let persistentValuesStorage = DivPersistentValuesStorage()
  private let timerStorage: DivTimerStorage
  private let updateAggregator: RunLoopCardUpdateAggregator
  private let updateCard: DivActionURLHandler.UpdateCardAction
  private let updateCardPipe: SignalPipe<[DivActionURLHandler.UpdateReason]>
  private let variableTracker = DivVariableTracker()

  /// You can create an instance of `DivKitComponents` with various optional parameters that allow
  /// you to customize the behavior and functionality of `DivKit` to suit your specific needs.
  ///
  /// - Parameters:
  ///   - divCustomBlockFactory: An optional ``DivCustomBlockFactory`` object that defines a custom
  /// block factory responsible for creating blocks based on custom data and context.
  ///   - extensionHandlers: An array of ``DivExtensionHandler`` objects that enable the extension
  /// of existing blocks. These extensions can involve wrapping blocks in others or adding basic
  /// properties to enhance their behavior.
  ///   - flagsInfo: An optional ``DivFlagsInfo`` object that provides information about new
  /// features added under specific flags.
  ///   - fontProvider: An optional ``DivFontProvider`` object that allows you to specify a custom
  /// font provider.
  ///   - imageHolderFactory: An optional `ImageHolderFactory` object responsible for creating image
  /// holders within `DivKit`.
  ///   - layoutDirection: The user interface layout direction to be used within `DivKit`. This
  /// parameter is set to `.leftToRight` by default.
  ///   - patchProvider: An optional ``DivPatchProvider`` object responsible for downloading
  /// patches.
  ///   - requestPerformer: An optional `URLRequestPerforming` object that performs URL requests for
  /// data retrieval.
  ///   - reporter: An optional `DivReporter` object that allows you to learn about actions and
  /// errors that occur in the layout.
  ///   - showTooltip: Deprecated. This parameter is deprecated, use ``tooltipManager`` instead.
  ///   - stateManagement: An optional ``DivStateManagement`` object responsible for managing card
  /// states.
  ///   - tooltipManager: An optional `TooltipManager` object that manages the processing and
  /// display of tooltips.
  ///   - trackVisibility: A closure that tracks the visibility of elements. Deprecated. Use
  /// ``reporter`` instead.
  ///   - trackDisappear: A closure that tracks the disappearance of elements. Deprecated. Use
  /// ``reporter`` instead.
  ///   - updateCardAction: Deprecated. This parameter is deprecated, use ``updateCardSignal``
  /// instead.
  ///   - playerFactory: An optional `PlayerFactory` object responsible for creating custom video
  /// players.
  ///   - urlHandler: An optional ``DivUrlHandler`` object that allows you to implement custom
  /// action handling for specific URLs.
  ///   - variablesStorage: A ``DivVariablesStorage`` object that handles the storage and retrieval
  /// of variables.
  public init(
    divCustomBlockFactory: DivCustomBlockFactory? = nil,
    extensionHandlers: [DivExtensionHandler] = [],
    flagsInfo: DivFlagsInfo = .default,
    fontProvider: DivFontProvider? = nil,
    imageHolderFactory: DivImageHolderFactory? = nil,
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    patchProvider: DivPatchProvider? = nil,
    requestPerformer: URLRequestPerforming? = nil,
    reporter: DivReporter? = nil,
    showTooltip: DivActionURLHandler.ShowTooltipAction? = nil,
    stateManagement: DivStateManagement = DefaultDivStateManagement(),
    tooltipManager: TooltipManager? = nil,
    trackVisibility: @escaping DivActionHandler.TrackVisibility = { _, _ in },
    trackDisappear: @escaping DivActionHandler.TrackVisibility = { _, _ in },
    updateCardAction: UpdateCardAction? = nil,
    playerFactory: PlayerFactory? = nil,
    urlHandler: DivUrlHandler = DivUrlHandlerDelegate { _ in },
    variablesStorage: DivVariablesStorage = DivVariablesStorage()
  ) {
    self.divCustomBlockFactory = divCustomBlockFactory ?? EmptyDivCustomBlockFactory()
    self.extensionHandlers = extensionHandlers
    self.flagsInfo = flagsInfo
    self.fontProvider = fontProvider ?? DefaultFontProvider()
    self.layoutDirection = layoutDirection
    self.playerFactory = playerFactory ?? defaultPlayerFactory
    let reporter = reporter ?? DefaultDivReporter()
    self.reporter = reporter
    self.showToolip = showTooltip
    self.stateManagement = stateManagement
    self.urlHandler = urlHandler
    self.variablesStorage = variablesStorage

    let updateCardActionSignalPipe = SignalPipe<[DivActionURLHandler.UpdateReason]>()
    self.updateCardPipe = updateCardActionSignalPipe

    layoutProviderHandler = DivLayoutProviderHandler(variablesStorage: variablesStorage)

    safeAreaManager = DivSafeAreaManager(storage: variablesStorage)

    updateAggregator = RunLoopCardUpdateAggregator(updateCardAction: {
      updateCardAction?($0)
      updateCardActionSignalPipe.send($0.asArray())
    })
    updateCard = updateAggregator.aggregate(_:)

    let requestPerformer = requestPerformer ?? URLRequestPerformer(urlTransform: nil)

    self.imageHolderFactory = (
      imageHolderFactory
        ?? DefaultImageHolderFactory(
          requestPerformer: requestPerformer,
          imageLoadingOptimizationEnabled: flagsInfo.imageLoadingOptimizationEnabled
        )
    ).withAssets()

    self.patchProvider = patchProvider
      ?? DivPatchDownloader(requestPerformer: requestPerformer)

    weak var weakTimerStorage: DivTimerStorage?
    weak var weakActionHandler: DivActionHandler?

    #if os(iOS)
    self.tooltipManager = tooltipManager ?? DefaultTooltipManager(
      shownTooltips: .init(),
      handleAction: {
        switch $0.payload {
        case let .divAction(params: params):
          weakActionHandler?.handle(params: params, sender: nil)
        default: break
        }
      }
    )
    #else
    self.tooltipManager = tooltipManager ?? DefaultTooltipManager()
    #endif

    actionHandler = DivActionHandler(
      stateUpdater: stateManagement,
      blockStateStorage: blockStateStorage,
      patchProvider: self.patchProvider,
      variablesStorage: variablesStorage,
      updateCard: updateCard,
      showTooltip: showTooltip,
      tooltipActionPerformer: self.tooltipManager,
      trackVisibility: trackVisibility,
      trackDisappear: trackDisappear,
      performTimerAction: { weakTimerStorage?.perform($0, $1, $2) },
      urlHandler: urlHandler,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter
    )

    triggersStorage = DivTriggersStorage(
      variablesStorage: variablesStorage,
      stateUpdates: blockStateStorage.stateUpdates,
      actionHandler: actionHandler,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter
    )

    timerStorage = DivTimerStorage(
      variablesStorage: variablesStorage,
      actionHandler: actionHandler,
      updateCard: updateCard,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter
    )

    weakActionHandler = actionHandler
    weakTimerStorage = timerStorage
    variablesStorage.changeEvents.addObserver { [weak self] event in
      self?.onVariablesChanged(event: event)
    }.dispose(in: disposePool)
  }

  public func reset() {
    patchProvider.cancelRequests()

    blockStateStorage.reset()
    lastVisibleBoundsCache.reset()
    stateManagement.reset()
    variablesStorage.reset()
    triggersStorage.reset()
    visibilityCounter.reset()
    timerStorage.reset()
  }

  public func reset(cardId: DivCardID) {
    blockStateStorage.reset(cardId: cardId)
    lastVisibleBoundsCache.dropVisibleBounds(prefix: UIElementPath(cardId.rawValue))
    stateManagement.reset(cardId: cardId)
    variablesStorage.reset(cardId: cardId)
    triggersStorage.reset(cardId: cardId)
    visibilityCounter.reset(cardId: cardId)
    timerStorage.reset(cardId: cardId)
  }

  /// When using DivView, use DivData.resolve to avoid adding variables twice.
  public func parseDivData(
    _ jsonDict: [String: Any],
    cardId: DivCardID
  ) throws -> DeserializationResult<DivData> {
    try parseDivDataWithTemplates(["card": jsonDict], cardId: cardId)
  }

  /// When using DivView, use DivData.resolve to avoid adding variables twice.
  ///
  /// Parses DivData from JSON in following format:
  /// {
  ///   "card": { ... },
  ///   "templates": { ... }
  /// }
  public func parseDivDataWithTemplates(
    _ jsonDict: [String: Any],
    cardId: DivCardID
  ) throws -> DeserializationResult<DivData> {
    let rawDivData = try RawDivData(dictionary: jsonDict)
    let result = DivData.resolve(
      card: rawDivData.card,
      templates: rawDivData.templates
    ).asCardResult(cardId: cardId)
    if let divData = result.value {
      setVariablesAndTriggers(divData: divData, cardId: cardId)
      setTimers(divData: divData, cardId: cardId)
    }
    return result
  }

  /// When using DivView, use DivData.resolve to avoid adding variables twice.
  ///
  /// Parses DivData from JSON in following format:
  /// {
  ///   "card": { ... },
  ///   "templates": { ... }
  /// }
  public func parseDivDataWithTemplates(
    _ jsonData: Data,
    cardId: DivCardID
  ) throws -> DeserializationResult<DivData> {
    guard let jsonObj = try? JSONSerialization.jsonObject(with: jsonData),
          let jsonDict = jsonObj as? [String: Any] else {
      throw DeserializationError.nestedObjectError(
        field: cardId.rawValue,
        error: .invalidJSONData(data: jsonData)
      )
    }
    return try parseDivDataWithTemplates(jsonDict, cardId: cardId)
  }

  public func makeContext(
    cardId: DivCardID,
    additionalId: String? = nil,
    cachedImageHolders: [ImageHolder],
    debugParams: DebugParams = DebugParams(),
    parentScrollView: ScrollView? = nil
  ) -> DivBlockModelingContext {
    variableTracker.onModelingStarted(id: DivViewId(cardId: cardId, additionalId: additionalId))
    return DivBlockModelingContext(
      cardId: cardId,
      additionalId: additionalId,
      stateManager: stateManagement.getStateManagerForCard(cardId: cardId),
      blockStateStorage: blockStateStorage,
      visibilityCounter: visibilityCounter,
      lastVisibleBoundsCache: lastVisibleBoundsCache,
      imageHolderFactory: imageHolderFactory.withCache(cachedImageHolders),
      divCustomBlockFactory: divCustomBlockFactory,
      fontProvider: fontProvider,
      flagsInfo: flagsInfo,
      extensionHandlers: extensionHandlers,
      variablesStorage: variablesStorage,
      triggersStorage: triggersStorage,
      playerFactory: playerFactory,
      debugParams: debugParams,
      parentScrollView: parentScrollView,
      layoutDirection: layoutDirection,
      variableTracker: variableTracker,
      persistentValuesStorage: persistentValuesStorage,
      tooltipViewFactory: DivTooltipViewFactory(divKitComponents: self, cardId: cardId),
      layoutProviderHandler: layoutProviderHandler
    )
  }

  public func setVariablesAndTriggers(divData: DivData, cardId: DivCardID) {
    updateAggregator.performWithNoUpdates {
      let divDataVariables = divData.variables?.extractDivVariableValues() ?? [:]
      variablesStorage.append(
        variables: divDataVariables,
        for: cardId,
        replaceExisting: false
      )

      triggersStorage.set(
        cardId: cardId,
        triggers: divData.variableTriggers ?? []
      )
    }
  }

  public func setTimers(divData: DivData, cardId: DivCardID) {
    timerStorage.set(cardId: cardId, timers: divData.timers ?? [])
  }

  public func flushUpdateActions() {
    updateAggregator.flushUpdateActions()
  }

  public func forceUpdate() {
    updateAggregator.forceUpdate()
  }

  private func onVariablesChanged(event: DivVariablesStorage.ChangeEvent) {
    switch event.kind {
    case let .global(variables):
      let affectedCards = variableTracker.getAffectedCards(variables: variables)
      if !affectedCards.isEmpty {
        updateCard(.variable(affectedCards))
      }
    case let .local(cardId, variables):
      updateCard(.variable([cardId: variables]))
    }
  }
}

#if os(iOS)
let defaultPlayerFactory: PlayerFactory? = DefaultPlayerFactory()
#else
let defaultPlayerFactory: PlayerFactory? = nil
#endif
