import Foundation

import VGSL_Fundamentals_Tiny

public final class DivVariableTracker {
  private var usedVariables = Atomic<[DivViewId: Set<DivVariableName>]>(initialValue: [:])

  init() {}

  func onModelingStarted(id: DivViewId) {
    usedVariables.accessWrite { value in
      value[id] = []
    }
  }

  func onVariablesUsed(id: DivViewId, variables: Set<DivVariableName>) {
    usedVariables.accessWrite { value in
      value[id] = (value[id] ?? []).union(variables)
    }
  }

  func getAffectedCards(variables: Set<DivVariableName>) -> Set<DivCardID> {
    var cardIds = Set<DivCardID>()
    usedVariables.accessRead { value in
      value.forEach { id, usedVariables in
        if !usedVariables.isDisjoint(with: variables) {
          cardIds.insert(id.cardId)
        }
      }
    }
    return cardIds
  }
}
