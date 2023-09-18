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
  private let reporter: DivReporter

  init(
    divActionURLHandler: DivActionURLHandler,
    urlHandler: DivUrlHandler,
    logger: DivActionLogger,
    trackVisibility: @escaping TrackVisibility,
    trackDisappear: @escaping TrackVisibility,
    variablesStorage: DivVariablesStorage,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter
  ) {
    self.divActionURLHandler = divActionURLHandler
    self.urlHandler = urlHandler
    self.logger = logger
    self.trackVisibility = trackVisibility
    self.trackDisappear = trackDisappear
    self.variablesStorage = variablesStorage
    self.persistentValuesStorage = persistentValuesStorage
    self.reporter = reporter
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
      reporter: reporter ?? DefaultDivReporter()
    )
  }

  public func handle(
    params: UserInterfaceAction.DivActionParams,
    sender: AnyObject?
  ) {
    let action: DivActionBase?
    switch params.source {
    case .tap, .custom:
      action = parseAction(type: DivActionTemplate.self, json: params.action)
    case .visibility:
      action = parseAction(type: DivVisibilityActionTemplate.self, json: params.action)
    case .disappear:
      action = parseAction(type: DivDisappearActionTemplate.self, json: params.action)
    }
    guard let action = action else {
      return
    }

    handle(
      action,
      cardId: DivCardID(rawValue: params.cardId),
      source: params.source,
      sender: sender
    )
  }

  func handle(
    _ action: DivActionBase,
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource,
    sender: AnyObject?
  ) {
    let variables = variablesStorage.makeVariables(for: cardId)
    let expressionResolver = ExpressionResolver(
      variables: variables,
      persistentValuesStorage: persistentValuesStorage,
      errorTracker: reporter.asExpressionErrorTracker(cardId: cardId)
    )
    if let url = action.resolveUrl(expressionResolver) {
      let isDivActionURLHandled = divActionURLHandler.handleURL(
        url,
        cardId: cardId,
        completion: { [unowned self] result in
          let callbackActions: [DivAction]
          switch result {
          case .success:
            callbackActions = action.downloadCallbacks?.onSuccessActions ?? []
          case .failure:
            callbackActions = action.downloadCallbacks?.onFailActions ?? []
          }
          callbackActions.forEach {
            self.handle($0, cardId: cardId, source: source, sender: sender)
          }
        }
      )
      if !isDivActionURLHandled {
        switch source {
        case .tap, .custom:
          urlHandler.handle(url, sender: sender)
        case .visibility, .disappear:
          // For visibility actions url is treated as logUrl.
          let referer = action.resolveReferer(expressionResolver)
          logger.log(url: url, referer: referer, payload: action.payload)
        }
      }
    }

    if let logUrl = action.resolveLogUrl(expressionResolver) {
      let referer = action.resolveReferer(expressionResolver)
      logger.log(url: logUrl, referer: referer, payload: action.payload)
    }

    if source == .visibility {
      trackVisibility(action.logId, cardId)
    } else if source == .disappear {
      trackDisappear(action.logId, cardId)
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
