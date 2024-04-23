import BasePublic
import LayoutKit
import Serialization

public final class DivActionHandler {
  public typealias TrackVisibility = (_ logId: String, _ cardId: DivCardID) -> Void

  private let divActionURLHandler: DivActionURLHandler
  private let urlHandler: DivUrlHandler
  private let logger: DivActionLogger
  private let trackVisibility: TrackVisibility
  private let trackDisappear: TrackVisibility
  private let variablesStorage: DivVariablesStorage
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let blockStateStorage: DivBlockStateStorage
  private let updateCard: DivActionURLHandler.UpdateCardAction
  private let reporter: DivReporter

  private let setVariableActionHandler: SetVariableActionHandler
  private let arrayInsertValueActionHandler: ArrayInsertValueActionHandler
  private let arrayRemoveValueActionHandler: ArrayRemoveValueActionHandler
  private let copyToClipboardActionHandler: CopyToClipboardActionHandler
  private let focusElementActionHandler: FocusElementActionHandler
  private let clearFocusActionHandler: ClearFocusActionHandler

  init(
    divActionURLHandler: DivActionURLHandler,
    urlHandler: DivUrlHandler,
    logger: DivActionLogger,
    trackVisibility: @escaping TrackVisibility,
    trackDisappear: @escaping TrackVisibility,
    variablesStorage: DivVariablesStorage,
    persistentValuesStorage: DivPersistentValuesStorage,
    blockStateStorage: DivBlockStateStorage,
    updateCard: @escaping DivActionURLHandler.UpdateCardAction,
    reporter: DivReporter
  ) {
    self.divActionURLHandler = divActionURLHandler
    self.urlHandler = urlHandler
    self.logger = logger
    self.trackVisibility = trackVisibility
    self.trackDisappear = trackDisappear
    self.variablesStorage = variablesStorage
    self.persistentValuesStorage = persistentValuesStorage
    self.blockStateStorage = blockStateStorage
    self.updateCard = updateCard
    self.reporter = reporter

    setVariableActionHandler = SetVariableActionHandler()
    arrayInsertValueActionHandler = ArrayInsertValueActionHandler()
    arrayRemoveValueActionHandler = ArrayRemoveValueActionHandler()
    copyToClipboardActionHandler = CopyToClipboardActionHandler()
    focusElementActionHandler = FocusElementActionHandler()
    clearFocusActionHandler = ClearFocusActionHandler()
  }

  public convenience init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    patchProvider: DivPatchProvider,
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    updateCard: @escaping DivActionURLHandler.UpdateCardAction,
    showTooltip: DivActionURLHandler.ShowTooltipAction? = nil,
    tooltipActionPerformer: TooltipActionPerformer? = nil,
    logger: DivActionLogger = EmptyDivActionLogger(),
    trackVisibility: @escaping TrackVisibility = { _, _ in },
    trackDisappear: @escaping TrackVisibility = { _, _ in },
    performTimerAction: @escaping DivActionURLHandler.PerformTimerAction = { _, _, _ in },
    urlHandler: DivUrlHandler,
    persistentValuesStorage: DivPersistentValuesStorage = DivPersistentValuesStorage(),
    reporter: DivReporter? = nil
  ) {
    self.init(
      divActionURLHandler: DivActionURLHandler(
        stateUpdater: stateUpdater,
        blockStateStorage: blockStateStorage,
        patchProvider: patchProvider,
        variableUpdater: variablesStorage,
        updateCard: updateCard,
        showTooltip: showTooltip,
        tooltipActionPerformer: tooltipActionPerformer,
        performTimerAction: performTimerAction,
        persistentValuesStorage: persistentValuesStorage
      ),
      urlHandler: urlHandler,
      logger: logger,
      trackVisibility: trackVisibility,
      trackDisappear: trackDisappear,
      variablesStorage: variablesStorage,
      persistentValuesStorage: persistentValuesStorage,
      blockStateStorage: blockStateStorage,
      updateCard: updateCard,
      reporter: reporter ?? DefaultDivReporter()
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
      cardId: DivCardID(rawValue: params.cardId),
      source: params.source,
      prototypeVariables: params.prototypeVariables,
      sender: sender
    )
  }

  func handle(
    _ action: DivActionBase,
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource,
    prototypeVariables: [String: AnyHashable] = [:],
    sender: AnyObject?
  ) {
    let expressionResolver = makeExpressionResolver(
      cardId: cardId,
      prototypeVariables: prototypeVariables
    )
    let context = DivActionHandlingContext(
      cardId: cardId,
      expressionResolver: expressionResolver,
      variablesStorage: variablesStorage,
      blockStateStorage: blockStateStorage,
      updateCard: updateCard
    )

    var isHandled = true
    switch action.typed {
    case let .divActionSetVariable(action):
      setVariableActionHandler.handle(action, context: context)
    case let .divActionArrayInsertValue(action):
      arrayInsertValueActionHandler.handle(action, context: context)
    case let .divActionArrayRemoveValue(action):
      arrayRemoveValueActionHandler.handle(action, context: context)
    case let .divActionCopyToClipboard(action):
      copyToClipboardActionHandler.handle(action, context: context)
    case let .divActionFocusElement(action):
      focusElementActionHandler.handle(action, context: context)
    case .divActionClearFocus:
      clearFocusActionHandler.handle(context: context)
    case .none:
      isHandled = false
    }

    if !isHandled {
      handleUrl(action, context: context, source: source, sender: sender)
    }

    if let logUrl = action.resolveLogUrl(expressionResolver) {
      let referer = action.resolveReferer(expressionResolver)
      logger.log(url: logUrl, referer: referer, payload: action.payload)
    }

    let logId = action.resolveLogId(expressionResolver) ?? ""

    reporter.reportAction(
      cardId: cardId,
      info: DivActionInfo(
        cardId: cardId,
        logId: logId,
        source: source,
        payload: action.payload
      )
    )

    if source == .visibility {
      trackVisibility(logId, cardId)
    } else if source == .disappear {
      trackDisappear(logId, cardId)
    }
  }

  private func handleUrl(
    _ action: DivActionBase,
    context: DivActionHandlingContext,
    source: UserInterfaceAction.DivActionSource,
    sender: AnyObject?
  ) {
    let expressionResolver = context.expressionResolver
    guard let url = action.resolveUrl(expressionResolver) else {
      return
    }

    let isDivActionURLHandled = divActionURLHandler.handleURL(
      url,
      cardId: context.cardId,
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
            cardId: context.cardId,
            source: source,
            sender: sender
          )
        }
      }
    )

    if !isDivActionURLHandled {
      switch source {
      case .visibility, .disappear:
        // For visibility actions url is treated as logUrl.
        let referer = action.resolveReferer(expressionResolver)
        logger.log(url: url, referer: referer, payload: action.payload)
      default:
        urlHandler.handle(
          url,
          info: DivActionInfo(
            cardId: context.cardId,
            logId: action.resolveLogId(expressionResolver) ?? "",
            source: source,
            payload: action.payload
          ),
          sender: sender
        )
      }
    }
  }

  private func makeExpressionResolver(
    cardId: DivCardID,
    prototypeVariables: [String: AnyHashable]
  ) -> ExpressionResolver {
    ExpressionResolver(
      functionsProvider: FunctionsProvider(
        cardId: cardId,
        variablesStorage: variablesStorage,
        variableTracker: { _ in },
        persistentValuesStorage: persistentValuesStorage,
        prototypesStorage: prototypeVariables
      ),
      errorTracker: reporter.asExpressionErrorTracker(cardId: cardId)
    )
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
