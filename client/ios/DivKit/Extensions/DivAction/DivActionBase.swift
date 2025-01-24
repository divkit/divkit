import Foundation
import LayoutKit
import Serialization
import VGSL

public protocol DivActionBase: Serializable {
  var downloadCallbacks: DivDownloadCallbacks? { get }
  var payload: [String: Any]? { get }
  var typed: DivActionTyped? { get }
  var url: Expression<URL>? { get }
  var scopeId: String? { get }

  func resolveLogId(_ resolver: ExpressionResolver) -> String?
  func resolveLogUrl(_ resolver: ExpressionResolver) -> URL?
  func resolveReferer(_ resolver: ExpressionResolver) -> URL?
  func resolveUrl(_ resolver: ExpressionResolver) -> URL?
}

extension DivActionBase {
  func resolveInfo(
    _ expressionResolver: ExpressionResolver,
    path: UIElementPath,
    source: UserInterfaceAction.DivActionSource
  ) -> DivActionInfo {
    DivActionInfo(
      path: path,
      logId: resolveLogId(expressionResolver) ?? "",
      url: resolveUrl(expressionResolver),
      logUrl: resolveLogUrl(expressionResolver),
      referer: resolveReferer(expressionResolver),
      source: source,
      payload: payload
    )
  }

  func makeDivActionPayload(
    path: UIElementPath,
    source: UserInterfaceAction.DivActionSource,
    localValues: [String: AnyHashable] = [:]
  ) -> UserInterfaceAction.Payload {
    // url parameter is used for backward compatibility, it should be removed
    // when all custom div-action handlers will be replaced
    let url: URL? = switch self.url {
    case let .value(value):
      value.adding(cardId: path.root)
    case .link, .none:
      nil
    }

    return .divAction(
      params: UserInterfaceAction.DivActionParams(
        action: .object(toDictionary().typedJSON()),
        path: path,
        source: source,
        url: url,
        localValues: localValues
      )
    )
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
