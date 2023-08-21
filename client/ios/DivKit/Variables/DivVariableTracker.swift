import Foundation

public final class DivVariableTracker {
  private var usedVariables: [DivCardID: Set<DivVariableName>] = [:]

  init() {}

  func onModelingStarted(cardId: DivCardID) {
    usedVariables[cardId] = []
  }

  func onVariablesUsed(cardId: DivCardID, variables: Set<DivVariableName>) {
    usedVariables[cardId] = (usedVariables[cardId] ?? []).union(variables)
  }

  func getAffectedCards(variables: Set<DivVariableName>) -> Set<DivCardID> {
    var cardIds = Set<DivCardID>()
    usedVariables.forEach { cardId, usedVariables in
      if (!usedVariables.isDisjoint(with: variables)) {
        cardIds.insert(cardId)
      }
    }
    return cardIds
  }
}
