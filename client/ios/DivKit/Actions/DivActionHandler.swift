import Foundation

import LayoutKit
import Serialization
import VGSL

public final class DivActionHandler {
  public typealias TrackVisibility = (_ logId: String, _ cardId: DivCardID) -> Void

  private let divActionURLHandler: DivActionURLHandler
  private let urlHandler: DivUrlHandler
  private let logger: DivActionLogger
  private let trackVisibility: TrackVisibility
  private let trackDisappear: TrackVisibility
  private let variablesStorage: DivVariablesStorage
  private let functionsStorage: DivFunctionsStorage?
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let blockStateStorage: DivBlockStateStorage
  private let updateCard: DivActionURLHandler.UpdateCardAction
  private let reporter: DivReporter
  private let idToPath: IdToPath

  private let arrayActionsHandler = ArrayActionsHandler()
  private let dictSetValueActionHandler = DictSetValueActionHandler()
  private let clearFocusActionHandler = ClearFocusActionHandler()
  private let copyToClipboardActionHandler = CopyToClipboardActionHandler()
  private let focusElementActionHandler = FocusElementActionHandler()
  private let setVariableActionHandler = SetVariableActionHandler()
  private let submitActionHandler: SubmitActionHandler
  private let timerActionHandler: TimerActionHandler
  private let videoActionHandler = VideoActionHandler()
  private let animatorHandler: AnimatorActionHandler

  /// Deprecated. Do not create `DivActionHandler`. Use the instance from `DivKitComponents`.
  public convenience init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    patchProvider: DivPatchProvider,
    submitter: DivSubmitter,
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    functionsStorage: DivFunctionsStorage? = nil,
    updateCard: @escaping DivActionURLHandler.UpdateCardAction,
    showTooltip: DivActionURLHandler.ShowTooltipAction? = nil,
    tooltipActionPerformer: TooltipActionPerformer? = nil,
    logger: DivActionLogger? = nil,
    trackVisibility: @escaping TrackVisibility = { _, _ in },
    trackDisappear: @escaping TrackVisibility = { _, _ in },
    performTimerAction: @escaping DivActionURLHandler.PerformTimerAction = { _, _, _ in },
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
      logger: logger ?? EmptyDivActionLogger(),
      trackVisibility: trackVisibility,
      trackDisappear: trackDisappear,
      performTimerAction: performTimerAction,
      urlHandler: urlHandler,
      persistentValuesStorage: persistentValuesStorage,
      reporter: reporter ?? DefaultDivReporter(),
      idToPath: IdToPath(),
      animatorController: DivAnimatorController()
    )
  }

  init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage,
    patchProvider: DivPatchProvider,
    submitter: DivSubmitter,
    variablesStorage: DivVariablesStorage,
    functionsStorage: DivFunctionsStorage?,
    updateCard: @escaping DivActionURLHandler.UpdateCardAction,
    showTooltip: DivActionURLHandler.ShowTooltipAction?,
    tooltipActionPerformer: TooltipActionPerformer?,
    logger: DivActionLogger,
    trackVisibility: @escaping TrackVisibility,
    trackDisappear: @escaping TrackVisibility,
    performTimerAction: @escaping DivActionURLHandler.PerformTimerAction,
    urlHandler: DivUrlHandler,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter,
    idToPath: IdToPath,
    animatorController: DivAnimatorController
  ) {
    self.divActionURLHandler = DivActionURLHandler(
      stateUpdater: stateUpdater,
      blockStateStorage: blockStateStorage,
      patchProvider: patchProvider,
      variableUpdater: variablesStorage,
      updateCard: updateCard,
      showTooltip: showTooltip,
      tooltipActionPerformer: tooltipActionPerformer,
      performTimerAction: performTimerAction,
      persistentValuesStorage: persistentValuesStorage
    )
    self.urlHandler = urlHandler
    self.logger = logger
    self.submitActionHandler = SubmitActionHandler(submitter: submitter)
    self.trackVisibility = trackVisibility
    self.trackDisappear = trackDisappear
    self.variablesStorage = variablesStorage
    self.functionsStorage = functionsStorage
    self.persistentValuesStorage = persistentValuesStorage
    self.blockStateStorage = blockStateStorage
    self.updateCard = updateCard
    self.reporter = reporter
    self.timerActionHandler = TimerActionHandler(performer: performTimerAction)
    self.idToPath = idToPath
    self.animatorHandler = AnimatorActionHandler(animatorController: animatorController)
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

  /// Deprecated. This method is intended for backward compatibility only. Do not use it.
  public func handleDivActionUrl(_ url: URL, cardId: DivCardID) -> Bool {
    divActionURLHandler.handleURL(url, path: cardId.path)
  }

  func handle(
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
    case let .divActionArrayInsertValue(action):
      arrayActionsHandler.handle(action, context: context)
    case let .divActionArrayRemoveValue(action):
      arrayActionsHandler.handle(action, context: context)
    case let .divActionArraySetValue(action):
      arrayActionsHandler.handle(action, context: context)
    case let .divActionDictSetValue(action):
      dictSetValueActionHandler.handle(action, context: context)
    case .divActionClearFocus:
      clearFocusActionHandler.handle(context: context)
    case let .divActionCopyToClipboard(action):
      copyToClipboardActionHandler.handle(action, context: context)
    case let .divActionFocusElement(action):
      focusElementActionHandler.handle(action, context: context)
    case let .divActionSetVariable(action):
      setVariableActionHandler.handle(action, context: context)
    case let .divActionTimer(action):
      timerActionHandler.handle(action, context: context)
    case let .divActionVideo(action):
      videoActionHandler.handle(action, context: context)
    case let .divActionSubmit(action):
      submitActionHandler.handle(action, context: context)
    case let .divActionAnimatorStart(action):
      animatorHandler.handle(action, context: context)
    case let .divActionAnimatorStop(action):
      animatorHandler.handle(action, context: context)
    case .divActionShowTooltip, .divActionHideTooltip, .divActionDownload,
         .divActionSetState, .divActionSetStoredValue, .divActionScrollBy, .divActionScrollTo:
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
      handleUrl(action, info: divActionInfo, sender: sender)
    }

    if let logUrl {
      logger.log(url: logUrl, referer: referer, payload: action.payload)
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
    sender: AnyObject?
  ) {
    guard let url = info.url else {
      return
    }

    let isDivActionURLHandled = divActionURLHandler.handleURL(
      url,
      path: info.path,
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

    if !isDivActionURLHandled {
      switch info.source {
      case .visibility, .disappear:
        // For visibility actions url is treated as logUrl.
        let referer = info.referer
        logger.log(url: url, referer: referer, payload: action.payload)
      default:
        urlHandler.handle(
          url,
          info: info,
          sender: sender
        )
      }
    }
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
