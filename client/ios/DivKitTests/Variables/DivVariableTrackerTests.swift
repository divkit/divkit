@testable import DivKit

import XCTest

final class DivVariableTrackerTests: XCTestCase {
  private let variableTracker = DivVariableTracker()

  func test_onVariablesUsed_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(id: id1, variables: ["Var1", "Var2"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1"])
  }

  func test_onVariablesUsed_WithSameCardId_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(id: id1, variables: ["Var1", "Var2"])
    variableTracker.onVariablesUsed(id: id1, variables: ["Var3", "Var4"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1"])
  }

  func test_onVariablesUsed_WithDifferentCardId_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(id: id1, variables: ["Var1", "Var2"])
    variableTracker.onVariablesUsed(id: id2, variables: ["Var1", "Var3"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1", "card2"])
  }

  func test_onModelingStarted_ResetsAffectedCards() {
    variableTracker.onVariablesUsed(id: id1, variables: ["Var1", "Var2"])

    variableTracker.onModelingStarted(id: id1)

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertTrue(affectedCards.isEmpty)
  }

  func test_onVariablesUsed_WithAdditionalId_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(id: id1a1, variables: ["Var1"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1"])
  }

  func test_onModelingStarted_WithAdditionalId_DoesNotResetAffectedCards() {
    variableTracker.onVariablesUsed(id: id1, variables: ["Var1", "Var2"])

    variableTracker.onModelingStarted(id: id1a1)

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1"])
  }
}

private let id1 = DivViewId(cardId: "card1", additionalId: nil)
private let id1a1 = DivViewId(cardId: "card1", additionalId: "1")
private let id2 = DivViewId(cardId: "card2", additionalId: nil)
