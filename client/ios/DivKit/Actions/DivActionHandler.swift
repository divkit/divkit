import Foundation
import LayoutKit
import Serialization
import VGSL

public final class DivActionHandler {
  public typealias ShowTooltipAction = (TooltipInfo) -> Void
  public typealias TrackVisibility = (_ logId: String, _ cardId: DivCardID) -> Void

  typealias PerformTimerAction = (
    _ cardId: DivCardID,
    _ timerId: String,
    _ action: DivTimerAction
  ) -> Void

  typealias UpdateCardAction = (DivCardUpdateReason) -> Void

  private let urlHandler: DivUrlHandler
  private let trackVisibility: TrackVisibility
  private let trackDisappear: TrackVisibility
  private let variablesStorage: DivVariablesStorage
  private let functionsStorage: DivFunctionsStorage?
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let blockStateStorage: DivBlockStateStorage
  private let updateCard: UpdateCardAction
  private let reporter: DivReporter
  private let idToPath: IdToPath
  private let flags: DivFlagsInfo

  private let animatorActionHandler: AnimatorActionHandler
  private let arrayActionsHandler = ArrayActionsHandler()
  private let clearFocusActionHandler = ClearFocusActionHandler()
  private let copyToClipboardActionHandler = CopyToClipboardActionHandler()
  private let dictSetValueActionHandler = DictSetValueActionHandler()
  private let downloadActionHandler: DownloadActionHandler
  private let focusElementActionHandler = FocusElementActionHandler()
  private let scrollActionHandler: ScrollActionHandler
  private let setStateActionHandler: SetStateActionHandler
  private let setStoredValueActionHandler: SetStoredValueActionHandler
  private let setVariableActionHandler = SetVariableActionHandler()
  private let submitActionHandler: SubmitActionHandler
  private let timerActionHandler: TimerActionHandler
  private let tooltipActionHandler: TooltipActionHandler
  private let videoActionHandler = VideoActionHandler()

  /// Do not create `DivActionHandler`. Use the instance from `DivKitComponents`.
  @_spi(Legacy)
  public convenience init(
    stateUpdater: DivStateUpdater = DefaultDivStateManagement(),
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    patchProvider: DivPatchProvider,
    submitter: DivSubmitter,
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    functionsStorage: DivFunctionsStorage? = nil,
    updateCard: @escaping (DivCardUpdateReason) -> Void,
    showTooltip: ShowTooltipAction? = nil,
    tooltipActionPerformer: TooltipActionPerformer? = nil,
    trackVisibility: @escaping TrackVisibility = { _, _ in },
    trackDisappear: @escaping TrackVisibility = { _, _ in },
    urlHandler: DivUrlHandler,
    persistentValuesStorage: DivPersistentValuesStorage = DivPersistentValuesStorage(),
    reporter: DivReporter? = nil
  ) {
    self.init(
      stateUpdater: stateUpdater,
      blockStateStorage: blockStateStorage,
      patchProvider: patchProvider,
      submitter: submitter,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      updateCard: updateCard,
      showTooltip: showTooltip,
      tooltipActionPerformer: tooltipActionPerformer,
      trackVisibility: trackVisibility,
      trackDisappear: trackDisappear,
      performTimerAction: { _, _, _ in },
      urlHandler: urlHandler,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter ?? DefaultDivReporter(),
      idToPath: IdToPath(),
      animatorController: DivAnimatorController(),
      flags: .default
    )
  }

