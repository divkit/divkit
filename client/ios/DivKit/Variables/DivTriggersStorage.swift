import Foundation

import BasePublic
import LayoutKit

public typealias DivCardsTriggers = [DivCardID: [DivTrigger]]

public final class DivTriggersStorage {
  private var cardsTriggers = DivCardsTriggers()
  private let variablesStorage: DivVariablesStorage
  private let actionHandler: DivActionHandler?
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let reporter: DivReporter
  private let cardsTriggersLock = RWLock()
  private let disposePool = AutodisposePool()

  public init(
    variablesStorage: DivVariablesStorage,
    actionHandler: DivActionHandler,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter? = nil
  ) {
    self.variablesStorage = variablesStorage
    self.actionHandler = actionHandler
    self.persistentValuesStorage = persistentValuesStorage
    self.reporter = reporter ?? DefaultDivReporter()

    variablesStorage.addObserver { [unowned self] event in
      let cardIdTriggersPairs = makeCardIdTriggersPairsForEvent(event)
      cardIdTriggersPairs.forEach { cardId, triggers in
        tryRunTriggerActions(
          triggers: triggers,
          cardId: cardId,
          changedVariablesNames: event.kind.names,
          newVariables: event.newValues.makeVariables(for: cardId),
          oldVariables: event.oldValues.makeVariables(for: cardId)
        )
      }
    }.dispose(in: disposePool)
  }

  private func makeCardIdTriggersPairsForEvent(
    _ event: DivVariablesStorage.ChangeEvent
  ) -> [(DivCardID, [DivTrigger])] {
    cardsTriggersLock.read {
      let changedCardIds = makeChangedCardIds(
        for: event.kind,
        cardIds: Array(cardsTriggers.keys)
      )
      return changedCardIds.compactMap { cardId in
        guard let triggers = cardsTriggers[cardId] else { return nil }
        return (cardId, triggers)
      }
    }
  }

  public func set(
    cardId: DivCardID,
    triggers: [DivTrigger]
  ) {
    cardsTriggersLock.write {
      cardsTriggers[cardId] = triggers
    }
    let variables = variablesStorage.makeVariables(for: cardId)
    tryRunTriggerActions(
      triggers: triggers,
      cardId: cardId,
      changedVariablesNames: Set(variables.keys),
      newVariables: variables,
      oldVariables: nil
    )
  }

  private func tryRunTriggerActions(
    triggers: [DivTrigger],
    cardId: DivCardID,
    changedVariablesNames: Set<DivVariableName>,
    newVariables: DivVariables,
    oldVariables: DivVariables?
  ) {
    triggers.forEach { trigger in
      if trigger.shouldPerformActions(
        cardId: cardId,
        changedVariablesNames: changedVariablesNames,
        newVariables: newVariables,
        oldVariables: oldVariables,
        persistentValuesStorage: persistentValuesStorage,
        reporter: reporter
      ) {
        trigger.actions.forEach {
          actionHandler?.handle(
            $0,
            cardId: cardId,
            source: .trigger,
            sender: nil
          )
        }
      }
    }
  }
}

extension DivVariablesStorage.ChangeEvent.Kind {
  var names: Set<DivVariableName> {
    switch self {
    case let .local(_, names):
      names
    case let .global(names):
      names
    }
  }
}

private func makeChangedCardIds(
  for eventKind: DivVariablesStorage.ChangeEvent.Kind,
  cardIds: [DivCardID]
) -> [DivCardID] {
  switch eventKind {
  case let .local(cardId, _):
    [cardId]
  case .global:
    cardIds
  }
}

extension DivTrigger {
  fileprivate func shouldPerformActions(
    cardId: DivCardID,
    changedVariablesNames: Set<DivVariableName>,
    newVariables: DivVariables,
    oldVariables: DivVariables?,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter
  ) -> Bool {
    if condition.variablesNames.intersection(changedVariablesNames).isEmpty {
      return false
    }

    let resolverWithNewVariables = ExpressionResolver(
      variables: newVariables,
      persistentValuesStorage: persistentValuesStorage,
      errorTracker: reporter.asExpressionErrorTracker(cardId: cardId)
    )
    guard resolveCondition(resolverWithNewVariables) ?? false else {
      return false
    }

    // oldVariables is nil for initial trigger resolving
    guard let oldVariables else {
      return true
    }

    switch resolveMode(resolverWithNewVariables) {
    case .onVariable:
      return true
    case .onCondition:
      let resolverWithOldVariables = ExpressionResolver(
        variables: oldVariables,
        persistentValuesStorage: persistentValuesStorage,
        errorTracker: reporter.asExpressionErrorTracker(cardId: cardId)
      )
      return !(resolveCondition(resolverWithOldVariables) ?? false)
    }
  }
}

extension Expression {
  fileprivate var variablesNames: Set<DivVariableName> {
    switch self {
    case let .link(link):
      Set(link.variablesNames.map(DivVariableName.init(rawValue:)))
    case .value:
      []
    }
  }
}
