import CoreFoundation
import Foundation
import VGSL

// MARK: Utils

@usableFromInline
func invalidFieldErrorForKey(
  _ key: [some Any],
  element: Int? = nil,
  representation: (some Any)?
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

@usableFromInline
func unwrapOptionalTransformedValue<T, U>(
  _ value: T,
  key: [some Any],
  transform: (T) -> U?
) throws -> U {
  guard let transformed = transform(value) else {
    throw invalidFieldErrorForKey(key, representation: value)
  }
  return transformed
}

@usableFromInline
func rawRepresentableValue<T: RawRepresentable>(
  _ raw: T.RawValue,
  key: [some Any]
) throws -> T {
  guard let value = T(rawValue: raw) else {
    throw invalidFieldErrorForKey(key, representation: raw)
  }
  return value
}

@usableFromInline
func castValidSerializationValue<T: ValidSerializationValue>(
  _ value: Any,
  key: [some Any]
) throws -> T {
  guard let transformed = value as? T else {
    throw invalidFieldErrorForKey(key, representation: value)
  }
  return transformed
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
    try getArray(key, transform: transform, validator: validator).unwrap()
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
    if case let .failure(dictErrors) = dictResult {
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
    }

    if validator?.isValid(result) == false {
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
  func getOptionalFieldCore<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>?,
    invalidValueHandler: ((Any) -> Void)?
  ) throws -> U? {
    guard let dict = try? enclosedDictForKeySequence(key), let value = dict[key.last!] else {
      return nil
    }

    guard let valueBeforeConversion = value as? T else {
      invalidValueHandler?(value)
      return nil
    }

    guard let result = try? transform(valueBeforeConversion),
          validator?.isValid(result) != false else {
      invalidValueHandler?(valueBeforeConversion)
      return nil
    }

    return result
  }

  @usableFromInline
  func getOptionalField<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>?
  ) throws -> U? {
    try getOptionalFieldCore(
      key,
      transform: transform,
      validator: validator,
      invalidValueHandler: nil
    )
  }

  @usableFromInline
  func getOptionalArrayCore<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>?,
    invalidContainerHandler: ((Any) -> Void)?,
    invalidElementHandler: ((Int, Any) throws -> Void)?,
    invalidTransformedElementHandler: ((Int, Any) -> Void)?
  ) throws -> [U]? {
    guard let dict = try? enclosedDictForKeySequence(key), let value = dict[key.last!] else {
      return nil
    }

    guard let valueBeforeConversion = value as? NSArray else {
      invalidContainerHandler?(value)
      return nil
    }

    var result: [U] = []
    result.reserveCapacity(valueBeforeConversion.count)
    for (index, element) in valueBeforeConversion.enumerated() {
      guard let typedElement = element as? T else {
        try invalidElementHandler?(index, element)
        continue
      }

      guard let transformed = try? transform(typedElement) else {
        invalidTransformedElementHandler?(index, element)
        continue
      }

      result.append(transformed)
    }

    if validator?.isValid(result) == false {
      invalidContainerHandler?(valueBeforeConversion)
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
    try getOptionalArrayCore(
      key,
      transform: transform,
      validator: validator,
      invalidContainerHandler: nil,
      invalidElementHandler: { index, element in
        throw invalidFieldErrorForKey(key, element: index, representation: element)
      },
      invalidTransformedElementHandler: nil
    )
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
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
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
    try getArray(key, transform: transform, validator: validator).unwrap()
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
    try getOptionalField(
      key,
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
      },
      validator: validator
    )
  }

  @usableFromInline
  func getOptionalArray<T, U>(
    _ key: [Key],
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>?
  ) throws -> [U]? {
    try getOptionalArray(
      key,
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
      },
      validator: validator
    )
  }
}

// MARK: Required values (public interface)

extension Dictionary where Key == String {
  @usableFromInline
  func getFieldWithContext<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> U {
    let result: DeserializationResult<U> = getResult {
      try getField(key, transform: transform, validator: validator)
    }
    context.append(result: result)
    return try result.unwrap()
  }

  @usableFromInline
  func getArrayWithContext<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> [U] {
    let result: DeserializationResult<[U]> = getResult {
      try getArray(key, transform: transform, validator: validator)
    }
    context.append(result: result)
    return try result.unwrap()
  }

