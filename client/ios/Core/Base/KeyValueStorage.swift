// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public protocol KeyValueStorage: AnyObject {
  func object(forKey: String) -> Any?
  func removeObject(forKey: String)
  func set(_ value: Any?, forKey: String)
  subscript(_: String) -> Any? { get set }
}

extension KeyValueStorage {
  public subscript(_ str: String) -> Any? {
    get {
      object(forKey: str)
    }
    set {
      set(newValue, forKey: str)
    }
  }

  public func nested(forKey key: String) -> KeyValueStorage {
    PropertyStorage(
      property: SettingProperty.storage(self, key: key).withDefault([String: Any]())
    )
  }
}

extension UserDefaults: KeyValueStorage {}

private final class PropertyStorage: KeyValueStorage {
  // cast of Any to [String: KeyValueDirectStoringSupporting] doesn't work
  let property: Property<[String: Any]>

  init(property: Property<[String: Any]>) {
    self.property = property
  }

  func object(forKey key: String) -> Any? {
    property.value[key]
  }

  func removeObject(forKey key: String) {
    property.value[key] = nil
  }

  func set(_ obj: Any?, forKey key: String) {
    assert(obj == nil || obj is KeyValueDirectStoringSupporting)
    property.value[key] = obj as? KeyValueDirectStoringSupporting
  }
}
