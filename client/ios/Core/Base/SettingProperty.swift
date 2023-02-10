// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public enum SettingProperty<T> {}

extension SettingProperty where T: KeyValueDirectStoringSupporting {
  @inlinable
  public static func storage(_ storage: KeyValueStorage, key: String) -> Property<T?> {
    Property(getter: {
      storage.object(forKey: key) as? T
    }, setter: {
      if let newValue = $0 {
        storage.set(newValue, forKey: key)
      } else {
        storage.removeObject(forKey: key)
      }
    })
  }
}

extension SettingProperty where T: RawRepresentable, T.RawValue: KeyValueDirectStoringSupporting {
  @inlinable
  public static func rawStorage(_ storage: KeyValueStorage, key: String) -> Property<T?> {
    let field = SettingProperty<T.RawValue>.storage(storage, key: key)
    return Property(
      getter: { field.value.flatMap(T.init(rawValue:)) },
      setter: { field.value = $0?.rawValue }
    )
  }
}

extension SettingProperty where T: RawRepresentable, T.RawValue: KeyValueDirectStoringSupporting {
  @inlinable
  public static func rawArrayStorage(_ storage: KeyValueStorage, key: String) -> Property<[T]?> {
    let field = SettingProperty<[T.RawValue]>.storage(storage, key: key)
    return Property(
      getter: { field.value.map { $0.compactMap(T.init(rawValue:)) } },
      setter: { field.value = $0?.map { $0.rawValue } }
    )
  }
}

extension SettingProperty where T: NSCoding {
  @inlinable
  public static func encodedStorage(
    _ storage: KeyValueStorage,
    key: String,
    of _: T.Type = T.self
  ) -> Property<T?> {
    Property(getter: {
      guard let object = storage.object(forKey: key) else { return nil }
      guard let data = object as? Data else {
        assertionFailure()
        return nil
      }
      return try? (NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data) as? T)
    }, setter: {
      if let newValue = $0 {
        let encodedValue: Data?
        // Base is exported with minimal iOS version 9.0.
        // It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
        if #available(iOS 11, tvOS 11, *) {
          encodedValue = try? NSKeyedArchiver.archivedData(
            withRootObject: newValue,
            requiringSecureCoding: false
          )
        } else {
          encodedValue = NSKeyedArchiver.archivedData(
            withRootObject: newValue
          )
        }
        storage.set(encodedValue, forKey: key)
      } else {
        storage.removeObject(forKey: key)
      }
    })
  }
}

extension SettingProperty where T: Codable {
  public static func codableStorage(_ storage: KeyValueStorage, key: String) -> Property<T?> {
    SettingProperty<Data>.storage(storage, key: key).decoded(T.self)
  }
}

extension Property where T == Data? {
  public func decoded<U: Codable>(_: U.Type) -> Property<U?> {
    bimap(
      get: { $0.flatMap { try? SingleValueDecoder().decode(U.self, from: $0) } },
      set: {
        if let newValue = $0 {
          do {
            return try SingleValueEncoder().encode(newValue)
          } catch {
            assertionFailure(error.localizedDescription)
          }
        }

        return nil
      }
    )
  }
}

extension SettingProperty where T: ReferenceConvertible, T.ReferenceType: NSCoding {
  @inlinable
  public static func referencedEncodedStorage(
    _ storage: KeyValueStorage,
    key: String
  ) -> Property<T?> {
    let field = SettingProperty<T.ReferenceType>.encodedStorage(storage, key: key)
    return Property(
      getter: { field.value as! T? },
      setter: { field.value = ($0 as! T.ReferenceType?) }
    )
  }
}

public func stringPropertyForURLField(_ field: Property<URL?>) -> Property<String?> {
  field.bimap(
    get: { $0?.absoluteString.removingPercentEncoding },
    set: { $0.flatMap { URL(string: $0.percentEncodedURLString) } }
  )
}

extension KeyValueStorage {
  @inlinable
  public func makeCodableField<T>(key: String) -> Property<T?>
    where T: Codable {
    SettingProperty.codableStorage(self, key: key)
  }

  @inlinable
  public func makeCodableField<T>(key: String, default: T) -> Property<T>
    where T: Codable {
    SettingProperty.codableStorage(self, key: key).withDefault(`default`)
  }
}

extension KeyValueStorage {
  @inlinable
  public func makeField<T: KeyValueDirectStoringSupporting>(key: String) -> Property<T?> {
    SettingProperty.storage(self, key: key)
  }

  @inlinable
  public func makeField<T: KeyValueDirectStoringSupporting>(
    key: String,
    default: T
  ) -> Property<T> {
    SettingProperty.storage(self, key: key).withDefault(`default`)
  }

  @inlinable
  public func makeField<T: KeyValueDirectStoringSupporting>(
    key: String,
    writeDefaultIfNone defaultValue: T
  ) -> Property<T> {
    let field: Property<T?> = SettingProperty.storage(self, key: key)
    if field.value == nil {
      field.value = defaultValue
    }
    return field.withDefault(defaultValue)
  }

  @inlinable
  public func makeField<T>(key: String) -> Property<T?>
    where T: RawRepresentable, T.RawValue: KeyValueDirectStoringSupporting {
    SettingProperty.rawStorage(self, key: key)
  }

  @inlinable
  public func makeField<T>(key: String, default: T) -> Property<T>
    where T: RawRepresentable, T.RawValue: KeyValueDirectStoringSupporting {
    SettingProperty.rawStorage(self, key: key).withDefault(`default`)
  }
}

extension KeyValueStorage {
  @inlinable
  public func makeField<Element>(key: String) -> Property<[Element]?>
    where Element: RawRepresentable, Element.RawValue: KeyValueDirectStoringSupporting {
    SettingProperty.rawArrayStorage(self, key: key)
  }

  @inlinable
  public func makeField<Element>(key: String, default: [Element]) -> Property<[Element]>
    where Element: RawRepresentable, Element.RawValue: KeyValueDirectStoringSupporting {
    SettingProperty.rawArrayStorage(self, key: key).withDefault(`default`)
  }
}
