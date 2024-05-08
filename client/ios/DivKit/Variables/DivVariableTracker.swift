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

  func getAffectedCards(variables: Set<DivVariableName>) -> [DivCardID: Set<DivVariableName>] {
    var affectedCards = [DivCardID: Set<DivVariableName>]()
    usedVariables.accessRead { value in
      for (id, usedVariables) in value {
        let intersection = usedVariables.intersection(variables)
        if !intersection.isEmpty {
          affectedCards[id.cardId] = intersection
        }
      }
    }
    return affectedCards
  }
}