  init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage,
    patchProvider: DivPatchProvider,
    submitter: DivSubmitter,
    variablesStorage: DivVariablesStorage,
    functionsStorage: DivFunctionsStorage?,
    updateCard: @escaping UpdateCardAction,
    showTooltip: ShowTooltipAction?,
    tooltipActionPerformer: TooltipActionPerformer?,
    trackVisibility: @escaping TrackVisibility,
    trackDisappear: @escaping TrackVisibility,
    performTimerAction: @escaping PerformTimerAction,
    urlHandler: DivUrlHandler,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter,
    idToPath: IdToPath,
    animatorController: DivAnimatorController,
    flags: DivFlagsInfo
  ) {
    self.urlHandler = urlHandler
    self.trackVisibility = trackVisibility
    self.trackDisappear = trackDisappear
    self.variablesStorage = variablesStorage
    self.functionsStorage = functionsStorage
    self.persistentValuesStorage = persistentValuesStorage
    self.blockStateStorage = blockStateStorage
    self.updateCard = updateCard
    self.reporter = reporter
    self.idToPath = idToPath
    self.flags = flags

    animatorActionHandler = AnimatorActionHandler(animatorController: animatorController)
    downloadActionHandler = DownloadActionHandler(
      patchProvider: patchProvider,
      updateCard: updateCard
    )
    scrollActionHandler = ScrollActionHandler(
      blockStateStorage: blockStateStorage,
      updateCard: updateCard
    )
    setStateActionHandler = SetStateActionHandler(stateUpdater: stateUpdater)
    setStoredValueActionHandler = SetStoredValueActionHandler(
      persistentValuesStorage: persistentValuesStorage
    )
    submitActionHandler = SubmitActionHandler(submitter: submitter)
    timerActionHandler = TimerActionHandler(performer: performTimerAction)
    tooltipActionHandler = TooltipActionHandler(
      performer: tooltipActionPerformer,
      showTooltip: showTooltip
    )
  }

  public func handle(
    params: UserInterfaceAction.DivActionParams,
    sender: AnyObject?
  ) {
    let action: DivActionBase? = switch params.source {
    case .visibility:
      parseAction(type: DivVisibilityActionTemplate.self, json: params.action)
    case .disappear:
      parseAction(type: DivDisappearActionTemplate.self, json: params.action)
    default:
      parseAction(type: DivActionTemplate.self, json: params.action)
    }
    guard let action else {
      return
    }

    handle(
      action,
      path: params.path,
      source: params.source,
      localValues: params.localValues,
      sender: sender
    )
  }

  @_spi(Internal)
  public func handle(
    _ action: DivActionBase,
    path: UIElementPath,
    source: UserInterfaceAction.DivActionSource,
    localValues: [String: AnyHashable] = [:],
    sender: AnyObject?
  ) {
    let cardId = path.cardId
    let expressionResolver = ExpressionResolver(
      functionsProvider: FunctionsProvider(
        persistentValuesStorage: persistentValuesStorage
      ),
      customFunctionsStorageProvider: { [weak functionsStorage] in
        functionsStorage?.getStorage(path: path, contains: $0)
      },
      variableValueProvider: { [unowned variablesStorage] in
        if let value = localValues[$0] {
          return value
        }
        let variableName = DivVariableName(rawValue: $0)
        return variablesStorage.getVariableValue(path: path, name: variableName)
      },
      errorTracker: reporter.asExpressionErrorTracker(cardId: cardId)
    )

    guard action.resolveIsEnabled(expressionResolver) else {
      return
    }

    let path = if let scopeId = action.scopeId {
      idToPath[path.cardId.path + scopeId] ?? path
    } else {
      path
    }
    let info = action.resolveInfo(expressionResolver, path: path, source: source)
    let context = DivActionHandlingContext(
      info: info,
      expressionResolver: expressionResolver,
      variablesStorage: variablesStorage,
      blockStateStorage: blockStateStorage,
      actionHandler: self,
      updateCard: updateCard
    )

    switch action.typed {
    case let .divActionAnimatorStart(action):
      animatorActionHandler.handle(action, context: context)
    case let .divActionAnimatorStop(action):
      animatorActionHandler.handle(action, context: context)
    case let .divActionArrayInsertValue(action):
      arrayActionsHandler.handle(action, context: context)
    case let .divActionArrayRemoveValue(action):
      arrayActionsHandler.handle(action, context: context)
    case let .divActionArraySetValue(action):
      arrayActionsHandler.handle(action, context: context)
    case .divActionClearFocus:
      clearFocusActionHandler.handle(context: context)
    case let .divActionCopyToClipboard(action):
      copyToClipboardActionHandler.handle(action, context: context)
    case let .divActionDictSetValue(action):
      dictSetValueActionHandler.handle(action, context: context)
    case let .divActionDownload(action):
      downloadActionHandler.handle(action, context: context)
    case let .divActionFocusElement(action):
      focusElementActionHandler.handle(action, context: context)
    case let .divActionHideTooltip(action):
      tooltipActionHandler.handle(action, context: context)
    case let .divActionScrollBy(action):
      scrollActionHandler.handle(action, context: context)
    case let .divActionScrollTo(action):
      scrollActionHandler.handle(action, context: context)
    case let .divActionSetVariable(action):
      setVariableActionHandler.handle(action, context: context)
    case let .divActionSetState(action):
      setStateActionHandler.handle(action, context: context)
    case let .divActionSetStoredValue(action):
      setStoredValueActionHandler.handle(action, context: context)
    case let .divActionShowTooltip(action):
      tooltipActionHandler.handle(action, context: context)
    case let .divActionSubmit(action):
      submitActionHandler.handle(action, context: context)
    case let .divActionTimer(action):
      timerActionHandler.handle(action, context: context)
    case .divActionUpdateStructure(_):
      break
    case let .divActionVideo(action):
      videoActionHandler.handle(action, context: context)
    case .none:
      handleUrl(action, context: context, sender: sender)
    }

    reporter.reportAction(cardId: cardId, info: info)

    if source == .visibility {
      trackVisibility(info.logId, cardId)
    } else if source == .disappear {
      trackDisappear(info.logId, cardId)
    }
  }

  private func handleUrl(
    _ action: DivActionBase,
    context: DivActionHandlingContext,
    sender: AnyObject?
  ) {
    guard let url = context.info.url else {
      return
    }

    if let intent = DivActionIntent(url: url) {
      let cardId = context.cardId
      switch intent {
      case let .showTooltip(id, multiple):
        let tooltipInfo = TooltipInfo(id: id, showsOnStart: false, multiple: multiple)
        tooltipActionHandler.showTooltip(tooltipInfo)
      case let .hideTooltip(id):
        tooltipActionHandler.hideTooltip(id: id)
      case let .download(patchUrl):
        downloadActionHandler.handle(
          url: patchUrl,
          context: context,
          onSuccessActions: action.downloadCallbacks?.onSuccessActions,
          onFailActions: action.downloadCallbacks?.onFailActions
        )
      case let .setState(divStatePath, lifetime):
        setStateActionHandler.handle(
          divStatePath: divStatePath,
          lifetime: lifetime,
          context: context
        )
      case let .setVariable(name, value):
        context.variablesStorage.update(
          path: context.path,
          name: DivVariableName(rawValue: name),
          value: value
        )
      case let .setCurrentItem(id, index):
        scrollActionHandler.scrollToItem(cardId: cardId, id: id, index: index, animated: true)
      case let .setNextItem(id, step, overflow):
        scrollActionHandler.scrollToNextItem(
          cardId: cardId,
          id: id,
          step: step,
          overflow: overflow,
          animated: true
        )
      case let .setPreviousItem(id, step, overflow):
        scrollActionHandler.scrollToNextItem(
          cardId: cardId,
          id: id,
          step: -step,
          overflow: overflow,
          animated: true
        )
      case let .scroll(id, mode):
        switch mode {
        case .start:
          scrollActionHandler.scrollToStart(cardId: cardId, id: id, animated: true)
        case .end:
          scrollActionHandler.scrollToEnd(cardId: cardId, id: id, animated: true)
        case let .forward(offset, overflow):
          scrollActionHandler.scrollToOffset(
            cardId: cardId,
            id: id,
            offset: offset,
            isRelative: true,
            overflow: overflow,
            animated: true
          )
        case let .backward(offset, overflow):
          scrollActionHandler.scrollToOffset(
            cardId: cardId,
            id: id,
            offset: -offset,
            isRelative: true,
            overflow: overflow,
            animated: true
          )
        case let .position(position):
          scrollActionHandler.scrollToOffset(
            cardId: cardId,
            id: id,
            offset: position,
            animated: true
          )
        }
      case let .video(id: id, action: action):
        context.blockStateStorage.setState(
          id: id,
          cardId: cardId,
          state: VideoBlockViewState(state: action == .play ? .playing : .paused)
        )
        context.updateCard(.state(cardId))
      case let .timer(timerId, action):
        timerActionHandler.handle(cardId: cardId, timerId: timerId, action: action)
      case let .setStoredValue(storedValue):
        persistentValuesStorage.set(value: storedValue)
      }
      return
    }

    let info = context.info
    if !flags.useUrlHandlerForVisibilityActions,
       info.source == .visibility || info.source == .disappear {
      return
    }

    urlHandler.handle(url, info: info, sender: sender)
  }

  private func parseAction<T: TemplateValue>(
    type _: T.Type,
    json: JSONObject
  ) -> T.ResolvedValue? {
    try? DivTemplates.empty.parseValue(
      type: T.self,
      from: json.makeDictionary() ?? [:]
    ).unwrap()
  }
}
