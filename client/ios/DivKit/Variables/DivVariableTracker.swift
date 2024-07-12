import Foundation
import VGSL

/// `DivVariableTracker` provides sets of variables used in a last `Block` modeling for a `DivCard`.
public final class DivVariableTracker {
  private var usedVariables = Atomic<[DivViewId: Set<DivVariableName>]>(initialValue: [:])

  /// Sets of variables used in a last `Block` modeling for a `DivCard`.
  public var usedVariablesByCard: [DivCardID: Set<DivVariableName>] {
    usedVariables.accessRead { value in
      value.map(key: { $0.cardId }, value: { $0 })
    }
  }

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
