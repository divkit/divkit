import Foundation

import VGSL_Fundamentals_Tiny

public final class DivVariableTracker {
  private var usedVariables = Atomic<[DivCardID: Set<DivVariableName>]>(initialValue: [:])

  init() {}

  func onModelingStarted(cardId: DivCardID) {
    usedVariables.accessWrite { value in
      value[cardId] = []
    }
  }

  func onVariablesUsed(cardId: DivCardID, variables: Set<DivVariableName>) {
    usedVariables.accessWrite { value in
      value[cardId] = (value[cardId] ?? []).union(variables)
    }
  }

  func getAffectedCards(variables: Set<DivVariableName>) -> Set<DivCardID> {
    var cardIds = Set<DivCardID>()
    usedVariables.accessRead { value in
      value.forEach { cardId, usedVariables in
        if !usedVariables.isDisjoint(with: variables) {
          cardIds.insert(cardId)
        }
      }
    }
    return cardIds
  }
}
