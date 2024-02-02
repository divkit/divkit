import Foundation

import BasePublic

/// Stores variables.
/// Use ``DivVariableStorage`` to provide external variables into `DivKit`.
/// You can combine storages to have variables with different scopes.
public final class DivVariableStorage {
  public struct ChangeEvent {
    public let changedVariables: Set<DivVariableName>
    public let oldValues: DivVariables
  }

  private let outerStorage: DivVariableStorage?

  private var values = DivVariables()
  private let lock = RWLock()

  /// Gets all available variables including variables from outer storage.
  public var allValues: DivVariables {
    lock.read {
      _allValues
    }
  }

  private var _allValues: DivVariables {
    (outerStorage?.allValues ?? [:]) + values
  }

  private let changeEventsPipe = SignalPipe<ChangeEvent>()
  let changeEvents: Signal<ChangeEvent>

  /// Initializes a new instance of ``DivVariableStorage``.
  ///
  /// - Parameters:
  ///   - outerStorage: Storage that provides outer scope variables. Outer scope variables are
  /// accessible via current storage (see `getValue` and `update` methods). Outer scope variables
  /// can be shadowed.
  public init(outerStorage: DivVariableStorage? = nil) {
    self.outerStorage = outerStorage

    if let outerStorage {
      weak var weakSelf: DivVariableStorage?
      let outerStorageEvents: Signal<ChangeEvent> = outerStorage.changeEvents.compactMap {
        guard let self = weakSelf else {
          return nil
        }
        return ChangeEvent(
          changedVariables: $0.changedVariables,
          oldValues: $0.oldValues + self.values
        )
      }
      changeEvents = Signal.merge(outerStorageEvents, changeEventsPipe.signal)
      weakSelf = self
    } else {
      changeEvents = changeEventsPipe.signal
    }
  }

  /// Gets variable value.
  /// If variable with the given name not exists gets value from the outer storage.
  public func getValue<T>(_ name: DivVariableName) -> T? {
    let variable = lock.read {
      values[name]
    }
    if let variable {
      return variable.typedValue()
    }
    return outerStorage?.getValue(name)
  }

  /// Puts variable into the storage.
  /// Updates variable value if a variable with the given name already exists.
  /// Does not affect outer storage.
  public func put(name: DivVariableName, value: DivVariableValue) {
    var oldValues: DivVariables = [:]
    lock.write {
      oldValues = _allValues
      values[name] = value
    }
    if oldValues[name] != value {
      notify(ChangeEvent(changedVariables: [name], oldValues: oldValues))
    }
  }

  /// Puts multiple variables into the storage.
  /// Updates values of existing variables.
  /// Does not affect outer storage.
  public func put(
    _ variables: DivVariables,
    notifyObservers: Bool = true
  ) {
    let oldValues = allValues
    var changedVariables = Set<DivVariableName>()
    lock.write {
      values = values + variables
      if notifyObservers {
        for (name, value) in variables {
          if oldValues[name] != value {
            changedVariables.insert(name)
          }
        }
      }
    }
    if !changedVariables.isEmpty {
      notify(ChangeEvent(changedVariables: changedVariables, oldValues: oldValues))
    }
  }

  /// Replaces all variables with new ones in single transaction.
  /// Does not affect outer storage.
  public func replaceAll(
    _ variables: DivVariables,
    notifyObservers: Bool = true
  ) {
    lock.write {
      let oldValues = allValues
      values = variables
      if notifyObservers {
        let changedVariables = makeChangedVariables(old: oldValues, new: variables)
        if changedVariables.isEmpty {
          return
        }
        notify(ChangeEvent(changedVariables: changedVariables, oldValues: oldValues))
      }
    }
  }

  /// Updates variable value.
  /// If variable with the given name not exists updates value in the outer storage.
  public func update(name: DivVariableName, value: DivVariableValue) {
    update(name: name, valueFactory: { _ in value })
  }

  /// Clears the storage.
  /// Does not affect outer storage.
  public func clear() {
    lock.write {
      values = DivVariables()
    }
  }

  func update(
    name: DivVariableName,
    valueFactory: (DivVariableValue) -> DivVariableValue?
  ) {
    let hasLocalValue = lock.write {
      guard let oldValue = values[name] else {
        return false
      }

      if let value = valueFactory(oldValue), value != oldValue {
        let oldValues = allValues
        values[name] = value
        notify(ChangeEvent(changedVariables: [name], oldValues: oldValues))
      }

      return true
    }

    if hasLocalValue {
      return
    }

    if let outerStorage {
      outerStorage.update(name: name, valueFactory: valueFactory)
    } else {
      DivKitLogger.error("Variable is not declared: \(name)")
    }
  }

  public func addObserver(_ action: @escaping (ChangeEvent) -> Void) -> Disposable {
    changeEvents.addObserver(action)
  }

  private func notify(_ event: ChangeEvent) {
    onMainThread { [weak self] in
      self?.changeEventsPipe.send(event)
    }
  }
}

func makeChangedVariables(
  old: DivVariables,
  new: DivVariables
) -> Set<DivVariableName> {
  var result = Set<DivVariableName>()
  for (name, value) in old {
    if new[name] != value {
      result.insert(name)
    }
  }
  return result.union(Set(new.keys).subtracting(Set(old.keys)))
}
