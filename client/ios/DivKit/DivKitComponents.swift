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
  public let submitter: DivSubmitter
  public let tooltipManager: TooltipManager
  public let triggersStorage: DivTriggersStorage
  public let urlHandler: DivUrlHandler
  public let variablesStorage: DivVariablesStorage
  @_spi(Internal)
  public let visibilityCounter = DivVisibilityCounter()
  public let resourcesPreloader: DivDataResourcesPreloader?

  private let animatorController = DivAnimatorController()
  private let disposePool = AutodisposePool()
  private let idToPath = IdToPath()
  private let functionsStorage: DivFunctionsStorage
  private let lastVisibleBoundsCache = DivLastVisibleBoundsCache()
  private let layoutProviderHandler: DivLayoutProviderHandler
  private let persistentValuesStorage = DivPersistentValuesStorage()
  private let timerStorage: DivTimerStorage
  private let updateAggregator: RunLoopCardUpdateAggregator
  private let updateCard: DivActionHandler.UpdateCardAction
  private let updateCardPipe: SignalPipe<[DivCardUpdateReason]>
  private let variableTracker = DivVariableTracker()
  private var debugErrorCollectors = [DivCardID: DebugErrorCollector]()

  public var updateCardSignal: Signal<[DivCardUpdateReason]> {
    updateCardPipe.signal
  }

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
  ///   - submitter: An optional ``DivSubmitter`` object responsible for submitting data from
  /// container.
  ///   - tooltipManager: An optional `TooltipManager` object that manages the processing and
  /// display of tooltips.
  ///   - trackVisibility: A closure that tracks the visibility of elements. Deprecated. Use
  /// ``reporter`` instead.
  ///   - trackDisappear: A closure that tracks the disappearance of elements. Deprecated. Use
  /// ``reporter`` instead.
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
    resourcesPreloader: DivDataResourcesPreloader? = nil,
    showTooltip: DivActionHandler.ShowTooltipAction? = nil,
    stateManagement: DivStateManagement = DefaultDivStateManagement(),
    submitter: DivSubmitter? = nil,
    tooltipManager: TooltipManager? = nil,
    trackVisibility: @escaping DivActionHandler.TrackVisibility = { _, _ in },
    trackDisappear: @escaping DivActionHandler.TrackVisibility = { _, _ in },
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
    self.stateManagement = stateManagement
    self.urlHandler = urlHandler
    self.variablesStorage = variablesStorage
    self.resourcesPreloader = resourcesPreloader
    let updateCardPipe = SignalPipe<[DivCardUpdateReason]>()
    self.updateCardPipe = updateCardPipe

    layoutProviderHandler = DivLayoutProviderHandler(variablesStorage: variablesStorage)

    safeAreaManager = DivSafeAreaManager(storage: variablesStorage)

    updateAggregator = RunLoopCardUpdateAggregator(updateCardAction: updateCardPipe.send)
    updateCard = updateAggregator.aggregate(_:)

    let requestPerformer = requestPerformer ?? URLRequestPerformer(urlTransform: nil)

    self.imageHolderFactory = (
      imageHolderFactory ?? DefaultImageHolderFactory(requestPerformer: requestPerformer)
    ).withAssets()

    self.submitter = submitter
      ?? DivNetworkSubmitter(requestPerformer: requestPerformer)
    self.patchProvider = patchProvider
      ?? DivPatchDownloader(requestPerformer: requestPerformer)

    self.tooltipManager = tooltipManager ?? DefaultTooltipManager()

    functionsStorage = DivFunctionsStorage(reporter: reporter)

    weak var weakTimerStorage: DivTimerStorage?
    actionHandler = DivActionHandler(
      stateUpdater: stateManagement,
      blockStateStorage: blockStateStorage,
      patchProvider: self.patchProvider,
      submitter: self.submitter,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      updateCard: updateCard,
      showTooltip: showTooltip,
      tooltipActionPerformer: self.tooltipManager,
      trackVisibility: trackVisibility,
      trackDisappear: trackDisappear,
      performTimerAction: { weakTimerStorage?.perform($0, $1, $2) },
      urlHandler: urlHandler,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter,
      idToPath: idToPath,
      animatorController: animatorController,
      flags: flagsInfo
    )

    triggersStorage = DivTriggersStorage(
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      blockStateStorage: blockStateStorage,
      actionHandler: actionHandler,
      persistentValuesStorage: persistentValuesStorage,
      flagsInfo: flagsInfo,
      reporter: reporter
    )

    timerStorage = DivTimerStorage(
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      actionHandler: actionHandler,
      updateCard: updateCard,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter
    )

    weakTimerStorage = timerStorage
    variablesStorage.changeEvents.addObserver { [weak self] event in
      self?.onVariablesChanged(event: event)
    }.dispose(in: disposePool)

    #if os(iOS)
    self.tooltipManager.setHandler { [weak self] in
      switch $0.payload {
      case let .divAction(params):
        self?.actionHandler.handle(params: params, sender: nil)
      default:
        break
      }
    }
    #endif
  }

  public func reset() {
    patchProvider.cancelRequests()
    submitter.cancelRequests()

    blockStateStorage.reset()
    lastVisibleBoundsCache.reset()
    stateManagement.reset()
    variablesStorage.reset()
    triggersStorage.reset()
    functionsStorage.reset()
    visibilityCounter.reset()
    timerStorage.reset()
    tooltipManager.reset()
    idToPath.reset()
    animatorController.reset()
    debugErrorCollectors = [:]
  }

  public func reset(cardId: DivCardID) {
    blockStateStorage.reset(cardId: cardId)
    lastVisibleBoundsCache.dropVisibleBounds(prefix: UIElementPath(cardId.rawValue))
    stateManagement.reset(cardId: cardId)
    variablesStorage.reset(cardId: cardId)
    triggersStorage.reset(cardId: cardId)
    functionsStorage.reset(cardId: cardId)
    visibilityCounter.reset(cardId: cardId)
    timerStorage.reset(cardId: cardId)
    idToPath.reset(cardId: cardId)
    animatorController.reset(cardId: cardId)
    debugErrorCollectors[cardId] = nil
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
      setCardData(divData: divData, cardId: cardId)
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
    let viewId = DivViewId(cardId: cardId, additionalId: additionalId)
    variableTracker.onModelingStarted(id: viewId)

    let errorsStorage = DivErrorsStorage(errors: [])
    return DivBlockModelingContext(
      viewId: viewId,
      stateManager: stateManagement.getStateManagerForCard(cardId: cardId),
      actionHandler: actionHandler,
      blockStateStorage: blockStateStorage,
      visibilityCounter: visibilityCounter,
      lastVisibleBoundsCache: lastVisibleBoundsCache,
      imageHolderFactory: imageHolderFactory
        .withCache(cachedImageHolders),
      divCustomBlockFactory: divCustomBlockFactory,
      fontProvider: fontProvider,
      flagsInfo: flagsInfo,
      extensionHandlers: extensionHandlers.dictionary,
      functionsStorage: functionsStorage,
      variablesStorage: variablesStorage,
      triggersStorage: triggersStorage,
      playerFactory: playerFactory,
      debugParams: debugParams,
      scheduler: nil,
      parentScrollView: parentScrollView,
      errorsStorage: errorsStorage,
      debugErrorCollector: debugErrorCollector(
        for: cardId,
        debugParams: debugParams,
        errorsStorage: errorsStorage
      ),
      layoutDirection: layoutDirection,
      variableTracker: variableTracker,
      persistentValuesStorage: persistentValuesStorage,
      tooltipViewFactory: DivTooltipViewFactory(
        divKitComponents: self,
        cardId: cardId
      ),
      layoutProviderHandler: layoutProviderHandler,
      idToPath: idToPath,
      animatorController: animatorController
    )
  }

  public func setCardData(divData: DivData, cardId: DivCardID) {
    setTimers(divData: divData, cardId: cardId)
    setVariablesAndTriggers(divData: divData, cardId: cardId)
    functionsStorage.set(cardId: cardId, functions: divData.functions ?? [])
  }

  public func setVariablesAndTriggers(divData: DivData, cardId: DivCardID) {
    updateAggregator.performWithNoUpdates {
      let resolver = ExpressionResolver(
        path: cardId.path,
        variablesStorage: variablesStorage,
        functionsStorage: functionsStorage,
        persistentValuesStorage: persistentValuesStorage,
        reporter: reporter
      )
      let divDataVariables = divData.variables?.extractDivVariableValues(resolver) ?? [:]
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

  #if os(iOS)
  @_spi(Internal)
  public func renderingDelegate(for cardId: DivCardID) -> RenderingDelegate {
    ErrorsReportingRenderingDelegate(
      wrappedRenderingDelegate: tooltipManager,
      cardId: cardId,
      errorReporter: debugErrorCollectors[cardId],
      renderingReporter: reporter
    )
  }
  #endif

  private func debugErrorCollector(
    for cardId: DivCardID,
    debugParams: DebugParams,
    errorsStorage: DivErrorsStorage
  ) -> DebugErrorCollector? {
    guard debugParams.isDebugInfoEnabled else { return nil }
    let collector = debugErrorCollectors.getOrCreate(cardId, factory: {
      DebugErrorCollector(wrappedDivReporter: reporter, errorStorage: errorsStorage)
    })
    collector.errorStorage = errorsStorage
    return collector
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

#if os(iOS)
private final class ErrorsReportingRenderingDelegate: RenderingDelegate {
  private let wrappedRenderingDelegate: RenderingDelegate
  private let cardId: DivCardID
  private let errorReporter: DivReporter?
  private let renderingReporter: DivReporter

  init(
    wrappedRenderingDelegate: RenderingDelegate,
    cardId: DivCardID,
    errorReporter: DivReporter?,
    renderingReporter: DivReporter
  ) {
    self.wrappedRenderingDelegate = wrappedRenderingDelegate
    self.cardId = cardId
    self.errorReporter = errorReporter
    self.renderingReporter = renderingReporter
  }

  func reportRenderingError(message: String, isWarning: Bool, path: UIElementPath) {
    wrappedRenderingDelegate.reportRenderingError(
      message: message,
      isWarning: isWarning,
      path: path
    )
    guard let errorReporter else { return }
    let error: DivError = if isWarning {
      DivLayoutWarning(message, path: path)
    } else {
      DivLayoutError(message, path: path)
    }
    errorReporter.reportError(cardId: cardId, error: error)
  }

  func reportViewWasCreated() {
    renderingReporter.reportViewWasCreated(cardId: cardId)
  }

  func reportBlockWillConfigure(path: UIElementPath) {
    renderingReporter.reportBlockWillConfigure(path: path)
  }

  func reportBlockDidConfigure(path: UIElementPath) {
    renderingReporter.reportBlockDidConfigure(path: path)
  }

  func reportViewWillLayout(path: UIElementPath) {
    renderingReporter.reportViewWillLayout(path: path)
  }

  func reportViewDidLayout(path: UIElementPath) {
    renderingReporter.reportViewDidLayout(path: path)
  }

  func mapView(_ view: any LayoutKit.BlockView, to id: LayoutKit.BlockViewID) {
    wrappedRenderingDelegate.mapView(view, to: id)
  }

  func tooltipAnchorViewAdded(anchorView: any LayoutKit.TooltipAnchorView) {
    wrappedRenderingDelegate.tooltipAnchorViewAdded(anchorView: anchorView)
  }

  func tooltipAnchorViewRemoved(anchorView: any LayoutKit.TooltipAnchorView) {
    wrappedRenderingDelegate.tooltipAnchorViewRemoved(anchorView: anchorView)
  }
}
#endif
