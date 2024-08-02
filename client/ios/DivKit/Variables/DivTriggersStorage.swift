import Foundation

import LayoutKit
import VGSL

public final class DivTriggersStorage {
  private final class Item {
    final class Trigger {
      let divTrigger: DivTrigger
      var condition = false

      init(_ divTrigger: DivTrigger) {
        self.divTrigger = divTrigger
      }
    }

    let triggers: [Trigger]
    var active = true

    init(_ triggers: [Trigger]) {
      self.triggers = triggers
    }
  }

  private var triggersByPath: [UIElementPath: Item] = [:]
  private var disposablesByPath: [UIElementPath: Disposable] = [:]
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
  }

  public func set(
    cardId: DivCardID,
    triggers: [DivTrigger]
  ) {
    reset(cardId: cardId)
    setIfNeeded(path: cardId.path, triggers: triggers)
  }

  func setIfNeeded(
    path: UIElementPath,
    triggers: [DivTrigger]
  ) {
    let item = lock.withLock {
      if let item = triggersByPath[path] {
        return item
      }

      let newItem = Item(triggers.map { Item.Trigger($0) })
      if !newItem.triggers.isEmpty {
        triggersByPath[path] = newItem
        disposablesByPath[path] = variablesStorage
          .getNearestStorage(path)
          .addObserver { [weak self] event in
            self?.runActions(
              path: path,
              item: newItem,
              changedVariablesNames: event.changedVariables
            )
          }
      }
      return newItem
    }

    runActions(
      path: path,
      item: item,
      changedVariablesNames: nil
    )
  }

  func reset() {
    lock.withLock {
      triggersByPath.removeAll()
      disposablesByPath.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      triggersByPath.keys.forEach { path in
        if path.cardId == cardId {
          triggersByPath.removeValue(forKey: path)
          disposablesByPath.removeValue(forKey: path)
        }
      }
    }
  }

  func enableTriggers(path: UIElementPath) {
    let item = lock.withLock {
      let item = triggersByPath[path]
      item?.active = true
      return item
    }
    guard let item else { return }

    runActions(path: path, item: item, changedVariablesNames: nil)
  }

  func disableTriggers(path: UIElementPath) {
    lock.withLock {
      triggersByPath[path]?.active = false
    }
  }

  private func runActions(
    path: UIElementPath,
    item: Item,
    changedVariablesNames: Set<DivVariableName>?
  ) {
    guard item.active else { return }
    for trigger in item.triggers {
      let triggerVariablesNames = trigger.divTrigger.condition.variablesNames
      if triggerVariablesNames.isEmpty {
        // conditions without variables is considered to be invalid
        continue
      }

      if let changedVariablesNames, triggerVariablesNames.isDisjoint(with: changedVariablesNames) {
        continue
      }

      let oldCondition = trigger.condition
      let expressionResolver = ExpressionResolver(
        path: path,
        variablesStorage: variablesStorage,
        persistentValuesStorage: persistentValuesStorage,
        reporter: reporter
      )
      trigger.condition = trigger.divTrigger.resolveCondition(expressionResolver) ?? false
      if !trigger.condition {
        continue
      }
      if changedVariablesNames == nil, trigger.condition, oldCondition {
        continue
      }
      if trigger.divTrigger.resolveMode(expressionResolver) == .onCondition, oldCondition {
        continue
      }

      for action in trigger.divTrigger.actions {
        actionHandler?.handle(
          action,
          path: path,
          source: .trigger,
          sender: nil
        )
      }
    }
  }
}

extension DivTriggersStorage: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    if let tabState = state as? TabViewState {
      let activeTab = Int(tabState.selectedPageIndex)

      for index in 0..<tabState.countOfPages {
        if index != activeTab {
          disableTriggers(path: path + index)
        }
      }

      enableTriggers(path: path + activeTab)
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
