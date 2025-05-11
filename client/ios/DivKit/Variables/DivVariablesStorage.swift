import Foundation
import LayoutKit
import VGSL

public final class DivVariablesStorage {
  public struct Values {
    public internal(set) var global: DivVariables = [:]
    public internal(set) var local: [DivCardID: DivVariables] = [:]
  }

  public struct ChangeEvent {
    @frozen
    public enum Kind: Equatable {
      case local(DivCardID, Set<DivVariableName>)
      case global(Set<DivVariableName>)
    }

    public let kind: Kind

    init(_ kind: Kind) {
      self.kind = kind
    }

    public var changedVariables: Set<DivVariableName> {
      switch kind {
      case let .global(names):
        names
      case let .local(_, names):
        names
      }
    }
  }

  private let globalStorage: DivVariableStorage
  private var localStorages: [UIElementPath: DivVariableStorage] = [:]
  private let lock = AllocatedUnfairLock()

  private let changeEventsPipe = SignalPipe<ChangeEvent>()
  public let changeEvents: Signal<ChangeEvent>

  public convenience init() {
    self.init(outerStorage: nil)
  }

  public init(outerStorage: DivVariableStorage?) {
    globalStorage = DivVariableStorage(outerStorage: outerStorage, initialPath: nil)

    let globalStorageEvents: Signal<ChangeEvent> = globalStorage.changeEvents.compactMap {
      ChangeEvent(.global($0.changedVariables))
    }
    changeEvents = Signal.merge(globalStorageEvents, changeEventsPipe.signal)
  }

  func getOnlyElementVariables(cardId: DivCardID, elementId: String) -> DivVariables? {
    lock.withLock {
      let storages = localStorages.filter {
        $0.key.leaf == elementId && $0.key.cardId == cardId
      }.map(\.value)

      guard let storage = storages.first else {
        DivKitLogger.error("Element with id \(elementId) not found")
        return nil
      }
      guard storages.count == 1 else {
        DivKitLogger.error("Found multiple elements that respond to id: \(elementId)")
        return nil
      }

      guard storage.initialPath?.leaf == elementId else {
        return [:]
      }

      return storage.values
    }
  }

  func getVariableValue<T>(
    path: UIElementPath,
    name: DivVariableName
  ) -> T? {
    lock.withLock {
      getNearestStorage(path).getValue(name)
    }
  }

  public func getVariableValue<T>(
    cardId: DivCardID,
    name: DivVariableName
  ) -> T? {
    getVariableValue(path: cardId.path, name: name)
  }

  public func getVariableValue(
    cardId: DivCardID,
    name: DivVariableName
  ) -> DivVariableValue? {
    lock.withLock {
      getNearestStorage(cardId.path).getVariableValue(name)
    }
  }

  public func hasValue(cardId: DivCardID, name: DivVariableName) -> Bool {
    let localStorage = lock.withLock {
      localStorages[cardId.path]
    }
    return localStorage?.hasValue(name) ?? globalStorage.hasValue(name)
  }

  func initializeIfNeeded(path: UIElementPath, variables: DivVariables) {
    lock.withLock {
      if localStorages[path] != nil {
        // storage is already initialized
        return
      }
      let nearestStorage = getNearestStorage(path.parent)
      if variables.isEmpty {
        // optimization that allows to access the local storage for one operation
        localStorages[path] = nearestStorage
      } else {
        let localStorage = DivVariableStorage(outerStorage: nearestStorage, initialPath: path)
        localStorage.replaceAll(variables, notifyObservers: false)
        localStorages[path] = localStorage
      }
    }
  }

  /// Replaces all card variables with new ones.
  /// Does not affect global variables.
  public func set(
    cardId: DivCardID,
    variables: DivVariables
  ) {
    let localStorage = lock.withLock {
      let path = cardId.path
      if let localStorage = localStorages[path] {
        return localStorage
      }
      let localStorage = DivVariableStorage(outerStorage: globalStorage, initialPath: path)
      localStorages[path] = localStorage
      return localStorage
    }

    let oldValues = localStorage.values
    localStorage.replaceAll(variables, notifyObservers: true)

    let changedVariables = makeChangedVariables(old: oldValues, new: variables)
    if changedVariables.isEmpty {
      return
    }

    notify(ChangeEvent(.local(cardId, changedVariables)))
  }

