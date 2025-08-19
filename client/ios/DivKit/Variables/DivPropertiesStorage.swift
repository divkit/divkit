import Foundation
import LayoutKit
import VGSL

final class DivPropertiesStorage {
  struct ChangeEvent {
    let changedProperties: Set<DivVariableName>
  }

  let initialPath: UIElementPath?

  private let outerStorage: DivPropertiesStorage?

  private var _values = DivProperties()
  private let lock = AllocatedUnfairLock()

  var allValues: DivProperties {
    lock.withLock {
      _allValues
    }
  }

  var values: DivProperties {
    lock.withLock {
      _values
    }
  }

  private var _allValues: DivProperties {
    (outerStorage?.allValues ?? [:]) + _values
  }

  convenience init(
    outerStorage: DivPropertiesStorage? = nil
  ) {
    self.init(outerStorage: outerStorage, initialPath: nil)
  }

  init(
    outerStorage: DivPropertiesStorage?,
    initialPath: UIElementPath?
  ) {
    self.outerStorage = outerStorage
    self.initialPath = initialPath
  }

  func getPropertyValue(_ name: DivVariableName) -> DivProperty? {
    let property = lock.withLock {
      _values[name]
    }
    return property ?? outerStorage?.getPropertyValue(name)
  }

  func getVariableValue(_ name: DivVariableName) -> DivVariableValue? {
    let variableValue = _values[name]?.toVariableValue()
    return variableValue ?? outerStorage?.getVariableValue(name)
  }

  /// Gets property value.
  /// If property with the given name not exists gets value from the outer storage.
  func getValue<T>(_ name: DivVariableName) -> T? {
    let property = lock.withLock {
      _values[name]
    }
    if let property {
      return property.typedValue()
    }
    return outerStorage?.getValue(name)
  }

  /// Replaces all properties with new ones in single transaction.
  /// Does not affect outer storage.
  func replaceAll(
    _ properties: DivProperties
  ) {
    lock.withLock {
      _values = properties
    }
  }

  func update(
    path: UIElementPath,
    name: DivVariableName,
    newValue: DivVariableValue?
  ) -> Bool {
    guard let property = (lock.withLock { _values[name] }),
          let newValue else {
      return false
    }
    guard let actionHandler = property.actionHandler else {
      DivKitLogger.error("Action Handler is not set in propertiesStorage")
      return false
    }

    for action in property.actions {
      actionHandler.handle(
        action,
        path: path,
        source: .property,
        localValues: [property.newValueVariableName: newValue.hashableValue],
        sender: nil
      )
    }
    return true
  }
}

extension DivVariableValue {
  fileprivate var hashableValue: AnyHashable {
    switch self {
    case let .string(value): return value
    case let .number(value): return value
    case let .integer(value): return value
    case let .bool(value): return value
    case let .color(value): return value
    case let .url(value): return value
    case let .dict(value): return value
    case let .array(value): return value
    }
  }
}
