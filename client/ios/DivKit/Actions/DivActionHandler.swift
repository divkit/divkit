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

  typealias UpdateCardAction = (DivActionURLHandler.UpdateReason) -> Void

  private let divActionURLHandler: DivActionURLHandler
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
    updateCard: @escaping (DivActionURLHandler.UpdateReason) -> Void,
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
    let scrollActionHandler = ScrollActionHandler(
      blockStateStorage: blockStateStorage,
      updateCard: updateCard
    )
    self.scrollActionHandler = scrollActionHandler

    let setStateActionHandler = SetStateActionHandler(stateUpdater: stateUpdater)
    self.setStateActionHandler = setStateActionHandler

    let timerActionHandler = TimerActionHandler(performer: performTimerAction)
    self.timerActionHandler = timerActionHandler

    let tooltipActionHandler = TooltipActionHandler(
      performer: tooltipActionPerformer,
      showTooltip: showTooltip
    )
    self.tooltipActionHandler = tooltipActionHandler

    self.divActionURLHandler = DivActionURLHandler(
      patchProvider: patchProvider,
      updateCard: updateCard,
      persistentValuesStorage: persistentValuesStorage,
      scrollActionHandler: scrollActionHandler,
      setStateActionHandler: setStateActionHandler,
      timerActionHandler: timerActionHandler,
      tooltipActionHandler: tooltipActionHandler
    )
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
    setStoredValueActionHandler = SetStoredValueActionHandler(
      persistentValuesStorage: persistentValuesStorage
    )
    submitActionHandler = SubmitActionHandler(submitter: submitter)
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
    let path = if let scopeId = action.scopeId {
      idToPath[path.cardId.path + scopeId] ?? path
    } else {
      path
    }
    let context = DivActionHandlingContext(
      path: path,
      expressionResolver: expressionResolver,
      variablesStorage: variablesStorage,
      blockStateStorage: blockStateStorage,
      actionHandler: self,
      updateCard: updateCard
    )

    var isHandled = true
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
    case let .divActionVideo(action):
      videoActionHandler.handle(action, context: context)
    case .divActionDownload:
      break
    case .none:
      isHandled = false
    }

    let logId = action.resolveLogId(expressionResolver) ?? ""
    let logUrl = action.resolveLogUrl(expressionResolver)
    let referer = action.resolveReferer(expressionResolver)

    let divActionInfo = DivActionInfo(
      path: path,
      logId: logId,
      url: action.resolveUrl(expressionResolver),
      logUrl: logUrl,
      referer: referer,
      source: source,
      payload: action.payload
    )

    if !isHandled {
      handleUrl(
        action,
        info: divActionInfo,
        context: context,
        sender: sender
      )
    }

    reporter.reportAction(
      cardId: cardId,
      info: divActionInfo
    )

    if source == .visibility {
      trackVisibility(logId, cardId)
    } else if source == .disappear {
      trackDisappear(logId, cardId)
    }
  }

  private func handleUrl(
    _ action: DivActionBase,
    info: DivActionInfo,
    context: DivActionHandlingContext,
    sender: AnyObject?
  ) {
    guard let url = info.url else {
      return
    }

    let isDivActionURLHandled = divActionURLHandler.handleURL(
      url,
      info: info,
      context: context,
      completion: { [weak self] result in
        guard let self else {
          return
        }
        let callbackActions: [DivAction] = switch result {
        case .success:
          action.downloadCallbacks?.onSuccessActions ?? []
        case .failure:
          action.downloadCallbacks?.onFailActions ?? []
        }
        for action in callbackActions {
          self.handle(
            action,
            path: info.path,
            source: info.source,
            sender: sender
          )
        }
      }
    )

    if isDivActionURLHandled {
      return
    }

    if !flags.useUrlHandlerForVisibilityActions,
       (info.source == .visibility || info.source == .disappear) {
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
