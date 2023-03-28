import Foundation

import CommonCorePublic
import LayoutKit
import Serialization

public protocol DivActionBase: Serializable {
  var downloadCallbacks: DivDownloadCallbacks? { get }
  var logId: String { get }
  var logUrl: Expression<URL>? { get }
  var payload: [String: Any]? { get }
  var referer: Expression<URL>? { get }
  var url: Expression<URL>? { get }

  func resolveLogUrl(_ resolver: ExpressionResolver) -> URL?
  func resolveReferer(_ resolver: ExpressionResolver) -> URL?
  func resolveUrl(_ resolver: ExpressionResolver) -> URL?
}

extension DivActionBase {
  func makeDivActionParams(
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource
  ) -> UserInterfaceAction.DivActionParams {
    let actionJson = JSONObject.object(toDictionary().typedJSON())
    // url parameter is used for backward compatibility, it should be removed
    // when all custom div-action handlers will be replaced
    let url = url?.rawValue.map { $0.adding(cardId: cardId.rawValue) }
    return UserInterfaceAction.DivActionParams(
      action: actionJson,
      cardId: cardId.rawValue,
      source: source,
      url: url
    )
  }

  func makeDivActionPayload(
    cardId: DivCardID,
    source: UserInterfaceAction.DivActionSource
  ) -> UserInterfaceAction.Payload? {
    .divAction(params: makeDivActionParams(cardId: cardId, source: source))
  }

  func makeJsonPayload() -> UserInterfaceAction.Payload? {
    guard let payload = payload else {
      return nil
    }

    let jsonDict = payload.typedJSON()
    return .json(.object(jsonDict))
  }
}

extension URL {
  fileprivate func adding(cardId: String?) -> URL {
    guard scheme == "div-action", host == "set_state" else {
      return self
    }
    return URLByAddingGETParameters(
      ["card_id": cardId].compactMapValues { $0 }
    )
  }
}
