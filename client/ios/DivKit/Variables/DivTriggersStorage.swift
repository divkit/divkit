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
  private let functionsStorage: DivFunctionsStorage?
  private let actionHandler: DivActionHandler?
  private let persistentValuesStorage: DivPersistentValuesStorage
  private let reporter: DivReporter
  private let disposePool = AutodisposePool()

  @_spi(Internal)
  public init(
    variablesStorage: DivVariablesStorage,
    functionsStorage: DivFunctionsStorage? = nil,
    blockStateStorage: DivBlockStateStorage,
    actionHandler: DivActionHandler,
    persistentValuesStorage: DivPersistentValuesStorage,
    reporter: DivReporter? = nil
  ) {
    self.variablesStorage = variablesStorage
    self.functionsStorage = functionsStorage
    self.actionHandler = actionHandler
    self.persistentValuesStorage = persistentValuesStorage
    self.reporter = reporter ?? DefaultDivReporter()

    blockStateStorage.stateUpdates.addObserver { [weak self] stateEvent in
      self?.handleStateEvent(stateEvent)
    }.dispose(in: disposePool)
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
      for path in triggersByPath.keys {
        if path.cardId == cardId {
          triggersByPath.removeValue(forKey: path)
          disposablesByPath.removeValue(forKey: path)
        }
      }
    }
  }

  func reset(elementId: String) {
    lock.withLock {
      for path in triggersByPath.keys {
        if path.contains(elementId) {
          triggersByPath.removeValue(forKey: path)
          disposablesByPath.removeValue(forKey: path)
        }
      }
    }
  }

  func enableTriggers(path: UIElementPath) {
    enableTriggers(predicate: { $0.starts(with: path) })
  }

  func disableTriggers(path: UIElementPath) {
    disableTriggers(predicate: { $0.starts(with: path) })
  }

  private func enableTriggers(predicate: (UIElementPath) -> Bool) {
    let items = lock.withLock {
      let triggers = triggersByPath.filter { predicate($0.key) }
      triggers.forEach { $0.value.active = true }
      return triggers
    }

    for (path, item) in items {
      runActions(path: path, item: item, changedVariablesNames: nil)
    }
  }

  private func disableTriggers(predicate: (UIElementPath) -> Bool) {
    lock.withLock {
      triggersByPath
        .filter { predicate($0.key) }
        .forEach { $0.value.active = false }
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
        functionsStorage: functionsStorage,
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

  private func handleStateEvent(_ stateEvent: DivBlockStateStorage.ChangeEvent) {
    if let tabState = stateEvent.state as? TabViewState {
      let activeTab = Int(tabState.selectedPageIndex)

      for index in 0..<tabState.countOfPages {
        if index != activeTab {
          disableTriggers(predicate: {
            $0.cardId == stateEvent.id.cardId
              && $0.findTabId(stateEvent.id.id) == String(index)
          })
        }
      }

      enableTriggers(predicate: {
        $0.cardId == stateEvent.id.cardId
          && $0.findTabId(stateEvent.id.id) == String(activeTab)
      })
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

extension UIElementPath {
  fileprivate func contains(_ part: String) -> Bool {
    var currPath: UIElementPath? = self
    while let path = currPath {
      if path.leaf == part {
        return true
      }
      currPath = currPath?.parent
    }
    return false
  }

  fileprivate func findTabId(_ tabsId: String) -> String? {
    var currPath: UIElementPath? = self
    while let path = currPath {
      if path.parent?.leaf == tabsId {
        return path.leaf
      }
      currPath = currPath?.parent
    }
    return nil
  }
}
