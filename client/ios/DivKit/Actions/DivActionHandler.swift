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
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let blockStateStorage: DivBlockStateStorage
  private let updateCard: DivActionURLHandler.UpdateCardAction
  private let reporter: DivReporter

  private let arrayActionsHandler = ArrayActionsHandler()
  private let dictSetValueActionHandler = DictSetValueActionHandler()
  private let clearFocusActionHandler = ClearFocusActionHandler()
  private let copyToClipboardActionHandler = CopyToClipboardActionHandler()
  private let focusElementActionHandler = FocusElementActionHandler()
  private let setVariableActionHandler = SetVariableActionHandler()
  private let timerActionHandler: TimerActionHandler

  /// Deprecated. Do not create `DivActionHandler`. Use the instance from `DivKitComponents`.
  public init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    patchProvider: DivPatchProvider,
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
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
    self.logger = logger ?? EmptyDivActionLogger()
    self.trackVisibility = trackVisibility
    self.trackDisappear = trackDisappear
    self.variablesStorage = variablesStorage
    self.persistentValuesStorage = persistentValuesStorage
    self.blockStateStorage = blockStateStorage
    self.updateCard = updateCard
    self.reporter = reporter ?? DefaultDivReporter()
    self.timerActionHandler = TimerActionHandler(performer: performTimerAction)
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
      variableValueProvider: { [unowned variablesStorage] in
        if let value = localValues[$0] {
          return value
        }
        let variableName = DivVariableName(rawValue: $0)
        return variablesStorage.getVariableValue(path: path, name: variableName)
      },
      errorTracker: reporter.asExpressionErrorTracker(cardId: cardId)
    )
    let context = DivActionHandlingContext(
      path: path,
      expressionResolver: expressionResolver,
      variablesStorage: variablesStorage,
      blockStateStorage: blockStateStorage,
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
    case .divActionAnimatorStart, .divActionAnimatorStop, .divActionVideo,
        .divActionShowTooltip, .divActionSetState, .divActionHideTooltip:
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
