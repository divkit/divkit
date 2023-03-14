import CoreFoundation
import Foundation

import CommonCore

// MARK: Utils

extension Dictionary {
  fileprivate init<S: Sequence>(_ seq: S) where S.Iterator.Element == (Key, Value) {
    self.init()
    for (key, value) in seq { self[key] = value }
  }
}

@usableFromInline
func invalidFieldErrorForKey<T, U>(
  _ key: [T],
  element: Int? = nil,
  representation: U?
) -> DeserializationError {
  let keyStrings: [String] = key.map { "\($0)" }
  return .invalidFieldRepresentation(
    field: "\(keyStrings.joined(separator: "."))" + (element.map { "[\($0)]" } ?? ""),
    representation: representation
  )
}

extension Dictionary where Key == String {
  @usableFromInline
  func enclosedDictForKeySequence(_ keys: [Key]) throws -> Self {
    guard !keys.isEmpty else {
      throw DeserializationError.noData
    }

    guard keys.count > 1 else {
      return self
    }

    var dict = self
    for key in keys.dropLast() {
      dict = try dict.getField(key) as Self
    }

    return dict
  }
}

// MARK: Required values (private interface)

extension Dictionary where Key == String {
  @usableFromInline
  func getField<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>?
  ) throws -> U {
    let dict = try enclosedDictForKeySequence(key)
    guard let valueBeforeConversion = dict[key.last!] else {
      throw DeserializationError.noData
    }

    guard let transformed = try? (valueBeforeConversion as? T).map(transform),
          validator?.isValid(transformed) != false else {
      throw invalidFieldErrorForKey(key, representation: valueBeforeConversion)
    }

    return transformed
  }

  @usableFromInline
  func getArray<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>?
  ) throws -> [U] {
    let dict = try enclosedDictForKeySequence(key)
    guard let valueBeforeConversion = dict[key.last!] else {
      throw DeserializationError.noData
    }

    guard let array = valueBeforeConversion as? NSArray else {
      throw invalidFieldErrorForKey(key, representation: valueBeforeConversion)
    }

    var result: [U] = []
    result.reserveCapacity(array.count)
    for index in 0..<array.count {
      do {
        guard let element = array[index] as? T else {
          throw invalidFieldErrorForKey(
            key,
            element: index,
            representation: array[index]
          )
        }

        result.append(try transform(element))
      } catch {
        if validator?.isPartialDeserializationAllowed == false {
          throw error
        }
      }
    }

    if result.count != array.count,
       validator?.isPartialDeserializationAllowed == false {
      throw invalidFieldErrorForKey(key, representation: array)
    }

    if validator?.isValid(result) == false {
      throw invalidFieldErrorForKey(key, representation: array)
    }

    return result
  }
}

// MARK: Optional values (private interface)

extension Dictionary where Key == String {
  @usableFromInline
  func getOptionalField<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>?
  ) throws -> U? {
    guard let dict = try? enclosedDictForKeySequence(key), let value = dict[key.last!] else {
      return nil
    }

    guard let valueBeforeConversion = value as? T,
          let result = try? transform(valueBeforeConversion),
          validator?.isValid(result) != false else {
      return nil
    }

    return result
  }

  @usableFromInline
  func getOptionalArray<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>?
  ) throws -> [U]? {
    guard let dict = try? enclosedDictForKeySequence(key), let value = dict[key.last!] else {
      return nil
    }

    guard let valueBeforeConversion = value as? NSArray else {
      return nil
    }

    let result: [U] = try valueBeforeConversion.enumerated().compactMap {
      do {
        guard let element = $0.element as? T else {
          throw invalidFieldErrorForKey(
            key,
            element: $0.offset,
            representation: $0.element
          )
        }

        return try transform(element)
      } catch {
        if validator?.isPartialDeserializationAllowed == false {
          throw error
        }
        return nil
      }
    }

    if result.count != valueBeforeConversion.count,
       validator?.isPartialDeserializationAllowed == false {
      throw invalidFieldErrorForKey(key, representation: valueBeforeConversion)
    }

    if validator?.isValid(result) == false {
      return nil
    }

    return result
  }
}

// MARK: Adapters for transform

extension Dictionary where Key == String {
  @usableFromInline
  func getField<T, U>(
    _ key: [Key],
    transform: (T) -> U?,
    validator: AnyValueValidator<U>?
  ) throws -> U {
    try getField(
      key,
      transform: { (value: T) throws -> U in
        guard let result = transform(value) else {
          throw invalidFieldErrorForKey(key, representation: value)
        }
        return result
      },
      validator: validator
    )
  }

  @usableFromInline
  func getArray<T, U>(
    _ key: [Key],
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>?
  ) throws -> [U] {
    try getArray(
      key,
      transform: { (value: T) throws -> U in
        guard let result = transform(value) else {
          throw invalidFieldErrorForKey(key, representation: value)
        }
        return result
      },
      validator: validator
    )
  }

  @usableFromInline
  func getOptionalField<T, U>(
    _ key: [Key],
    transform: (T) -> U?,
    validator: AnyValueValidator<U>?
  ) throws -> U? {
    try getOptionalField(key, transform: { (value: T) throws -> U in
      guard let result = transform(value) else {
        throw invalidFieldErrorForKey(key, representation: value)
      }
      return result
    }, validator: validator)
  }

