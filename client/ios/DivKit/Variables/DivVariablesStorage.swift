import Foundation

import CommonCorePublic

public enum DivVariableNameTag {}
public typealias DivVariableName = Tagged<DivVariableNameTag, String>

@frozen
public enum DivVariableValue: Hashable {
  case string(String)
  case number(Double)
  case integer(Int)
  case bool(Bool)
  case color(Color)
  case url(URL)
  case dict([String: AnyHashable])
  case array([AnyHashable])

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
    case let .dict(value):
      return value as? T
    case let .array(value):
      return value as? T
    }
  }

  init?<T>(_ value: T) {
    switch value {
    case let value as String:
      self = .string(value)
    case let value as Double:
      self = .number(value)
    case let value as Int:
      self = .integer(value)
    case let value as Bool:
      self = .bool(value)
    case let value as Color:
      self = .color(value)
    case let value as URL:
      self = .url(value)
    case let value as [String: AnyHashable]:
      self = .dict(value)
    default:
      return nil
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

  public func reset(cardId: DivCardID) {
    rwLock.write {
      storage.local[cardId] = [:]
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
    update(
      cardId: cardId,
      name: name,
      valueFactory: { makeDivVariableValue(oldValue: $0, name: name, value: value) }
    )
  }

  func update(
    cardId: DivCardID,
    name: DivVariableName,
    value: DivVariableValue
  ) {
    update(cardId: cardId, name: name, valueFactory: { _ in value })
  }

  private func update(
    cardId: DivCardID,
    name: DivVariableName,
    valueFactory: (DivVariableValue) -> DivVariableValue?
  ) {
    rwLock.write {
      var cardVariables = storage.local[cardId]
      if let localValue = cardVariables?[name] {
        let oldValues = storage
        guard let newValue = valueFactory(localValue), newValue != localValue else {
          return
        }
        cardVariables?[name] = newValue
        storage.local[cardId] = cardVariables
        update(ChangeEvent(
          kind: .local(cardId, [name]),
          oldValues: oldValues,
          newValues: storage
        ))
      } else if let globalValue = storage.global[name] {
        let oldValues = storage
        guard let newValue = valueFactory(globalValue), newValue != globalValue else {
          return
        }
        storage.global[name] = newValue
        update(ChangeEvent(
          kind: .global([name]),
          oldValues: oldValues,
          newValues: storage
        ))
      } else {
        DivKitLogger.error("Variable is not declared: \(name)")
      }
    }
  }
}

private func makeDivVariableValue(
  oldValue: DivVariableValue,
  name: DivVariableName,
  value: String
) -> DivVariableValue? {
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
  case .dict:
    newValue = nil
    DivKitLogger.warning("Unsupported variable type: dict")
  case .array:
    newValue = nil
    DivKitLogger.warning("Unsupported variable type: array")
  }

  if newValue == nil {
    DivKitLogger.error("Incorrect value for variable \(name): \(value)")
  }

  return newValue
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
      case let .dictVariable(dictVariable):
        let name = DivVariableName(rawValue: dictVariable.name)
        if variables.keys.contains(name) { return }
        if let dictionary = NSDictionary(dictionary: dictVariable.value) as? [String: AnyHashable] {
          variables[name] = .dict(dictionary)
        } else {
          DivKitLogger.error("Incorrect value for dict variable \(name): \(dictVariable.value)")
          variables[name] = .dict([:])
        }
      case let .arrayVariable(arrayVariable):
        let name = DivVariableName(rawValue: arrayVariable.name)
        if variables.keys.contains(name) { return }
        if let array = NSArray(array: arrayVariable.value) as? [AnyHashable] {
          variables[name] = .array(array)
        } else {
          DivKitLogger.error("Incorrect value for array variable \(name): \(arrayVariable.value)")
          variables[name] = .array([])
        }
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
