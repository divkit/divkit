@testable import DivKit
import DivKitTestsSupport
import XCTest

final class DivActionIntentTests: XCTestCase {
  func test_Download() {
    switch makeIntent("div-action://download?url=https://download.url") {
    case let .download(patchUrl):
      XCTAssertEqual(url("https://download.url"), patchUrl)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_HideTooltip() {
    switch makeIntent("div-action://hide_tooltip?id=123") {
    case let .hideTooltip(id):
      XCTAssertEqual("123", id)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_ShowTooltip() {
    switch makeIntent("div-action://show_tooltip?id=123") {
    case let .showTooltip(id, multiple):
      XCTAssertEqual("123", id)
      XCTAssertFalse(multiple)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_ShowTooltip_InvalidParams() {
    XCTAssertNil(makeIntent("div-action://show_tooltip"))
  }

  func test_ShowTooltip_InvalidScheme() {
    XCTAssertNil(makeIntent("divaction://show_tooltip?id=123"))
  }

  func test_SetState() {
    switch makeIntent("div-action://set_state?state_id=0/state/second") {
    case let .setState(divStatePath, lifetime):
      XCTAssertEqual(DivStatePath.makeDivStatePath(from: "0/state/second"), divStatePath)
      XCTAssertEqual(.short, lifetime)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetState_TemporaryIsTrue() {
    switch makeIntent("div-action://set_state?state_id=0/state/second&temporary=true") {
    case let .setState(divStatePath, lifetime):
      XCTAssertEqual(DivStatePath.makeDivStatePath(from: "0/state/second"), divStatePath)
      XCTAssertEqual(.short, lifetime)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetState_TemporaryIsFalse() {
    switch makeIntent("div-action://set_state?state_id=0/state/second&temporary=false") {
    case let .setState(divStatePath, lifetime):
      XCTAssertEqual(DivStatePath.makeDivStatePath(from: "0/state/second"), divStatePath)
      XCTAssertEqual(.long, lifetime)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetVariable() {
    switch makeIntent("div-action://set_variable?name=var1&value=newvalue") {
    case let .setVariable(name, value):
      XCTAssertEqual("var1", name)
      XCTAssertEqual("newvalue", value)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetVariable_InvalidParams() {
    XCTAssertNil(makeIntent("div-action://set_variable?name=var1"))
  }

  func test_SetCurrentItem() {
    switch makeIntent("div-action://set_current_item?id=div_id&item=10") {
    case let .setCurrentItem(id, index):
      XCTAssertEqual("div_id", id)
      XCTAssertEqual(10, index)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetCurrentItem_InvalidItem() {
    XCTAssertNil(makeIntent("set_current_item?id=0/div_id&item=abc"))
  }

  func test_SetNextItem() {
    switch makeIntent("div-action://set_next_item?id=div_id&step=3&overflow=ring") {
    case let .setNextItem(id, step, overflow):
      XCTAssertEqual("div_id", id)
      XCTAssertEqual(3, step)
      XCTAssertEqual(.ring, overflow)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetPreviousItem() {
    switch makeIntent("div-action://set_previous_item?id=div_id&step=3&overflow=clamp") {
    case let .setPreviousItem(id, step, overflow):
      XCTAssertEqual("div_id", id)
      XCTAssertEqual(3, step)
      XCTAssertEqual(.clamp, overflow)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetStoredValue_String() {
    switch makeIntent(
      "div-action://set_stored_value?name=var&value=value&type=string&lifetime=100"
    ) {
    case let .setStoredValue(value):
      XCTAssertEqual("var", value.name)
      XCTAssertEqual("value", value.value)
      XCTAssertEqual(.string, value.type)
      XCTAssertEqual(100, value.lifetimeInSec)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetStoredValue_Boolean() {
    switch makeIntent(
      "div-action://set_stored_value?name=var&value=true&type=boolean&lifetime=100"
    ) {
    case let .setStoredValue(value):
      XCTAssertEqual("var", value.name)
      XCTAssertEqual("true", value.value)
      XCTAssertEqual(.boolean, value.type)
      XCTAssertEqual(100, value.lifetimeInSec)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetStoredValue_Bool() {
    switch makeIntent(
      "div-action://set_stored_value?name=var&value=true&type=bool&lifetime=100"
    ) {
    case let .setStoredValue(value):
      XCTAssertEqual("var", value.name)
      XCTAssertEqual("true", value.value)
      XCTAssertEqual(.bool, value.type)
      XCTAssertEqual(100, value.lifetimeInSec)
    default:
      XCTFail("Invalid intent")
    }
  }

  func test_SetStoredValue_WithoutValueReturnsNil() {
    XCTAssertNil(
      makeIntent("div-action://set_stored_value?name=var&type=string&lifetime=100")
    )
  }

  func test_SetStoredValue_WithoutTypeReturnsNil() {
    XCTAssertNil(
      makeIntent("div-action://set_stored_value?name=var&value=value&lifetime=100")
    )
  }

  func test_SetStoredValue_WithoutLifetimeReturnsNil() {
    XCTAssertNil(
      makeIntent("div-action://set_stored_value?name=var&value=value&type=string")
    )
  }
}

private func makeIntent(_ url: String) -> DivActionIntent? {
  DivActionIntent(url: URL(string: url)!)
}
