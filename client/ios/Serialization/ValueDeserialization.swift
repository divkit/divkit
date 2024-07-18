import CoreFoundation

import VGSL

@inlinable
public func deserialize<T: ValidSerializationValue>(
  _ value: Any,
  validator: AnyValueValidator<T>? = nil
) -> DeserializationResult<T> {
  deserialize(value, transform: { $0 }, validator: validator)
}

@inlinable
public func deserialize<T: RawRepresentable>(
  _ value: Any,
  validator: AnyValueValidator<T>? = nil
) -> DeserializationResult<T> where T.RawValue: ValidSerializationValue {
  deserialize(value, transform: T.init, validator: validator)
}

@inlinable
public func deserialize<T: ValidSerializationValue, U>(
  _ value: Any,
  transform: (T) -> U?,
  validator: AnyValueValidator<U>? = nil
) -> DeserializationResult<U> {
  let transformWithResult: (T) -> DeserializationResult<U> = {
    guard let transformed = transform($0) else {
      return .failure(NonEmptyArray(.invalidValue(result: nil, value: value)))
    }
    return .success(transformed)
  }
  return deserialize(value, transform: transformWithResult, validator: validator)
}

@inlinable
public func deserialize<T: ValidSerializationValue, U>(
  _ value: Any,
  transform: (T) -> DeserializationResult<U>,
  validator: AnyValueValidator<U>? = nil
) -> DeserializationResult<U> {
  guard let typedValue = value as? T else {
    return .failure(NonEmptyArray(.typeMismatch(
      expected: String(describing: T.self),
      representation: value
    )))
  }

  let result = transform(typedValue)
  guard let resultValue = result.value, validator?.isValid(resultValue) != false else {
    if let resultErrors = result.errorsOrWarnings {
      return .failure(NonEmptyArray(.composite(
        error: .invalidValue(result: result.value, from: value),
        causes: resultErrors
      )))
    }
    return .failure(NonEmptyArray(.invalidValue(result: result.value, value: value)))
  }
  return result
}

@inlinable
public func deserialize<T: ValidSerializationValue>(
  _ value: Any,
  validator: AnyArrayValueValidator<T>? = nil
) -> DeserializationResult<[T]> {
  deserialize(value, transform: { $0 } as ((T) -> T?), validator: validator)
}

@inlinable
public func deserialize<T: RawRepresentable>(
  _ value: Any,
  validator: AnyArrayValueValidator<T>? = nil
) -> DeserializationResult<[T]> where T.RawValue: ValidSerializationValue {
  deserialize(value, transform: T.init, validator: validator)
}

@inlinable
public func deserialize<T: ValidSerializationValue, U>(
  _ value: Any,
  transform: (T) -> U?,
  validator: AnyArrayValueValidator<U>? = nil
) -> DeserializationResult<[U]> {
  let transformWithResult: (T) -> DeserializationResult<U> = {
    guard let transformed = transform($0) else {
      return .failure(NonEmptyArray(.invalidValue(result: nil, value: value)))
    }
    return .success(transformed)
  }
  return deserialize(value, transform: transformWithResult, validator: validator)
}

@inlinable
public func deserialize<T: ValidSerializationValue, U>(
  _ value: Any,
  transform: (T) -> DeserializationResult<U>,
  validator: AnyArrayValueValidator<U>? = nil
) -> DeserializationResult<[U]> {
  guard let resultBeforeTransform = (value as? [T]) else {
    return .failure(NonEmptyArray(.typeMismatch(
      expected: String(describing: [T].self),
      representation: value
    )))
  }

  var result: [U] = []
  var errors: [DeserializationError] = []
  result.reserveCapacity(resultBeforeTransform.count)

  for index in resultBeforeTransform.indices {
    let transformResult = transform(resultBeforeTransform[index])
    if let resultValue = transformResult.value {
      result.append(resultValue)
    }
    errors.append(
      contentsOf: (transformResult.errorsOrWarnings?.asArray() ?? [])
        .map { .nestedObjectError(field: "\(index)", error: $0) }
    )
  }

  guard validator?.isValid(result) != false else {
    if let errors = NonEmptyArray(errors) {
      return .failure(NonEmptyArray(.composite(
        error: .invalidValue(result: result, from: value),
        causes: errors
      )))
    } else {
      return .failure(NonEmptyArray(.invalidValue(result: result, value: value)))
    }
  }

  return errors.isEmpty
    ? .success(result)
    : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
}
