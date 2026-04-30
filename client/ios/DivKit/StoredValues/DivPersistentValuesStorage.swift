import Foundation
import VGSL

public final class DivPersistentValuesStorage {
  static let storageFileName = "divkit.values_storage"

  private let timestampProvider: Variable<Milliseconds>

  private let storage = Property<StoredValues>(
    fileName: storageFileName,
    initialValue: StoredValues(items: [:]),
    onError: { error in
      DivKitLogger.error("Failed to create storage: \(error)")
    }
  )

  public init(
    timestampProvider: Variable<Milliseconds> = Variable {
      Date().timeIntervalSince1970.milliseconds
    }
  ) {
    self.timestampProvider = timestampProvider
    removeOutdatedStoredValues()
  }

  func set(value: DivStoredValue, cardId: DivCardID? = nil) {
    let key = storageKey(name: value.name, cardId: cardId)
    var items = storage.value.items
    items[key] = StoredValue(
      timestamp: timestampProvider.value,
      value: value.value,
      type: value.type,
      lifetimeInSec: value.lifetimeInSec
    )
    storage.value = StoredValues(items: items)
  }

  func get<T>(name: String, cardId: DivCardID? = nil) -> T? {
    let key = storageKey(name: name, cardId: cardId)
    let items = storage.value.items
    let currentTimestamp = timestampProvider.value
    guard let storedValue = items[key] else {
      return nil
    }
    let elapsedTimeInSec = (currentTimestamp - storedValue.timestamp) / 1000
    guard elapsedTimeInSec < storedValue.lifetimeInSec else {
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

  func clearValues(forCardId cardId: DivCardID) {
    let prefix = cardScopedKeyPrefix(cardId: cardId)
    var items = storage.value.items
    items = items.filter { !$0.key.hasPrefix(prefix) }
    storage.value = StoredValues(items: items)
  }

  func reset() {
    storage.value = StoredValues(items: [:])
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

extension DivPersistentValuesStorage: Clearable {
  public func clear() {
    reset()
  }

  public func clear(cardId: DivCardID) {
    clearValues(forCardId: cardId)
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
      .string(value)
    case .number:
      if let value = Double(value) {
        .number(value)
      } else {
        nil
      }
    case .integer:
      if let value = Int(value) {
        .integer(value)
      } else {
        nil
      }
    case .bool, .boolean:
      switch value.lowercased() {
      case "0", "false":
        .bool(false)
      case "1", "true":
        .bool(true)
      default:
        nil
      }
    case .color:
      if let value = Color.color(withHexString: value) {
        .color(value)
      } else {
        nil
      }
    case .url:
      if let value = URL(string: value) {
        .url(value)
      } else {
        nil
      }
    case .array:
      if let rawValue = try? JSONSerialization.jsonObject(jsonString: value) as? [Any],
         let array = DivArray.fromAny(rawValue) {
        .array(array)
      } else {
        nil
      }
    case .dict:
      if let rawValue = try? JSONSerialization.jsonObject(jsonString: value) as? [String: Any],
         let dict = DivDictionary.fromAny(rawValue) {
        .dict(dict)
      } else {
        nil
      }
    }
  }

  var isValueValid: Bool {
    variable != nil
  }
}

private func cardScopedKeyPrefix(cardId: DivCardID) -> String {
  "\(cardKeyPrefix)\(cardId.rawValue)"
}
private func storageKey(name: String, cardId: DivCardID?) -> String {
  guard let cardId else { // global scope
    return name
  }
  return "\(cardScopedKeyPrefix(cardId: cardId))\(name)"
}
private let cardKeyPrefix = "card_"
