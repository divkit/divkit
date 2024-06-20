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

  func onVariableUsed(id: DivViewId, variable: DivVariableName) {
    usedVariables.accessWrite { value in
      value[id] = (value[id] ?? []).union([variable])
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
