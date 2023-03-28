import Foundation

import CommonCorePublic

public enum DivVariableNameTag {}
public typealias DivVariableName = Tagged<DivVariableNameTag, String>

@frozen
public enum DivVariableValue: Equatable {
  case string(String)
  case number(Double)
  case integer(Int)
  case bool(Bool)
  case color(Color)
  case url(URL)

  @inlinable
  public func typedValue<T>() -> T? {
    switch self {
    case let .string(value):
      return value as? T
    case let .number(value):
      return value as? T
    case let .integer(value):
      return value as? T
    case let .bool(value):
      return value as? T
    case let .color(value):
      return value as? T
    case let .url(value):
      return value as? T
    }
  }
}

public typealias DivVariables = [DivVariableName: DivVariableValue]

public final class DivVariablesStorage {
  public struct Values {
    public internal(set) var global: DivVariables = [:]
    public internal(set) var local: [DivCardID: DivVariables] = [:]

    func makeVariables(for cardId: DivCardID) -> DivVariables {
      global + (local[cardId] ?? [:])
    }
  }

  public struct ChangeEvent {
    @frozen
    public enum Kind: Equatable {
      case local(DivCardID, Set<DivVariableName>)
      case global(Set<DivVariableName>)
    }

    public let kind: Kind
    public let oldValues: Values
    public let newValues: Values

    init(kind: Kind, oldValues: Values, newValues: Values) {
      self.kind = kind
      self.oldValues = oldValues
      self.newValues = newValues
    }
  }

  private let changeEventsPipe = SignalPipe<ChangeEvent>()
  public var changeEvents: Signal<ChangeEvent> {
    changeEventsPipe.signal
  }

  private var storage = Values()
  private let rwLock = RWLock()

  public init() {}

  public func getVariableValue<T>(
    cardId: DivCardID,
    name: DivVariableName
  ) -> T? {
    let variable = storage.local[cardId]?[name] ?? storage.global[name]
    return variable?.typedValue()
  }
  
  public func set(
    cardId: DivCardID,
    variables: DivVariables
  ) {
    let oldValues = storage
    rwLock.write {
      storage.local[cardId] = variables
    }

    let changedVariables = makeChangedVariables(
      old: rwLock.read { oldValues.local[cardId] ?? [:] },
      new: variables
    )
    if changedVariables.isEmpty { return }
    update(ChangeEvent(
      kind: .local(cardId, changedVariables),
      oldValues: oldValues,
      newValues: storage
    ))
  }

  public func append(
    variables newVariables: DivVariables,
    for cardId: DivCardID,
    replaceExisting: Bool
  ) {
    let oldVariables = rwLock.read { storage.local[cardId] ?? [:] }
    let resultVariables = replaceExisting ?
      oldVariables + newVariables :
      newVariables + oldVariables
    set(cardId: cardId, variables: resultVariables)
  }

  public func set(
    variables: DivVariables,
    triggerUpdate: Bool
  ) {
    let oldValues = storage
    storage.global = variables
    if triggerUpdate {
      let changedVariables = makeChangedVariables(old: oldValues.global, new: variables)
      if changedVariables.isEmpty { return }
      update(ChangeEvent(
        kind: .global(changedVariables),
        oldValues: oldValues,
        newValues: storage
      ))
    }
  }

  public func append(
    variables: DivVariables,
    triggerUpdate: Bool
  ) {
    set(variables: storage.global + variables, triggerUpdate: triggerUpdate)
  }

  public func makeVariables(for cardId: DivCardID) -> DivVariables {
    rwLock.read {
      storage.makeVariables(for: cardId)
    }
  }

  public func reset() {
    rwLock.write {
      storage = Values()
    }
  }

  public func addObserver(_ action: @escaping (ChangeEvent) -> Void) -> Disposable {
    changeEvents.addObserver(action)
  }

  private func update(_ event: ChangeEvent) {
    onMainThread { [weak self] in
      self?.changeEventsPipe.send(event)
    }
  }
}