  @usableFromInline
  func getOptionalArray<T, U>(
    _ key: [Key],
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>?
  ) throws -> [U]? {
    try getOptionalArray(key, transform: { (value: T) throws -> U in
      guard let result = transform(value) else {
        throw invalidFieldErrorForKey(key, representation: value)
      }
      return result
    }, validator: validator)
  }
}

// MARK: Required values (public interface)

extension Dictionary where Key == String {
  public func getField(_ key: Key..., validator: AnyValueValidator<Any>? = nil) throws -> Any {
    try getField(key, transform: { $0 as Any }, validator: validator)
  }

  public func getField(
    _ key: Key...,
    validator: AnyValueValidator<CFString>? = nil
  ) throws -> CFString {
    let transform: (CFTypeRef) throws -> CFString = {
      if let value: CFString = safeCFCast($0) {
        return value
      } else {
        throw invalidFieldErrorForKey(key, representation: $0)
      }
    }
    return try getField(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getField<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T {
    try getField(key, transform: { $0 as T }, validator: validator)
  }

  @inlinable
  public func getField<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T {
    try getField(key, transform: T.init(rawValue:), validator: validator)
  }

  @inlinable
  public func getField<T: Deserializable>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T {
    try getField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict) },
      validator: validator
    )
  }

  @inlinable
  public func getArray(
    _ key: Key...,
    validator: AnyArrayValueValidator<Any>? = nil
  ) throws -> [Any] {
    try getArray(key, transform: { (value: Any) throws -> Any in value }, validator: validator)
  }

  @inlinable
  public func getArray<T: ValidSerializationValue, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>? = nil
  ) throws -> [U] {
    try getArray(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getArray<T: ValidSerializationValue, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>? = nil
  ) throws -> [U] {
    try getArray(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T] {
    try getArray(
      key, transform: { (obj: Any) -> T? in obj as? T },
      validator: validator
    )
  }

  @inlinable
  public func getArray<T: Deserializable>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T] {
    try getArray(
      key,
      transform: { (dict: Self) in try T(dictionary: dict) },
      validator: validator
    )
  }

  @inlinable
  public func getField<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil
  ) throws -> U {
    try getField(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getField<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyValueValidator<U>? = nil
  ) throws -> U {
    try getField(key, transform: transform, validator: validator)
  }

  public func getURL(_ key: Key..., validator: AnyValueValidator<URL>? = nil) throws -> URL {
    try getField(key, transform: URL.init(string:), validator: validator)
  }

  public func getObjCCompatibleBool(_ key: Key...) -> Bool? {
    (try? self.getField(key, transform: { $0 as NSString }, validator: nil) as NSString)?.boolValue
      ?? (try? self.getField(key, transform: { $0 as Bool }, validator: nil) as Bool)
  }
}

// MARK: Optional values (public interface)

extension Dictionary where Key == String {
  public func getOptionalField(
    _ key: Key...,
    validator: AnyValueValidator<Any>? = nil
  ) throws -> Any? {
    try getOptionalField(key, transform: { $0 as Any }, validator: validator)
  }

  public func getOptionalField(
    _ key: Key...,
    validator: AnyValueValidator<CFString>? = nil
  ) throws -> CFString? {
    try getOptionalField(key, transform: safeCFCast, validator: validator)
  }

  @inlinable
  public func getOptionalField<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T? {
    try getOptionalField(key, transform: { $0 as T }, validator: validator)
  }

  @inlinable
  public func getOptionalField<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T? {
    try getOptionalField(key, transform: T.init(rawValue:), validator: validator)
  }

  @inlinable
  public func getOptionalField<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyValueValidator<U>? = nil
  ) throws -> U? {
    try getOptionalField(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getOptionalField<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil
  ) throws -> U? {
    try getOptionalField(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getOptionalField<T: Deserializable>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T? {
    try getOptionalField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict) },
      validator: validator
    )
  }

  public func getOptionalArray(
    _ key: Key...,
    validator: AnyArrayValueValidator<Any>? = nil
  ) throws -> [Any]? {
    try getOptionalArray(
      key,
      transform: { (value: Any) throws -> Any in value },
      validator: validator
    )
  }

  @inlinable
  public func getOptionalArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T]? {
    try getOptionalArray(
      key,
      transform: { (obj: Any) -> T? in obj as? T },
      validator: validator
    )
  }

  @inlinable
  public func getOptionalArray<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T]? {
    try getOptionalArray(
      key,
      transform: { (obj: Any) -> T? in (obj as? T.RawValue).flatMap { T(rawValue: $0) } },
      validator: validator
    )
  }

  @inlinable
  public func getOptionalArray<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>? = nil
  ) throws -> [U]? {
    try getOptionalArray(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getOptionalArray<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>? = nil
  ) throws -> [U]? {
    try getOptionalArray(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getOptionalArray<T: Deserializable>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T]? {
    try getOptionalArray(
      key, transform: { (dict: Self) in try T(dictionary: dict) },
      validator: validator
    )
  }

  public func getOptionalURL(
    _ key: Key...,
    validator: AnyValueValidator<URL>? = nil
  ) throws -> URL? {
    try getOptionalField(key, transform: URL.init(string:), validator: validator)
  }
}
