import Foundation

import BasePublic
import LayoutKit
import Serialization

public final class DivActionHandler {
  public typealias TrackVisibility = (_ logId: String, _ cardId: DivCardID) -> Void

  private let divActionURLHandler: DivActionURLHandler
  private let logger: DivActionLogger
  private let trackVisibility: TrackVisibility
  private let variablesStorage: DivVariablesStorage

  public init(
    divActionURLHandler: DivActionURLHandler,
    logger: DivActionLogger = EmptyDivActionLogger(),
    trackVisibility: @escaping TrackVisibility = { _, _ in },
    variablesStorage: DivVariablesStorage
  ) {
    self.divActionURLHandler = divActionURLHandler
    self.logger = logger
    self.trackVisibility = trackVisibility
    self.variablesStorage = variablesStorage
  }

  public convenience init(
    stateUpdater: DivStateUpdater,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    patchProvider: DivPatchProvider,
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    updateCard: @escaping DivActionURLHandler.UpdateCardAction,
    showTooltip: @escaping DivActionURLHandler.ShowTooltipAction,
    logger: DivActionLogger = EmptyDivActionLogger(),
    trackVisibility: @escaping TrackVisibility = { _, _ in },
    performTimerAction: @escaping DivActionURLHandler.PerformTimerAction = { _, _, _ in }
  ) {
    self.init(
      divActionURLHandler: DivActionURLHandler(
        stateUpdater: stateUpdater,
        blockStateStorage: blockStateStorage,
        patchProvider: patchProvider,
        variableUpdater: variablesStorage,
        updateCard: updateCard,
        showTooltip: showTooltip,
        performTimerAction: performTimerAction
      ),
      logger: logger,
      trackVisibility: trackVisibility,
      variablesStorage: variablesStorage
    )
  }

  public func handle(
    params: UserInterfaceAction.DivActionParams,
    urlOpener: @escaping UrlOpener
  ) {
    let action: DivActionBase?
    switch params.source {
    case .tap, .custom:
      action = parseAction(type: DivActionTemplate.self, json: params.action)
    case .visibility:
      action = parseAction(type: DivVisibilityActionTemplate.self, json: params.action)
    }
    guard let action = action else {
      return
    }

    handle(
      action,
      cardId: DivCardID(rawValue: params.cardId),
      source: params.source,
      urlOpener: urlOpener
    )
  }

  public func handle(
    _ action: DivActionBase,
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource,
    urlOpener: @escaping UrlOpener
  ) {
    let variables = variablesStorage.makeVariables(for: cardId)
    let expressionResolver = ExpressionResolver(variables: variables)
    if let url = action.resolveUrl(expressionResolver) {
      let isDivActionURLHandled = divActionURLHandler.handleURL(
        url,
        cardId: cardId,
        completion: { [unowned self] result in
          self.handleResult(
            result: result,
            callbacks: action.downloadCallbacks,
            cardId: cardId,
            source: source,
            urlOpener: urlOpener
          )
        }
      )
      if !isDivActionURLHandled {
        switch source {
        case .tap, .custom:
          urlOpener(url)
        case .visibility:
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

  private func handleResult(
    result: Result<Void, Error>,
    callbacks: DivDownloadCallbacks?,
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource,
    urlOpener: @escaping UrlOpener
  ) {
    guard let callbacks = callbacks else {
      return
    }
    switch result {
    case .success:
      callbacks.onSuccessActions?.forEach {
        handle($0, cardId: cardId, source: source, urlOpener: urlOpener)
      }
    case .failure:
      callbacks.onFailActions?.forEach {
        handle($0, cardId: cardId, source: source, urlOpener: urlOpener)
      }
    }
  }
}
