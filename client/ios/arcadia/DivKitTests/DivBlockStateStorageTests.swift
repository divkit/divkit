// Copyright 2022 Yandex LLC. All rights reserved.

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
    XCTAssertNil(storage.getStateUntyped(path("0/id")))
  }

  func test_GetState_ById_NotExists() {
    XCTAssertNil(storage.getStateUntyped("id"))
  }

  func test_SetState_WithPath_GetState_ByPath() {
    storage.setState(path: path("0/id"), state: state1)
    XCTAssertEqual(storage.getState(path("0/id")), state1)
  }

  func test_SetState_WithPath_GetState_ById() {
    storage.setState(path: path("0/id"), state: state1)
    XCTAssertEqual(storage.getState("id"), state1)
  }

  func test_SetState_WithId_GetState_ByPath() {
    storage.setState(id: "id", state: state1)
    XCTAssertEqual(storage.getState(path("0/div_state/state1/id")), state1)
  }

  func test_SetState_WithId_GetState_ById() {
    storage.setState(id: "id", state: state1)
    XCTAssertEqual(storage.getState("id"), state1)
  }

  func test_SetState_WithPath_OverridesWithId() {
    storage.setState(id: "id", state: state1)
    storage.setState(path: path("0/id"), state: state2)
    XCTAssertEqual(storage.getState(path("0/id")), state2)
  }

  func test_SetState_WithId_OverridesWithPath() {
    storage.setState(path: path("0/id"), state: state1)
    storage.setState(id: "id", state: state2)
    XCTAssertEqual(storage.getState(path("0/id")), state2)
  }

  func test_Reset_ResetsPaths() {
    storage.setState(path: path("0/id"), state: state1)
    storage.reset()
    XCTAssertNil(storage.getStateUntyped(path("0/id")))
  }

  func test_Reset_ResetsIds() {
    storage.setState(id: "id", state: state1)
    storage.reset()
    XCTAssertNil(storage.getStateUntyped("id"))
  }
}

private let state1 = State(name: "State 1")
private let state2 = State(name: "State 2")

private struct State: ElementState, Equatable {
  public let name: String
}

private func path(_ path: String) -> UIElementPath {
  UIElementPath.parseDivPath(path)!
}
