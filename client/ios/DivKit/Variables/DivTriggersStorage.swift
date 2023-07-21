import Foundation

import BasePublic
import LayoutKit

public typealias DivCardsTriggers = [DivCardID: [DivTrigger]]

public final class DivTriggersStorage {
  private var cardsTriggers = DivCardsTriggers()
  private let variablesStorage: DivVariablesStorage
  private let actionHandler: DivActionHandler?
  private let persistentValuesStorage: DivPersistentValuesStorage

  private let cardsTriggersLock = RWLock()
  private let autodisposePool = AutodisposePool()

  public init(
    variablesStorage: DivVariablesStorage,
    actionHandler: DivActionHandler,
    persistentValuesStorage: DivPersistentValuesStorage
  ) {
    self.variablesStorage = variablesStorage
    self.actionHandler = actionHandler
    self.persistentValuesStorage = persistentValuesStorage

    variablesStorage.addObserver { [unowned self] event in
      let cardIdTriggersPairs = makeCardIdTriggersPairsForEvent(event)
      cardIdTriggersPairs.forEach { (cardId, triggers) in
        tryRunTriggerActions(
          triggers: triggers,
          cardId: cardId,
          changesVariablesNames: event.kind.names,
          newVariables: event.newValues.makeVariables(for: cardId),
          oldVariables: event.oldValues.makeVariables(for: cardId)
        )
      }
    }.dispose(in: autodisposePool)
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
      changesVariablesNames: Set(variables.keys),
      newVariables: variables,
      oldVariables: [:]
    )
  }

  private func tryRunTriggerActions(
    triggers: [DivTrigger],
    cardId: DivCardID,
    changesVariablesNames: Set<DivVariableName>,
    newVariables: DivVariables,
    oldVariables: DivVariables
  ) {
    triggers.forEach { trigger in
      if trigger.shouldPerformActions(
        for: changesVariablesNames,
        newVariables: newVariables,
        oldVariables: oldVariables,
        persistentValuesStorage: persistentValuesStorage
      ) {
        trigger.actions.forEach {
          actionHandler?.handle(
            $0,
            cardId: cardId,
            source: .custom,
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
      return names
    case let .global(names):
      return names
    }
  }
}

private func makeChangedCardIds(
  for eventKind: DivVariablesStorage.ChangeEvent.Kind,
  cardIds: [DivCardID]
) -> [DivCardID] {
  switch eventKind {
  case let .local(cardId, _):
    return [cardId]
  case .global:
    return cardIds
  }
}

extension DivTrigger {
  func shouldPerformActions(
    for changedVariablesNames: Set<DivVariableName>,
    newVariables: DivVariables,
    oldVariables: DivVariables,
    persistentValuesStorage: DivPersistentValuesStorage
  ) -> Bool {
    let resolverWithNewVariables = ExpressionResolver(variables: newVariables, persistentValuesStorage: persistentValuesStorage)
    guard
      !condition.variablesNames.intersection(changedVariablesNames).isEmpty,
      resolveCondition(resolverWithNewVariables) ?? false
    else { return false }

    switch resolveMode(resolverWithNewVariables) {
    case .onVariable:
      return true
    case .onCondition:
      let resolverWithOldVariables = ExpressionResolver(variables: oldVariables, persistentValuesStorage: persistentValuesStorage)
      return !(resolveCondition(resolverWithOldVariables) ?? false)
    }
  }
}

extension Expression {
  fileprivate var variablesNames: Set<DivVariableName> {
    switch self {
    case let .link(link):
      return Set(link.variablesNames.map(DivVariableName.init(rawValue:)))
    case .value:
      return []
    }
  }
}
