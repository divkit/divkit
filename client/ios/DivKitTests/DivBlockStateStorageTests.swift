@testable import DivKit

import XCTest

import LayoutKit

final class DivBlockStateStorageTests: XCTestCase {
  private var storage: DivBlockStateStorage!

  override func setUp() {
    super.setUp()

    storage = DivBlockStateStorage()
  }

  func test_GetState_ByPath_NotExists() {
    XCTAssertNil(storage.getStateUntyped(divStatePath("0/id")))
  }

  func test_GetState_ById_NotExists() {
    XCTAssertNil(storage.getStateUntyped("id"))
  }

  func test_SetState_WithPath_GetState_ByPath() {
    storage.setState(path: divStatePath("0/id"), state: state1)
    XCTAssertEqual(storage.getState(divStatePath("0/id")), state1)
  }

  func test_SetState_WithPath_GetState_ById() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    XCTAssertEqual(storage.getState("id", cardId: "card_id"), state1)
  }

  func test_SetState_WithIdAndCardId_GetState_ByPath() {
    storage.setState(id: "id", cardId: "card_id", state: state1)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/div_state/state1/id")), state1)
  }

  func test_SetState_WithIdAndCardId_GetState_ByIdAndCardId() {
    storage.setState(id: "id", cardId: "card_id", state: state1)
    XCTAssertEqual(storage.getState("id", cardId: "card_id"), state1)
  }

  func test_SetState_WithPath_OverridesWithId() {
    storage.setState(id: "id", cardId: "card_id", state: state1)
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state2)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/id")), state2)
  }

  func test_SetState_WithId_OverridesWithPath() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setState(id: "id", cardId: "card_id", state: state2)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/id")), state2)
  }

  func test_Reset_ResetsPaths() {
    storage.setState(path: divStatePath("0/id"), state: state1)
    storage.reset()
    XCTAssertNil(storage.getStateUntyped(divStatePath("0/id")))
  }

  func test_Reset_ResetsIds() {
    storage.setState(id: "id", cardId: "card_id", state: state1)
    storage.reset()
    XCTAssertNil(storage.getStateUntyped("id", cardId: "card_id"))
  }
}

private let state1 = State(name: "State 1")
private let state2 = State(name: "State 2")

private struct State: ElementState, Equatable {
  public let name: String
}

private func path(cardId: String, path: String) -> UIElementPath {
  UIElementPath(cardId) + path.split(separator: "/").map(String.init)
}

private func divStatePath(_ path: String) -> UIElementPath {
  DivStatePath.makeDivStatePath(from: path)!.rawValue
}