private func makeChangedVariables(
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

extension DivVariablesStorage: DivVariableUpdater {
  public func update(
    cardId: DivCardID,
    name: DivVariableName,
    value: String
  ) {
    rwLock.write {
      var cardVariables = storage.local[cardId]
      if let localValue = cardVariables?[name] {
        let oldValues = storage
        let isUpdated = cardVariables?
          .update(name: name, oldValue: localValue, value: value) ?? false
        storage.local[cardId] = cardVariables
        if isUpdated {
          update(ChangeEvent(
            kind: .local(cardId, [name]),
            oldValues: oldValues,
            newValues: storage
          ))
        }
      } else if let globalValue = storage.global[name] {
        let oldValues = storage
        let isUpdated = storage.global.update(name: name, oldValue: globalValue, value: value)
        if isUpdated {
          update(ChangeEvent(
            kind: .global([name]),
            oldValues: oldValues,
            newValues: storage
          ))
        }
      } else {
        DivKitLogger.error("Variable is not declared: \(name)")
      }
    }
  }
}

extension Dictionary where Key == DivVariableName, Value == DivVariableValue {
  fileprivate mutating func update(
    name: DivVariableName,
    oldValue: DivVariableValue,
    value: String
  ) -> Bool {
    let newValue: DivVariableValue?
    switch oldValue {
    case .string:
      newValue = .string(value)
    case .number:
      if let newNumber = Double(value) {
        newValue = .number(newNumber)
      } else {
        newValue = nil
      }
    case .integer:
      if let newInteger = Int(value) {
        newValue = .integer(newInteger)
      } else {
        newValue = nil
      }
    case .bool:
      switch value.lowercased() {
      case "0", "false":
        newValue = .bool(false)
      case "1", "true":
        newValue = .bool(true)
      default:
        newValue = nil
      }
    case .color:
      if let newColor = Color.color(withHexString: value) {
        newValue = .color(newColor)
      } else {
        newValue = nil
      }
    case .url:
      if let newURL = URL(string: value) {
        newValue = .url(newURL)
      } else {
        newValue = nil
      }
    }

    if newValue == oldValue {
      return false
    }

    if let newValue = newValue {
      self[name] = newValue
      return true
    }

    DivKitLogger.error("Incorrect value for variable \(name): \(value)")

    return false
  }
}

extension Collection where Element == DivVariable {
  public func extractDivVariableValues() -> DivVariables {
    var variables = DivVariables()
    forEach { variable in
      switch variable {
      case let .stringVariable(stringVariable):
        let name = DivVariableName(rawValue: stringVariable.name)
        if variables.keys.contains(name) { return }
        variables[name] = .string(stringVariable.value)
      case let .numberVariable(numberVariable):
        let name = DivVariableName(rawValue: numberVariable.name)
        if variables.keys.contains(name) { return }
        variables[name] = .number(numberVariable.value)
      case let .integerVariable(integerVariable):
        let name = DivVariableName(rawValue: integerVariable.name)
        if variables.keys.contains(name) { return }
        variables[name] = .integer(integerVariable.value)
      case let .booleanVariable(booleanVariable):
        let name = DivVariableName(rawValue: booleanVariable.name)
        if variables.keys.contains(name) { return }
        variables[name] = .bool(booleanVariable.value)
      case let .colorVariable(colorVariable):
        let name = DivVariableName(rawValue: colorVariable.name)
        if variables.keys.contains(name) { return }
        variables[name] = .color(colorVariable.value)
      case let .urlVariable(urlVariable):
        let name = DivVariableName(rawValue: urlVariable.name)
        if variables.keys.contains(name) { return }
        variables[name] = .url(urlVariable.value)
      }
    }
    return variables
  }
}

extension Dictionary where Key == String {
  public func mapToDivVariables(
    valueTransform: (Value) -> DivVariableValue
  ) -> DivVariables {
    var divVariables = DivVariables()
    forEach { element in
      divVariables[DivVariableName(rawValue: element.key)]
        = valueTransform(element.value)
    }
    return divVariables
  }
}

extension Dictionary where Key == String, Value == Color {
  public func mapToDivVariables() -> DivVariables {
    mapToDivVariables(valueTransform: DivVariableValue.color)
  }
}
