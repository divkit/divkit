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

    public var changedVariables: Set<DivVariableName> {
      switch kind {
      case let .global(names):
        names
      case let .local(_, names):
        names
      }
    }

    init(_ kind: Kind) {
      self.kind = kind
    }

  }

  public let changeEvents: Signal<ChangeEvent>

  private let globalStorage: DivVariableStorage
  private var localStorages: [UIElementPath: DivVariableStorage] = [:]
  private let lock = AllocatedUnfairLock()

  private let changeEventsPipe = SignalPipe<ChangeEvent>()

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
  public func extractDivVariableValues(
    _ resolver: ExpressionResolver? = nil
  ) -> DivVariables {
    var variables = DivVariables()
    forEach { variable in
      let resolver = resolver?.modifying(variableValueProvider: {
        variables[DivVariableName(rawValue: $0)]?.typedValue()
      })
      let name = DivVariableName(rawValue: variable.name)
      switch variable {
      case let .stringVariable(stringVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = stringVariable.resolveValue(resolver) {
          variables[name] = .string(value)
        } else if let value = stringVariable.value.rawValue {
          variables[name] = .string(value)
        }
      case let .numberVariable(numberVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = numberVariable.resolveValue(resolver) {
          variables[name] = .number(value)
        } else if let value = numberVariable.value.rawValue {
          variables[name] = .number(value)
        }
      case let .integerVariable(integerVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = integerVariable.resolveValue(resolver) {
          variables[name] = .integer(value)
        } else if let value = integerVariable.value.rawValue {
          variables[name] = .integer(value)
        }
      case let .booleanVariable(booleanVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = booleanVariable.resolveValue(resolver) {
          variables[name] = .bool(value)
        } else if let value = booleanVariable.value.rawValue {
          variables[name] = .bool(value)
        }
      case let .colorVariable(colorVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = colorVariable.resolveValue(resolver) {
          variables[name] = .color(value)
        } else if let value = colorVariable.value.rawValue {
          variables[name] = .color(value)
        }
      case let .urlVariable(urlVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = urlVariable.resolveValue(resolver) {
          variables[name] = .url(value)
        } else if let value = urlVariable.value.rawValue {
          variables[name] = .url(value)
        }
      case let .dictVariable(dictVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = dictVariable.resolveValue(resolver) {
          variables[name] = .dict(makeDivDict(name: name.rawValue, value: value))
        } else if let value = dictVariable.value.rawValue {
          variables[name] = .dict(makeDivDict(name: name.rawValue, value: value))
        }
      case let .arrayVariable(arrayVariable):
        guard !variables.keys.contains(name) else { return }
        if let resolver, let value = arrayVariable.resolveValue(resolver) {
          variables[name] = .array(makeDivArray(name: name.rawValue, value: value))
        } else if let value = arrayVariable.value.rawValue {
          variables[name] = .array(makeDivArray(name: name.rawValue, value: value))
        }
      }
    }
    return variables
  }
}

private func makeDivDict(name: String, value: [String: Any]) -> DivDictionary {
  if let dictionary = DivDictionary.fromAny(value) {
    return dictionary
  } else {
    DivKitLogger.error("Incorrect value for dict variable \(name): \(value)")
    return [:]
  }
}

private func makeDivArray(name: String, value: [Any]) -> DivArray {
  if let array = DivArray.fromAny(value) {
    return array
  } else {
    DivKitLogger.error("Incorrect value for array variable \(name): \(value)")
    return []
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

extension DivVariable {
  fileprivate var name: String {
    switch self {
    case let .integerVariable(variable): variable.name
    case let .numberVariable(variable): variable.name
    case let .booleanVariable(variable): variable.name
    case let .stringVariable(variable): variable.name
    case let .urlVariable(variable): variable.name
    case let .colorVariable(variable): variable.name
    case let .arrayVariable(variable): variable.name
    case let .dictVariable(variable): variable.name
    }
  }
}

extension Expression {
  fileprivate var rawValue: T? {
    switch self {
    case let .value(value): ExpressionValueConverter.cast(value)
    case .link: nil
    }
  }
}
