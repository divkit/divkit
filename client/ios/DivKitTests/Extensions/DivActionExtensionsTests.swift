@testable import DivKit

import XCTest

import CommonCorePublic
import LayoutKit

final class DivActionExtensionsTests: XCTestCase {
  func test_uiAction_AddsCardLogIdFromContext() {
    let action = DivAction(
      logId: "action_log_id"
    ).uiAction(context: .default.modifying(cardLogId: "custom_card_log_id"))

    XCTAssertEqual(action?.path, UIElementPath("custom_card_log_id") + "action_log_id")
  }

  func test_uiAction_ReturnsNull_IfActionIsDisabled() {
    let action = DivAction(
      isEnabled: .value(false),
      logId: "action_log_id",
      url: .value(url("https://some.url"))
    ).uiAction

    XCTAssertNil(action)
  }

  func test_WithLogIdOnly() {
    let action = DivAction(logId: "action_log_id").uiAction

    let expectedAction = UserInterfaceAction(
      payload: divActionPayload([
        "log_id": .string("action_log_id"),
        "is_enabled": .bool(true),
      ]),
      path: .root + "action_log_id"
    )

    assertEqual(action, expectedAction)
  }

  func test_WithUrl() {
    let action = DivAction(
      logId: "action_log_id",
      url: .value(url("https://some.url"))
    ).uiAction

    let expectedAction = UserInterfaceAction(
      payload: divActionPayload(
        [
          "log_id": .string("action_log_id"),
          "is_enabled": .bool(true),
          "url": .string("https://some.url"),
        ],
        url: "https://some.url"
      ),
      path: .root + "action_log_id"
    )

    assertEqual(action, expectedAction)
  }

  func test_WithTyped() {
    let action = DivAction(
      logId: "action_log_id",
      typed: .divActionSetVariable(DivActionSetVariable(
        value: .integerValue(IntegerValue(value: .value(10))),
        variableName: .value("var1")
      ))
    ).uiAction

    let expectedAction = UserInterfaceAction(
      payload: divActionPayload([
        "log_id": .string("action_log_id"),
        "is_enabled": .bool(true),
        "typed": .object([
          "type": .string("set_variable"),
          "variable_name": .string("var1"),
          "value": .object([
            "type": .string("integer"),
            "value": .number(10),
          ]),
        ]),
      ]),
      path: .root + "action_log_id"
    )

    assertEqual(action, expectedAction)
  }

  func test_WithMenu() {
    let action = DivAction(
      logId: "action_log_id",
      menuItems: [
        DivAction.MenuItem(
          action: DivAction(
            logId: "menu_item_log_id",
            url: .value(url("https://some.url"))
          ),
          text: .value("Menu Item")
        ),
      ]
    ).uiAction

    let expectedAction = UserInterfaceAction(
      menu: Menu(items: [
        Menu.Item(
          action: uiAction(
            logId: "menu_item_log_id",
            url: "https://some.url"
          ),
          text: "Menu Item"
        ),
      ])!,
      path: .root + "action_log_id"
    )

    assertEqual(action, expectedAction)
  }

  func test_WithPayload() {
    let action = DivAction(
      logId: "action_log_id",
      payload: [
        "string_key": "string_value",
        "number_key": 42,
      ]
    ).uiAction

    let expectedAction = UserInterfaceAction(
      payload: divActionPayload([
        "log_id": .string("action_log_id"),
        "is_enabled": .bool(true),
        "payload": .object([
          "string_key": .string("string_value"),
          "number_key": .number(42),
        ]),
      ]),
      path: .root + "action_log_id"
    )

    assertEqual(action, expectedAction)
  }

  func test_WithPayloadAndUrl() {
    let action = DivAction(
      logId: "action_log_id",
      payload: [
        "string_key": "string_value",
        "number_key": 42,
      ],
      url: .value(url("https://some.url"))
    ).uiAction

    let expectedAction = UserInterfaceAction(
      payload: divActionPayload(
        [
          "log_id": .string("action_log_id"),
          "is_enabled": .bool(true),
          "payload": .object([
            "string_key": .string("string_value"),
            "number_key": .number(42),
          ]),
          "url": .string("https://some.url"),
        ],
        url: "https://some.url"
      ),
      path: .root + "action_log_id"
    )

    assertEqual(action, expectedAction)
  }
}
