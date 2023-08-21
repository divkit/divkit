@testable import DivKit

import XCTest

final class DivVariableTrackerTests: XCTestCase {
  private let variableTracker = DivVariableTracker()

  func test_onVariablesUsed_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(cardId: "card1", variables: ["Var1", "Var2"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1"])
  }

  func test_onVariablesUsed_WithSameCardId_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(cardId: "card1", variables: ["Var1", "Var2"])
    variableTracker.onVariablesUsed(cardId: "card1", variables: ["Var3", "Var4"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1"])
  }

  func test_onVariablesUsed_WithDifferentCardId_UpdatesAffectedCards() {
    variableTracker.onVariablesUsed(cardId: "card1", variables: ["Var1", "Var2"])
    variableTracker.onVariablesUsed(cardId: "card2", variables: ["Var1", "Var3"])

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertEqual(affectedCards, ["card1", "card2"])
  }

  func test_onModelingStarted_ResetsAffectedCards() {
    variableTracker.onVariablesUsed(cardId: "card1", variables: ["Var1", "Var2"])

    variableTracker.onModelingStarted(cardId: "card1")

    let affectedCards = variableTracker.getAffectedCards(variables: ["Var1"])
    XCTAssertTrue(affectedCards.isEmpty)
  }
}