  @usableFromInline
  func getOptionalFieldWithContext<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> U? {
    try getOptionalFieldCore(
      key,
      transform: transform,
      validator: validator,
      invalidValueHandler: { value in
        context.appendWarning(invalidFieldErrorForKey(key, representation: value))
      }
    )
  }

  @usableFromInline
  func getOptionalArrayWithContext<T, U>(
    _ key: [Key],
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> [U]? {
    try getOptionalArrayCore(
      key,
      transform: transform,
      validator: validator,
      invalidContainerHandler: { value in
        context.appendWarning(invalidFieldErrorForKey(key, representation: value))
      },
      invalidElementHandler: { index, element in
        context.appendWarning(invalidFieldErrorForKey(key, element: index, representation: element))
      },
      invalidTransformedElementHandler: { index, element in
        context.appendWarning(invalidFieldErrorForKey(key, element: index, representation: element))
      }
    )
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
    try getField(
      key,
      transform: { raw in
        try rawRepresentableValue(raw, key: key)
      },
      validator: validator
    )
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
  public func getArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> [T] {
    try getArray(
      key,
      transform: { (obj: Any) -> T? in
        try? castValidSerializationValue(obj, key: key)
      },
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
    (try? self.getField(key, transform: { ($0 as NSString).boolValue }, validator: nil))
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
    try getOptionalField(
      key,
      transform: { raw in
        try rawRepresentableValue(raw, key: key)
      },
      validator: validator
    )
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
      transform: { (obj: Any) -> T? in
        (obj as? T.RawValue).flatMap { try? rawRepresentableValue($0, key: key) }
      },
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

// MARK: Context-aware values (public interface)

extension Dictionary where Key == String {
  @inlinable
  public func getField<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> T {
    try getFieldWithContext(
      key,
      transform: { value in
        try castValidSerializationValue(value, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getField<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> T {
    try getFieldWithContext(
      key,
      transform: { raw in
        try rawRepresentableValue(raw, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getField<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> U {
    try getFieldWithContext(key, transform: transform, validator: validator, context: context)
  }

  @inlinable
  public func getField<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> U {
    try getFieldWithContext(
      key,
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> [T] {
    try getArrayWithContext(
      key,
      transform: { value in
        try castValidSerializationValue(value, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getArray<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> [T] {
    try getArrayWithContext(
      key,
      transform: { (obj: Any) throws -> T in
        guard let rawValue = obj as? T.RawValue else {
          throw invalidFieldErrorForKey(key, representation: obj)
        }
        return try rawRepresentableValue(rawValue, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getArray<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> [U] {
    try getArrayWithContext(key, transform: transform, validator: validator, context: context)
  }

  @inlinable
  public func getArray<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> [U] {
    try getArrayWithContext(
      key,
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalField<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> T? {
    try getOptionalFieldWithContext(
      key,
      transform: { value in
        try castValidSerializationValue(value, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalField<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> T? {
    try getOptionalFieldWithContext(
      key,
      transform: { raw in
        try rawRepresentableValue(raw, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalField<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> U? {
    try getOptionalFieldWithContext(
      key,
      transform: transform,
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalField<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> U? {
    try getOptionalFieldWithContext(
      key,
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalArray<T: ValidSerializationValue>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> [T]? {
    try getOptionalArrayWithContext(
      key,
      transform: { value in
        try castValidSerializationValue(value, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalArray<T: RawRepresentable>(
    _ key: Key...,
    validator: AnyArrayValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> [T]? {
    try getOptionalArrayWithContext(
      key,
      transform: { (obj: Any) throws -> T in
        guard let rawValue = obj as? T.RawValue else {
          throw invalidFieldErrorForKey(key, representation: obj)
        }
        return try rawRepresentableValue(rawValue, key: key)
      },
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalArray<T, U>(
    _ key: Key...,
    transform: (T) throws -> U,
    validator: AnyArrayValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> [U]? {
    try getOptionalArrayWithContext(
      key,
      transform: transform,
      validator: validator,
      context: context
    )
  }

  @inlinable
  public func getOptionalArray<T, U>(
    _ key: Key...,
    transform: (T) -> U?,
    validator: AnyArrayValueValidator<U>? = nil,
    context: ParsingContext
  ) throws -> [U]? {
    try getOptionalArrayWithContext(
      key,
      transform: { value in
        try unwrapOptionalTransformedValue(value, key: key, transform: transform)
      },
      validator: validator,
      context: context
    )
  }
}
