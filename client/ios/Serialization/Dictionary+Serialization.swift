import CoreFoundation
import Foundation

import CommonCorePublic

// MARK: Utils

extension DeserializationResult {
  fileprivate func toThrows() throws -> T {
    switch self {
    case .success(let value):
      return value
    case .partialSuccess(let value, _):
      return value
    case .failure(let errors):
      throw errors.last
    case .noValue:
      throw DeserializationError.noData
    }
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

@usableFromInline
func getResult<U>(_ block: () throws -> U) -> DeserializationResult<U> {
  do {
    return try .success(block())
  } catch let error as DeserializationError {
    return .failure(NonEmptyArray(error))
  } catch {
    assertionFailure("Closure should throw only DeserializationError")
    return .failure(NonEmptyArray(.unexpectedError(message: error.localizedDescription)))
  }
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
    try getArray(key, transform: transform, validator: validator).toThrows()
  }

  @usableFromInline
  func getArray<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>?
  ) -> DeserializationResult<[U]> {
    getArray(key, transform: { value in getResult { try transform(value) } }, validator: validator)
  }

  @usableFromInline
  func getArray<T, U>(
    _ key: [Key],
    transform: (T) -> DeserializationResult<U>,
    validator: AnyArrayValueValidator<U>?
  ) -> DeserializationResult<[U]> {
    var errors: [DeserializationError] = []

    let dictResult = getResult { try enclosedDictForKeySequence(key) }
    if case .failure(let dictErrors) = dictResult {
      return .failure(dictErrors)
    }

    errors.append(contentsOf: dictResult.errorsOrWarnings?.asArray() ?? [])

    guard let dict = dictResult.value,
          let valueBeforeConversion = dict[key.last!] else {
      return .failure(NonEmptyArray(.noData, errors))
    }

    guard let array = valueBeforeConversion as? NSArray else {
      return .failure(
        NonEmptyArray(
          invalidFieldErrorForKey(key, representation: valueBeforeConversion),
          errors
        )
      )
    }

    var result: [U] = []

    result.reserveCapacity(array.count)
    for index in 0..<array.count {
      if let element = array[index] as? T {
        let resultElement = transform(element)
        errors.append(contentsOf: resultElement.errorsOrWarnings?.asArray() ?? [])

        if let resultValue = resultElement.value {
          result.append(resultValue)
          continue
        }
      }
      errors.append(
        invalidFieldErrorForKey(key, element: index, representation: array[index])
      )
      if validator?.isPartialDeserializationAllowed == false {
        return .failure(NonEmptyArray(errors)!)
      }
    }

    if validator?.isValid(result) == false {
      errors.append(invalidFieldErrorForKey(key, representation: array))
      return .failure(NonEmptyArray(errors)!)
    }

    if result.count != array.count, validator?.isPartialDeserializationAllowed == false {
      errors.append(invalidFieldErrorForKey(key, representation: array))
      return .failure(NonEmptyArray(errors)!)
    }

    return errors.isEmpty
    ? .success(result)
    : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
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
      transform: transform,
      validator: validator
    ).toThrows()
  }

  @usableFromInline
  func getArray<T, U>(
    _ key: [Key],
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>?
  ) -> DeserializationResult<[U]> {
    getArray(
      key,
      transform: {
        guard let result = transform($0) else {
          return .noValue
        }
        return .success(result)
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
  public func getArray(
    _ key: Key...,
    validator: AnyArrayValueValidator<Any>? = nil
  ) -> DeserializationResult<[Any]> {
    getArray(key, transform: { .success($0) }, validator: validator)
  }

  @inlinable
  public func getArray<T: ValidSerializationValue, U>(
    _ key: Key...,
    transform: (T) -> DeserializationResult<U>,
    validator: AnyArrayValueValidator<U>? = nil
  ) -> DeserializationResult<[U]> {
    getArray(key, transform: transform, validator: validator)
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
  public func getArray<T: ValidSerializationValue, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>? = nil
  ) -> DeserializationResult<[U]> {
    getArray(key, transform: transform, validator: validator)
  }

  @inlinable
  public func getArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T] {
    try getArray(
      key,
      transform: { (obj: Any) -> T? in obj as? T },
      validator: validator
    )
  }

  @inlinable
  public func getArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) -> DeserializationResult<[T]> {
    getArray(
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
  public func getArray<T: Deserializable>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) -> DeserializationResult<[T]> {
    getArray(
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
}
