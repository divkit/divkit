@testable import DivKit

import Foundation
import XCTest

import BasePublic

final class DivStateManagementTests: XCTestCase {
  private var stateManagement: DivStateManagement!
  private var currentTimestamp: Milliseconds = 0

  override func setUp() {
    let storageFileUrl = FileManager.default
      .urls(for: .applicationSupportDirectory, in: .userDomainMask)
      .first!
      .appendingPathComponent(DefaultDivStateManagement.storageFileName)
    try? FileManager.default.removeItem(at: storageFileUrl)

    stateManagement = makeStateManagement()
  }
  
  func test_getStateManagerForCard_ReturnsSameStateManagerForSameCard() {
    let stateManager1 = stateManagement.getStateManagerForCard(cardId: "card1")
    let stateManager2 = stateManagement.getStateManagerForCard(cardId: "card1")

    XCTAssertIdentical(stateManager1, stateManager2)
  }

  func test_getStateManagerForCard_MakesNewStateManagerForNewCard() {
    let stateManager1 = stateManagement.getStateManagerForCard(cardId: "card1")
    let stateManager2 = stateManagement.getStateManagerForCard(cardId: "card2")

    XCTAssertNotIdentical(stateManager1, stateManager2)
  }

  func test_set_UpdatesStateManager() {
    stateManagement.set(
      path: DivStatePath.makeDivStatePath(from: "0/DivId/State1")!,
      cardId: cardId,
      lifetime: .short
    )

    XCTAssertEqual(
      stateManagement.getStateManagerForCard(cardId: cardId).items,
      [
        DivStatePath.makeDivStatePath(from: "0/DivId"):
          DivStateManager.Item(
            currentStateID: DivStateID(rawValue: "State1"),
            previousState: .initial
          )
      ]
    )
  }

  func test_set_StoresPersistentState() {
    stateManagement.set(
      path: DivStatePath.makeDivStatePath(from: "0/DivId/State1")!,
      cardId: cardId,
      lifetime: .long
    )

    shiftTime(days: 1)

    XCTAssertEqual(
      makeStateManagement().getStateManagerForCard(cardId: cardId).items,
      [
        DivStatePath.makeDivStatePath(from: "0/DivId"):
          DivStateManager.Item(
            currentStateID: DivStateID(rawValue: "State1"),
            previousState: .initial
          )
      ]
    )
  }

  func test_StoredStateExpires() {
    stateManagement.set(
      path: DivStatePath.makeDivStatePath(from: "0/Div1/State1")!,
      cardId: cardId,
      lifetime: .long
    )

    shiftTime(days: 1.5)

    stateManagement.set(
      path: DivStatePath.makeDivStatePath(from: "0/Div2/State1")!,
      cardId: cardId,
      lifetime: .long
    )

    shiftTime(days: 1.5)

    XCTAssertEqual(
      makeStateManagement().getStateManagerForCard(cardId: cardId).items,
      [
        DivStatePath.makeDivStatePath(from: "0/Div2"):
          DivStateManager.Item(
            currentStateID: DivStateID(rawValue: "State1"),
            previousState: .initial
          )
      ]
    )
  }

  private func makeStateManagement() -> DivStateManagement {
    DefaultDivStateManagement(
      timestampProvider: Variable { self.currentTimestamp }
    )
  }

  private func shiftTime(days: Float) {
    currentTimestamp += Milliseconds(days * 86400000)
  }
}

private let cardId = DivCardID(rawValue: "card")