  /// Adds new card variables.
  /// Does not affect global variables.
  public func append(
    variables newVariables: DivVariables,
    for cardId: DivCardID,
    replaceExisting: Bool = true
  ) {
    let oldVariables = lock.withLock {
      localStorages[cardId.path]?.values ?? [:]
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

  /// Deprecated. Do not use this method.
  @_spi(Legacy)
  public func makeVariables(for cardId: DivCardID) -> DivVariables {
    lock.withLock {
      localStorages[cardId.path]?.allValues ?? globalStorage.allValues
    }
  }

  public func reset() {
    lock.withLock {
      globalStorage.clear()
      localStorages = [:]
    }
  }

  public func reset(cardId: DivCardID) {
    lock.withLock {
      localStorages.keys
        .filter { $0.root == cardId.rawValue }
        .forEach { localStorages[$0] = nil }
    }
  }

  public func addObserver(_ action: @escaping (ChangeEvent) -> Void) -> Disposable {
    changeEvents.addObserver(action)
  }

  func getNearestStorage(_ path: UIElementPath?) -> DivVariableStorage {
    var currentPath: UIElementPath? = path
    while let path = currentPath {
      let localStorage = localStorages[path]
      if let localStorage {
        return localStorage
      }
      currentPath = path.parent
    }
    return globalStorage
  }

  private func notify(_ event: ChangeEvent) {
    onMainThread { [weak self] in
      self?.changeEventsPipe.send(event)
    }
  }
}

extension DivVariablesStorage {
  public func update(
    cardId: DivCardID,
    name: DivVariableName,
    value: String
  ) {
    update(path: cardId.path, name: name, value: value)
  }

  public func update(
    path: UIElementPath,
    name: DivVariableName,
    value: String
  ) {
    update(
      path: path,
      name: name,
      valueFactory: { makeDivVariableValue(oldValue: $0, name: name, value: value) }
    )
  }

  func update(
    path: UIElementPath,
    name: DivVariableName,
    value: DivVariableValue
  ) {
    update(path: path, name: name, valueFactory: { _ in value })
  }

  private func update(
    path: UIElementPath,
    name: DivVariableName,
    valueFactory: (DivVariableValue) -> DivVariableValue?
  ) {
    let storage = lock.withLock {
      getNearestStorage(path)
    }
    if storage.update(name: name, valueFactory: valueFactory), storage !== globalStorage {
      notify(ChangeEvent(.local(path.cardId, [name])))
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
    let newDictValue: DivDictionary? = parseCollectionVar(value)
    if let newDictValue {
      newValue = .dict(newDictValue)
    } else {
      newValue = nil
    }
  case .array:
    let newDictValue: DivArray? = parseCollectionVar(value)
    if let newDictValue {
      newValue = .array(newDictValue)
    } else {
      newValue = nil
    }
  }

  if newValue == nil {
    DivKitLogger.error("Incorrect value for variable \(name): \(value)")
  }

  return newValue
}

private func parseCollectionVar<T>(_ val: String) -> T? {
  guard let data = val.data(using: .utf8) else { return nil }

  if let json = try? JSONSerialization.jsonObject(
    with: data,
    options: []
  ) {
    return json as? T
  }

  return nil
}

extension Collection<DivVariable> {
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
        if let dictionary = DivDictionary.fromAny(dictVariable.value) {
          variables[name] = .dict(dictionary)
        } else {
          DivKitLogger.error("Incorrect value for dict variable \(name): \(dictVariable.value)")
          variables[name] = .dict([:])
        }
      case let .arrayVariable(arrayVariable):
        let name = DivVariableName(rawValue: arrayVariable.name)
        if variables.keys.contains(name) { return }
        if let array = DivArray.fromAny(arrayVariable.value) {
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

extension [String: Color] {
  public func mapToDivVariables() -> DivVariables {
    mapToDivVariables(valueTransform: DivVariableValue.color)
  }
}
