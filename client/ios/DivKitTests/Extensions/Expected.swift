import Foundation

import CommonCorePublic
import LayoutKit

enum Expected {
  static let action = makeUiAction(url: "https://ya.ru", logId: "test_log_id")

  static let setStateAction = UserInterfaceAction(
    payload: makeDivActionPayload(
      action: makeDivActionJson(
        url: "div-action://set_state?state_id=42",
        logId: "test_log_id"
      ),
      url: "div-action://set_state?card_id=test_card_id&state_id=42"
    ),
    path: .root + "test_log_id"
  )

  static let actions = NonEmptyArray(action)
  static let setStateActions = NonEmptyArray(setStateAction)

  static let multiActions = NonEmptyArray(
    makeUiAction(url: "https://lenta.ru", logId: "test_log_id_1"),
    makeUiAction(url: "https://dtf.ru", logId: "test_log_id_2")
  )

  static let menuAction = NonEmptyArray(
    UserInterfaceAction(
      menu: Menu(items: [
        Menu.Item(action: action, text: "Have questions?"),
        Menu.Item(action: action, text: "Definitely!"),
      ])!,
      path: .root + "test_log_id_2"
    )
  )
}

extension UIElementPath {
  static let root = UIElementPath(DivKitTests.cardId.rawValue)
}

private func makeUiAction(url: String, logId: String) -> UserInterfaceAction {
  UserInterfaceAction(
    payload: makeDivActionPayload(
      action: makeDivActionJson(url: url, logId: logId),
      url: url
    ),
    path: .root + logId
  )
}

private func makeDivActionPayload(
  action: JSONObject,
  url: String
) -> UserInterfaceAction.Payload {
  .divAction(
    params: UserInterfaceAction.DivActionParams(
      action: action,
      cardId: DivKitTests.cardId.rawValue,
      source: .tap,
      url: URL(string: url)!
    )
  )
}

private func makeDivActionJson(url: String, logId: String) -> JSONObject {
  .object(["log_id": .string(logId), "url": .string(url), "is_enabled": .bool(true)])
}
