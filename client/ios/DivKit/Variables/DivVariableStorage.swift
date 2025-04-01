import Foundation
import VGSL

/// Stores variables.
/// Use ``DivVariableStorage`` to provide external variables into `DivKit`.
/// You can combine storages to have variables with different scopes.
public final class DivVariableStorage {
  public struct ChangeEvent {
    public let changedVariables: Set<DivVariableName>
  }

  private let outerStorage: DivVariableStorage?

  private var _values = DivVariables()
  private let lock = AllocatedUnfairLock()

  /// Gets all available variables including variables from outer storage.
  public var allValues: DivVariables {
    lock.withLock {
      _allValues
    }
  }

  var values: DivVariables {
    lock.withLock {
      _values
    }
  }

  private var _allValues: DivVariables {
    (outerStorage?.allValues ?? [:]) + _values
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
      changeEvents = Signal.merge(outerStorage.changeEvents, changeEventsPipe.signal)
    } else {
      changeEvents = changeEventsPipe.signal
    }
  }

  /// Gets variable value.
  /// If variable with the given name not exists gets value from the outer storage.
  /// Returns `DivVariableValue`.
  public func getVariableValue(_ name: DivVariableName) -> DivVariableValue? {
    let variable = lock.withLock {
      _values[name]
    }
    return variable ?? outerStorage?.getValue(name)
  }

  /// Gets variable value.
  /// If variable with the given name not exists gets value from the outer storage.
  public func getValue<T>(_ name: DivVariableName) -> T? {
    let variable = lock.withLock {
      _values[name]
    }
    if let variable {
      return variable.typedValue()
    }
    return outerStorage?.getValue(name)
  }

  /// Gets value indicating if variable with the given name is availabale.
  public func hasValue(_ name: DivVariableName) -> Bool {
    let variable = lock.withLock {
      _values[name]
    }
    return variable != nil || outerStorage?.hasValue(name) ?? false
  }

  /// Puts variable into the storage.
  /// Updates variable value if a variable with the given name already exists.
  /// Does not affect outer storage.
  public func put(name: DivVariableName, value: DivVariableValue) {
    let oldValues = lock.withLock {
      let oldValues = _allValues
      _values[name] = value
      return oldValues
    }
    if oldValues[name] != value {
      notify(ChangeEvent(changedVariables: [name]))
    }
  }

  /// Puts multiple variables into the storage.
  /// Updates values of existing variables.
  /// Does not affect outer storage.
  public func put(
    _ variables: DivVariables,
    notifyObservers: Bool = true
  ) {
    let changedVariables = lock.withLock {
      var changedVariables = Set<DivVariableName>()
      let oldValues = _allValues
      _values = _values + variables
      if notifyObservers {
        for (name, value) in variables {
          if oldValues[name] != value {
            changedVariables.insert(name)
          }
        }
      }
      return changedVariables
    }
    if !changedVariables.isEmpty {
      notify(ChangeEvent(changedVariables: changedVariables))
    }
  }

  /// Replaces all variables with new ones in single transaction.
  /// Does not affect outer storage.
  public func replaceAll(
    _ variables: DivVariables,
    notifyObservers: Bool = true
  ) {
    let changedVariables = lock.withLock {
      let oldValues = _allValues
      _values = variables
      if notifyObservers {
        return makeChangedVariables(old: oldValues, new: variables)
      }
      return []
    }
    if !changedVariables.isEmpty {
      notify(ChangeEvent(changedVariables: changedVariables))
    }
  }

  /// Updates variable value.
  /// If variable with the given name not exists updates value in the outer storage.
  public func update(name: DivVariableName, value: DivVariableValue) {
    _ = update(name: name, valueFactory: { _ in value })
  }

  /// Clears the storage.
  /// Does not affect outer storage.
  public func clear() {
    lock.withLock {
      _values = DivVariables()
    }
  }

  /// Removes variables from the storage.
  /// Does not affect outer storage.
  public func remove(
    variableNames: Set<DivVariableName>,
    notifyObservers: Bool = true
  ) {
    let changedVariables = lock.withLock {
      var changedVariables = Set<DivVariableName>()
      for variableName in variableNames {
        let removedValue = _values.removeValue(forKey: variableName)
        if removedValue != nil {
          changedVariables.insert(variableName)
        }
      }
      return changedVariables
    }

    if !changedVariables.isEmpty, notifyObservers {
      notify(ChangeEvent(changedVariables: changedVariables))
    }
  }

  func update(
    name: DivVariableName,
    valueFactory: (DivVariableValue) -> DivVariableValue?
  ) -> Bool {
    var (isUpdated, hasLocalValue) = lock.withLock {
      guard let oldValue = _values[name] else {
        return (false, false)
      }
      if let value = valueFactory(oldValue), value != oldValue {
        _values[name] = value
        return (true, true)
      }
      return (false, true)
    }

    if isUpdated {
      notify(ChangeEvent(changedVariables: [name]))
    }

    if hasLocalValue {
      return isUpdated
    }

    if let outerStorage {
      isUpdated = outerStorage.update(name: name, valueFactory: valueFactory)
    } else {
      DivKitLogger.error("Variable is not declared: \(name)")
    }

    return isUpdated
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
