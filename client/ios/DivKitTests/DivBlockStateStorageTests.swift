@testable import DivKit
import LayoutKit
import VGSL
import XCTest

final class DivBlockStateStorageTests: XCTestCase {
  private var storage: DivBlockStateStorage!
  private let disposePool = AutodisposePool()

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

  func test_Reset_ResetsFocusedElement() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id", cardId: "card_id"))
    storage.reset()
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_Reset_ResettingByCardId() {
    storage.setState(id: "id", cardId: "card_id", state: state1)
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state2)
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id", cardId: "card_id"))
    storage.reset(cardId: "card_id")
    XCTAssertNil(storage.getStateUntyped("id", cardId: "card_id"))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_PendingState_Peek_WhenEmpty_IsNil() {
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_Take_WhenEmpty_IsNil() {
    XCTAssertNil(storage.takePendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_SetById_ReadByPath() {
    storage.setPendingState(id: "id", cardId: "card_id", state: state1)
    let value = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(value, state1)
  }

  func test_PendingState_Take_ClearsTheSlot() {
    storage.setPendingState(id: "id", cardId: "card_id", state: state1)
    _ = storage.takePendingState(path(cardId: "card_id", path: "0/id"))
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_Set_AlsoUpdatesRegularState() {
    storage.setState(id: "id", cardId: "card_id", state: state1)
    storage.setPendingState(id: "id", cardId: "card_id", state: state2)
    XCTAssertEqual(storage.getState("id", cardId: "card_id"), state2)
    let pending = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(pending, state2)
  }

  func test_PendingState_NotOverwritten_BySetStateByPath() {
    storage.setPendingState(id: "id", cardId: "card_id", state: state1)
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state2)
    let pending = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(pending, state1)
  }

  func test_PendingState_LastWriteWins() {
    storage.setPendingState(id: "id", cardId: "card_id", state: state1)
    storage.setPendingState(id: "id", cardId: "card_id", state: state2)
    let pending = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(pending, state2)
  }

  func test_PendingState_Emits_StateUpdatesPipe() {
    var updatesCounter = 0
    storage.stateUpdates.addObserver { _ in
      updatesCounter += 1
    }.dispose(in: disposePool)

    storage.setPendingState(id: "id", cardId: "card_id", state: state1)

    XCTAssertEqual(updatesCounter, 1)
  }

  func test_PendingState_ClearedBy_Reset() {
    storage.setPendingState(id: "id", cardId: "card_id", state: state1)
    storage.reset()
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_ClearedBy_ResetByCardId() {
    storage.setPendingState(id: "id", cardId: "card_id", state: state1)
    storage.setPendingState(id: "id", cardId: "other_card", state: state2)
    storage.reset(cardId: "card_id")
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
    let other = storage.peekPendingState(path(cardId: "other_card", path: "0/id")) as? State
    XCTAssertEqual(other, state2)
  }

  func test_PreventUpdatePipeWhenSettingSameState() {
    var updatesCounter = 0
    storage.stateUpdates.addObserver { _ in
      updatesCounter += 1
    }.dispose(in: disposePool)

    storage
      .setState(id: "id", cardId: "card_id", state: state1) // Should update, new state for new ID
    storage.setState(id: "id", cardId: "card_id", state: state1) // Shouldn't update, same by ID
    storage
      .setState(
        path: path(cardId: "card_id", path: "0/div_state/state1/id"),
        state: state1
      ) // Shouldn't update, same by path

    storage.setState(id: "id", cardId: "card_id2", state: state1) // Should update, new ID

    storage
      .setState(
        path: path(cardId: "card_id", path: "0/div_state/state1/id2"),
        state: state1
      ) // Should update, new ID

    storage
      .setState(
        path: path(cardId: "card_id", path: "0/div_state/state1/id"),
        state: state2
      ) // Should update, new state for existing ID

    XCTAssertEqual(updatesCounter, 4)
  }

  func test_SetFocused_StoresLast() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id_1", cardId: "card_id_1"))
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id_2", cardId: "card_id_2"))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id_2", cardId: "card_id_2")))
  }

  func test_IfElementFocused_Unfocuses() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id", cardId: "card_id"))
    storage.setFocused(isFocused: false, element: IdAndCardId(id: "id", cardId: "card_id"))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_IfElementNotFocused_DoesNothing() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id_1", cardId: "card_id_1"))
    storage.setFocused(isFocused: false, element: IdAndCardId(id: "id_2", cardId: "card_id_2"))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id_1", cardId: "card_id_1")))
  }

  func test_Reset_ResetsFocusedElementByPath() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.reset()
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id_1", cardId: "card_id")))
  }

  func test_Reset_ResetsByCardIdFocusedElementByPath() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.reset(cardId: "card_id")
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
  }

  func test_SetFocusedByPath_StoresLast() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_2"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id_1", cardId: "card_id")))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id_2")))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id_2", cardId: "card_id")))
  }

  func test_SetFocusedByPath_WithTheSameId() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "1/id"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "1/id")))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_SetFocusedById_WithTheSameId() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id", cardId: "card_id"))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "1/id")))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_SetFocusedByPathAndId_StoresLast() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id_2", cardId: "card_id"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id_2")))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id_1", cardId: "card_id")))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id_2", cardId: "card_id")))
  }

  func test_SetFocusedByIdAndPath_StoresLast() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id_2", cardId: "card_id"))
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_2")))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id_1", cardId: "card_id")))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id_2", cardId: "card_id")))
  }

  func test_IfElementByPathFocused_Unfocuses() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.setFocused(isFocused: false, path: path(cardId: "card_id", path: "0/id"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
    XCTAssertFalse(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_SetFocusedByPath_GetById() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    XCTAssertTrue(storage.isFocused(element: IdAndCardId(id: "id", cardId: "card_id")))
  }

  func test_SetFocusedById_GetByPath() {
    storage.setFocused(isFocused: true, element: IdAndCardId(id: "id", cardId: "card_id"))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
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
  DivStatePath.makeDivStatePath(from: path).rawValue
}
