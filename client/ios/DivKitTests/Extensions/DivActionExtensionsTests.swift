@testable import DivKit

import XCTest

import CommonCorePublic
import LayoutKit

final class DivActionExtensionsTests: XCTestCase {
  func test_AddsCardLogIdToActionPath() {
    let divAction = DivAction(
      logId: logId,
      url: testURL
    )
    let action = divAction.uiAction(
      context: .default.modifying(cardLogId: "card_log_id")
    )
    XCTAssertEqual(action.path, UIElementPath("card_log_id") + logId)
  }
  
  func test_WhenHasURL_BuildsActionWithIt() {
    let divAction = DivAction(
      logId: logId,
      url: testURL
    )
    let action = divAction.uiAction(context: .default)
    let expectedAction = UserInterfaceAction(
      payload: divAction.makeDivActionPayload(),
      path: .root + logId
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenHasMenu_BuildsActionWithIt() {
    let menuItemDivAction = DivAction(logId: logId, url: testURL)
    let divAction = DivAction(
      logId: logId,
      menuItems: [
        DivAction.MenuItem(action: menuItemDivAction, text: .value("url")),
      ]
    )
    let action = divAction.uiAction(context: .default)
    let expectedAction = UserInterfaceAction(
      menu: Menu(items: [
        Menu.Item(
          action: UserInterfaceAction(
            payloads: [menuItemDivAction.makeDivActionPayload()],
            path: .root + logId
          ),
          text: "url"
        ),
      ])!,
      path: .root + logId
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenHasPayload_BuildsActionWithIt() {
    let divAction = DivAction(
      logId: logId,
      payload: testPayload
    )
    let action = divAction.uiAction(context: .default)
    let expectedAction = UserInterfaceAction(
      payload: divAction.makeDivActionPayload(),
      path: .root + logId
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenHasMultiplePayloadAndUrl_BuildsActionWithIt() {
    let divAction = DivAction(
      logId: logId,
      payload: testPayload,
      url: testURL
    )
    let action = divAction.uiAction(context: .default)
    let expectedAction = UserInterfaceAction(
      payload: divAction.makeDivActionPayload(),
      path: .root + logId
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenPayloadIsMissing_BuildsDivAction() {
    let divAction = DivAction(logId: logId)
    let action = divAction.uiAction(context: .default)
    let expectedAction = UserInterfaceAction(
      payload: divAction.makeDivActionPayload(),
      path: .root + logId
    )
    XCTAssertEqual(action, expectedAction)
  }
}

private let logId = "test/log_id"

private let testURL = Expression.value(URL(string: "https://ya.ru")!)

private let testPayload: [String: Any] = [
  "string_key": "string_value",
  "number_key": 42,
]

extension DivAction {
  fileprivate func makeDivActionPayload() -> UserInterfaceAction.Payload {
    .divAction(
      params: UserInterfaceAction.DivActionParams(
        action: .object(toDictionary().typedJSON()),
        cardId: DivKitTests.cardId.rawValue,
        source: .tap,
        url: url?.rawValue
      )
    )
  }
}
