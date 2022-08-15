import Foundation

import Base
import LayoutKit

public typealias DivCardsTriggers = [DivCardID: [DivTrigger]]

public final class DivTriggersStorage {
  private var cardsTriggers = DivCardsTriggers()
  private let variablesStorage: DivVariablesStorage
  private let actionHandler: DivActionHandler?
  private let urlOpener: UrlOpener

  private let autodisposePool = AutodisposePool()

  #if INTERNAL_BUILD

  public init() {
    variablesStorage = DivVariablesStorage()
    actionHandler = nil
    urlOpener = { _ in }
  }

  #endif

  public init(
    variablesStorage: DivVariablesStorage,
    actionHandler: DivActionHandler,
    urlOpener: @escaping UrlOpener
  ) {
    self.variablesStorage = variablesStorage
    self.actionHandler = actionHandler
    self.urlOpener = urlOpener
    variablesStorage.changeEvents.addObserver { [unowned self] event in
      let changedCardIds = makeChangedCardIds(
        for: event.kind,
        cardIds: Array(cardsTriggers.keys)
      )
      for cardId in changedCardIds {
        if let triggers = cardsTriggers[cardId] {
          tryRunTriggerActions(
            triggers: triggers,
            cardId: cardId,
            changesVariablesNames: event.kind.names,
            newVariables: event.variables.new.makeVariables(for: cardId),
            oldVariables: event.variables.old.makeVariables(for: cardId)
          )
        }
      }
    }.dispose(in: autodisposePool)
  }

  public func set(
    cardId: DivCardID,
    triggers: [DivTrigger]
  ) {
    cardsTriggers[cardId] = triggers
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
