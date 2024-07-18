import Foundation

import LayoutKit
import VGSL

public final class DivTriggersStorage {
  private typealias CardTriggers = (cardId: DivCardID, items: [Item])

  private final class Item {
    let trigger: DivTrigger
    var condition = false

    init(_ trigger: DivTrigger) {
      self.trigger = trigger
    }
  }

  private var triggersByCard: [CardTriggers] = []
  private let lock = AllocatedUnfairLock()

  private let variablesStorage: DivVariablesStorage
  private let actionHandler: DivActionHandler?
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let reporter: DivReporter
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
      runActions(event: event)
    }.dispose(in: disposePool)
  }

  public func set(
    cardId: DivCardID,
    triggers: [DivTrigger]
  ) {
    let cardTriggers = (cardId, triggers.map { Item($0) })
    lock.withLock {
      triggersByCard.removeAll { $0.cardId == cardId }
      triggersByCard.append(cardTriggers)
    }
    runActions(
      cardTriggers: cardTriggers,
      changedVariablesNames: nil
    )
  }

  private func runActions(event: DivVariablesStorage.ChangeEvent) {
    let triggers = lock.withLock {
      switch event.kind {
      case let .local(cardId, _):
        triggersByCard
          .first { $0.cardId == cardId }
          .map { [$0] } ?? []
      case .global:
        triggersByCard
      }
    }

    for cardTriggers in triggers {
      runActions(
        cardTriggers: cardTriggers,
        changedVariablesNames: event.changedVariables
      )
    }
  }

  private func runActions(
    cardTriggers: CardTriggers,
    changedVariablesNames: Set<DivVariableName>?
  ) {
    let cardId = cardTriggers.cardId
    for item in cardTriggers.items {
      let trigger = item.trigger
      let triggerVariablesNames = trigger.condition.variablesNames
      if triggerVariablesNames.isEmpty {
        // conditions without variables is considered to be invalid
        continue
      }

      if let changedVariablesNames, triggerVariablesNames.isDisjoint(with: changedVariablesNames) {
        continue
      }

      let oldCondition = item.condition
      let expressionResolver = ExpressionResolver(
        path: cardId.path,
        variablesStorage: variablesStorage,
        persistentValuesStorage: persistentValuesStorage,
        reporter: reporter
      )
      item.condition = trigger.resolveCondition(expressionResolver) ?? false
      if !item.condition {
        continue
      }
      if trigger.resolveMode(expressionResolver) == .onCondition, oldCondition {
        continue
      }

      for action in trigger.actions {
        actionHandler?.handle(
          action,
          path: cardId.path,
          source: .trigger,
          sender: nil
        )
      }
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
