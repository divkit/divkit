import Foundation

import BasePublic

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

  private let globalStorage: DivVariableStorage
  private var cardVariables: [DivCardID: DivVariables] = [:]
  private let lock = RWLock()

  private let changeEventsPipe = SignalPipe<ChangeEvent>()
  public let changeEvents: Signal<ChangeEvent>

  private var allValues: Values {
    Values(
      global: globalStorage.allValues,
      local: cardVariables
    )
  }

  public convenience init() {
    self.init(outerStorage: nil)
  }

  public init(outerStorage: DivVariableStorage?) {
    globalStorage = DivVariableStorage(outerStorage: outerStorage)

    weak var weakSelf: DivVariablesStorage?
    let globalStorageEvents: Signal<ChangeEvent> = globalStorage.changeEvents.compactMap {
      guard let self = weakSelf else {
        return nil
      }
      return ChangeEvent(
        kind: .global($0.changedVariables),
        oldValues: Values(
          global: $0.oldValues,
          local: self.cardVariables
        ),
        newValues: self.allValues
      )
    }
    changeEvents = Signal.merge(globalStorageEvents, changeEventsPipe.signal)

    weakSelf = self
  }

  public func getVariableValue<T>(
    cardId: DivCardID,
    name: DivVariableName
  ) -> T? {
    let variable = lock.read {
      cardVariables[cardId]?[name]
    }
    if let variable = variable {
      return variable.typedValue()
    }
    return globalStorage.getValue(name)
  }

  public func set(
    cardId: DivCardID,
    variables: DivVariables
  ) {
    lock.write {
      let oldValues = allValues
      cardVariables[cardId] = variables

      let changedVariables = makeChangedVariables(
        old: oldValues.local[cardId] ?? [:],
        new: variables
      )
      if changedVariables.isEmpty {
        return
      }

      notify(
        ChangeEvent(
          kind: .local(cardId, changedVariables),
          oldValues: oldValues,
          newValues: allValues
        )
      )
    }
  }

  public func append(
    variables newVariables: DivVariables,
    for cardId: DivCardID,
    replaceExisting: Bool = true
  ) {
    let oldVariables = lock.read {
      cardVariables[cardId] ?? [:]
    }
    let resultVariables = replaceExisting ?
      oldVariables + newVariables :
      newVariables + oldVariables
    set(cardId: cardId, variables: resultVariables)
  }

  /// Deprecated. Use ``DivVariableStorage`` for global variables.
  public func set(
    variables: DivVariables,
    triggerUpdate: Bool
  ) {
    globalStorage.replaceAll(variables, notifyObservers: triggerUpdate)
  }

  /// Deprecated. Use ``DivVariableStorage`` for global variables.
  public func append(
    variables: DivVariables,
    triggerUpdate: Bool
  ) {
    globalStorage.put(variables, notifyObservers: triggerUpdate)
  }

  public func makeVariables(for cardId: DivCardID) -> DivVariables {
    lock.read {
      globalStorage.allValues + (cardVariables[cardId] ?? [:])
    }
  }

  public func reset() {
    lock.write {
      globalStorage.clear()
      cardVariables = [:]
    }
  }

  public func reset(cardId: DivCardID) {
    lock.write {
      cardVariables[cardId] = [:]
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
    lock.write {
      var variables = cardVariables[cardId]
      if let oldValue = variables?[name] {
        guard let newValue = valueFactory(oldValue), newValue != oldValue else {
          return
        }
        let oldValues = allValues
        variables?[name] = newValue
        cardVariables[cardId] = variables
        notify(
          ChangeEvent(
            kind: .local(cardId, [name]),
            oldValues: oldValues,
            newValues: allValues
          )
        )
      } else {
        globalStorage.update(name: name, valueFactory: valueFactory)
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
