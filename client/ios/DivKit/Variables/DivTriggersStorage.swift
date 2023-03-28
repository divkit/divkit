import Foundation

import BasePublic
import LayoutKit

public typealias DivCardsTriggers = [DivCardID: [DivTrigger]]

public final class DivTriggersStorage {
  private var cardsTriggers = DivCardsTriggers()
  private let variablesStorage: DivVariablesStorage
  private let actionHandler: DivActionHandler?
  private let urlOpener: UrlOpener

  private let cardsTriggersLock = RWLock()
  private let autodisposePool = AutodisposePool()

  public init(
    variablesStorage: DivVariablesStorage,
    actionHandler: DivActionHandler,
    urlOpener: @escaping UrlOpener
  ) {
    self.variablesStorage = variablesStorage
    self.actionHandler = actionHandler
    self.urlOpener = urlOpener
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
        oldVariables: oldVariables
      ) {
        trigger.actions.forEach {
          actionHandler?.handle(
            $0,
            cardId: cardId,
            source: .custom,
            urlOpener: urlOpener
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
    oldVariables: DivVariables
  ) -> Bool {
    let resolverWithNewVariables = ExpressionResolver(variables: newVariables)
    guard
      !condition.variablesNames.intersection(changedVariablesNames).isEmpty,
      resolveCondition(resolverWithNewVariables) ?? false
    else { return false }

    switch resolveMode(resolverWithNewVariables) {
    case .onVariable:
      return true
    case .onCondition:
      let resolverWithOldVariables = ExpressionResolver(variables: oldVariables)
      return !(resolveCondition(resolverWithOldVariables) ?? false)
    }
  }
}

extension Expression {
  fileprivate var variablesNames: Set<DivVariableName> {
    switch self {
    case let .link(resolver):
      return Set(resolver.variablesNames.map(DivVariableName.init(rawValue:)))
    case .value:
      return []
    }
  }
}
