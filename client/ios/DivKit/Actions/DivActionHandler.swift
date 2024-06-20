import Foundation

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

  private let arrayActionsHandler = ArrayActionsHandler()
  private let dictSetValueActionHandler = DictSetValueActionHandler()
  private let clearFocusActionHandler = ClearFocusActionHandler()
  private let copyToClipboardActionHandler = CopyToClipboardActionHandler()
  private let focusElementActionHandler = FocusElementActionHandler()
  private let setVariableActionHandler = SetVariableActionHandler()

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
      localValues: params.localValues,
      sender: sender
    )
  }

  /// Deprecated. This method is intended for backward compatibility only. Do not use it.
  public func handleDivActionUrl(_ url: URL, cardId: DivCardID) -> Bool {
    divActionURLHandler.handleURL(url, cardId: cardId)
  }

  func handle(
    _ action: DivActionBase,
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource,
    localValues: [String: AnyHashable] = [:],
    sender: AnyObject?
  ) {
    let expressionResolver = makeExpressionResolver(
      cardId: cardId,
      localValues: localValues
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
    case .none:
      isHandled = false
    }

    let logId = action.resolveLogId(expressionResolver) ?? ""
    let logUrl = action.resolveLogUrl(expressionResolver)
    let referer = action.resolveReferer(expressionResolver)

    let divActionInfo = DivActionInfo(
      cardId: cardId,
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
      cardId: info.cardId,
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
            cardId: info.cardId,
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

  private func makeExpressionResolver(
    cardId: DivCardID,
    localValues: [String: AnyHashable]
  ) -> ExpressionResolver {
    ExpressionResolver(
      functionsProvider: FunctionsProvider(
        persistentValuesStorage: persistentValuesStorage
      ),
      variableValueProvider: { [unowned variablesStorage] in
        if let value = localValues[$0] {
          return value
        }
        let variableName = DivVariableName(rawValue: $0)
        return variablesStorage.getVariableValue(cardId: cardId, name: variableName)
      },
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
