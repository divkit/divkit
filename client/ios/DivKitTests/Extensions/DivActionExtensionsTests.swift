@testable import DivKit

import XCTest

import CommonCorePublic
import LayoutKit

final class DivActionExtensionsTests: XCTestCase {
  func test_WhenHasURL_BuildsActionWithIt() {
    let divAction = DivAction(
      logId: logId,
      url: testURL
    )
    let action = divAction.uiAction(context: context)
    let expectedAction = UserInterfaceAction(
      payloads: [divAction.makeDivActionPayload()],
      path: expectedPath
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
    let action = divAction.uiAction(context: context)
    let expectedAction = UserInterfaceAction(
      menu: Menu(items: [
        Menu.Item(
          action: UserInterfaceAction(
            payloads: [menuItemDivAction.makeDivActionPayload()],
            path: expectedPath
          ),
          text: "url"
        ),
      ])!,
      path: expectedPath
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenHasJSON_BuildsActionWithIt() {
    let divAction = DivAction(
      logId: logId,
      payload: testPayload
    )
    let action = divAction.uiAction(context: context)
    let expectedAction = UserInterfaceAction(
      payloads: [
        .json(.object(testPayload.typedJSON())),
        divAction.makeDivActionPayload(),
      ],
      path: expectedPath
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenHasMultiplePayloads_BuildsCompositeAction() {
    let divAction = DivAction(
      logId: logId,
      payload: testPayload,
      url: testURL
    )
    let action = divAction.uiAction(context: context)
    let expectedAction = UserInterfaceAction(
      payloads: [
        .json(.object(testPayload.typedJSON())),
        divAction.makeDivActionPayload(),
      ],
      path: expectedPath
    )
    XCTAssertEqual(action, expectedAction)
  }

  func test_WhenPayloadIsMissing_BuildsDivAction() {
    let divAction = DivAction(logId: logId)
    let action = divAction.uiAction(context: context)
    let expectedAction = UserInterfaceAction(
      payloads: [divAction.makeDivActionPayload()],
      path: expectedPath
    )
    XCTAssertEqual(action, expectedAction)
  }
}

private let context = DivBlockModelingContext.default.actionContext

private let logId = "test/log_id"

private let testURL = Expression.value(URL(string: "https://ya.ru")!)

private let testPayload: [String: Any] = [
  "string_key": "string_value",
  "number_key": 42,
]

private let expectedPath: UIElementPath = .root + logId

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
