@testable import DivKit

import XCTest

final class DivVariableTrackerTests: XCTestCase {
  private let variableTracker = DivVariableTracker()

  func test_onVariableUsed_UpdatesAffectedCards() {
    variableTracker.onVariableUsed(id: id1, variable: "Var1")

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1", "Var2"])
    XCTAssertEqual(affectedCards, ["card1": ["Var1"]])
  }

  func test_onVariableUsed_WithSameCardId_UpdatesAffectedCards() {
    variableTracker.onVariableUsed(id: id1, variable: "Var1")
    variableTracker.onVariableUsed(id: id1, variable: "Var2")
    variableTracker.onVariableUsed(id: id1, variable: "Var3")

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1", "Var2"])
    XCTAssertEqual(affectedCards, ["card1": ["Var1", "Var2"]])
  }

  func test_onVariableUsed_WithDifferentCardId_UpdatesAffectedCards() {
    variableTracker.onVariableUsed(id: id1, variable: "Var1")
    variableTracker.onVariableUsed(id: id1, variable: "Var2")
    variableTracker.onVariableUsed(id: id2, variable: "Var1")
    variableTracker.onVariableUsed(id: id2, variable: "Var3")

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1", "Var2"])
    XCTAssertEqual(affectedCards, ["card1": ["Var1", "Var2"], "card2": ["Var1"]])
  }

  func test_onModelingStarted_ResetsAffectedCards() {
    variableTracker.onVariableUsed(id: id1, variable: "Var1")

    variableTracker.onModelingStarted(id: id1)

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertTrue(affectedCards.isEmpty)
  }

  func test_onVariableUsed_WithAdditionalId_UpdatesAffectedCards() {
    variableTracker.onVariableUsed(id: id1a1, variable: "Var1")

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1": ["Var1"]])
  }

  func test_onModelingStarted_WithAdditionalId_DoesNotResetAffectedCards() {
    variableTracker.onVariableUsed(id: id1, variable: "Var1")

    variableTracker.onModelingStarted(id: id1a1)

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1": ["Var1"]])
  }
}

private let id1 = DivViewId(cardId: "card1", additionalId: nil)
private let id1a1 = DivViewId(cardId: "card1", additionalId: "1")
private let id2 = DivViewId(cardId: "card2", additionalId: nil)
