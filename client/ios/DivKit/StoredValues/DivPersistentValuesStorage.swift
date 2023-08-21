import Foundation

import BasePublic
import Foundation

public final class DivPersistentValuesStorage {
  static let storageFileName = "divkit.values_storage"
  private let timestampProvider: Variable<Milliseconds>

  public init(
    timestampProvider: Variable<Milliseconds> = Variable {
      Date().timeIntervalSince1970.milliseconds
    }
  ) {
    self.timestampProvider = timestampProvider
    removeOutdatedStoredValues()
  }

  private let storage = Property<StoredValues>(
    fileName: storageFileName,
    initialValue: StoredValues(items: [:]),
    onError: { error in
      DivKitLogger.error("Failed to create storage: \(error)")
    }
  )

  func set(value: DivStoredValue) {
    var items = storage.value.items
    items[value.name] = StoredValue(
      timestamp: timestampProvider.value,
      value: value.value,
      type: value.type,
      lifetimeInSec: value.lifetimeInSec
    )
    storage.value = StoredValues(items: items)
  }

  func get<T>(name: String) -> T? {
    let items = storage.value.items
    let currentTimestamp = timestampProvider.value
    guard let storedValue = items[name],
          currentTimestamp - storedValue.timestamp < storedValue.lifetimeInSec * 1000 else {
      return nil
    }
    let divStoredValue = DivStoredValue(
      name: name,
      value: storedValue.value,
      type: storedValue.type,
      lifetimeInSec: storedValue.lifetimeInSec
    )
    guard let value: T = divStoredValue.variable?.typedValue() else {
      DivKitLogger.error("The type of the stored value \(name) is not \(T.self)")
      return nil
    }
    return value
  }

  private func removeOutdatedStoredValues() {
    let currentTimestamp = timestampProvider.value
    let items = storage.value.items
    let newItems = items.filter { _, value in
      currentTimestamp - value.timestamp < value.lifetimeInSec * 1000
    }

    if newItems.count != items.count {
      storage.value = StoredValues(items: newItems)
    }
  }
}

private struct StoredValues: Equatable, Codable {
  let items: [String: StoredValue]
}

private struct StoredValue: Equatable, Codable {
  let timestamp: Milliseconds
  let value: String
  let type: DivStoredValue.ValueType
  let lifetimeInSec: Int
}

extension DivStoredValue {
  var variable: DivVariableValue? {
    switch type {
    case .string:
      return .string(value)
    case .number:
      if let value = Double(value) {
        return .number(value)
      } else {
        return nil
      }
    case .integer:
      if let value = Int(value) {
        return .integer(value)
      } else {
        return nil
      }
    case .bool:
      switch value.lowercased() {
      case "0", "false":
        return .bool(false)
      case "1", "true":
        return .bool(true)
      default:
        return nil
      }
    case .color:
      if let value = Color.color(withHexString: value) {
        return .color(value)
      } else {
        return nil
      }
    case .url:
      if let value = URL(string: value) {
        return .url(value)
      } else {
        return nil
      }
    }
  }

  var isValueValid: Bool {
    variable != nil
  }
}
