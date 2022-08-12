// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

import CommonCore

extension SettingProperty where T: Serializable & Deserializable {
  fileprivate static func serializableStorage(
    _ storage: KeyValueStorage,
    key: String
  ) -> Property<T?> {
    let field = SettingProperty<[String: Any]>.storage(storage, key: key)
    return Property(getter: {
      guard let val = field.value else { return nil }
      return try? T(dictionary: val)
    }, setter: { newValue in
      field.value = newValue?.toDictionary()
    })
  }
}

extension KeyValueStorage {
  public func makeField<T>(key: String) -> Property<T?>
    where T: Serializable, T: Deserializable {
    SettingProperty.serializableStorage(self, key: key)
  }

  public func makeField<T>(key: String, default: T) -> Property<T>
    where T: Serializable, T: Deserializable {
    SettingProperty.serializableStorage(self, key: key).withDefault(`default`)
  }
}
