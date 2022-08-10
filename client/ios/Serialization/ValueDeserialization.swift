import CoreFoundation

import CommonCore

public func deserialize<T: ValidSerializationValue>(
  _ value: Any,
  validator: AnyValueValidator<T>? = nil
) -> DeserializationResult<T> {
  deserialize(value, transform: { $0 }, validator: validator)
}

public func deserialize(
  _ value: Any,
  validator: AnyValueValidator<CFString>? = nil
) -> DeserializationResult<CFString> {
  guard let result: CFString = safeCFCast(value as CFTypeRef) else {
    return .failure(NonEmptyArray(.typeMismatch(expected: "CFString", representation: value)))
  }

  guard validator?.isValid(result) != false else {
    return .failure(NonEmptyArray(.invalidValue(result: result, value: value)))
  }

  return .success(result)
}

public func deserialize<T: RawRepresentable>(
  _ value: Any,
  validator: AnyValueValidator<T>? = nil
) -> DeserializationResult<T> where T.RawValue: ValidSerializationValue {
  deserialize(value, transform: T.init, validator: validator)
}

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
    var errors = NonEmptyArray(.invalidValue(result: result.value, value: value))
    if let resultErrors = result.errorsOrWarnings {
      errors.append(contentsOf: resultErrors)
    }
    return .failure(errors)
  }

  return result
}

public func deserialize<T: ValidSerializationValue>(
  _ value: Any,
  validator: AnyArrayValueValidator<T>? = nil
) -> DeserializationResult<[T]> {
  deserialize(value, transform: { $0 } as ((T) -> T?), validator: validator)
}

public func deserialize<T: RawRepresentable>(
  _ value: Any,
  validator: AnyArrayValueValidator<T>? = nil
) -> DeserializationResult<[T]> where T.RawValue: ValidSerializationValue {
  deserialize(value, transform: T.init, validator: validator)
}

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
  var errors: [Either<DeserializationError, FieldError>] = []
  result.reserveCapacity(resultBeforeTransform.count)

  for index in resultBeforeTransform.indices {
    let transformResult = transform(resultBeforeTransform[index])
    if let resultValue = transformResult.value {
      result.append(resultValue)
    }
    errors.append(contentsOf: transformResult.errorsOrWarnings?.asArray() ?? [])
  }

  if result.count != resultBeforeTransform.count,
     validator?.isPartialDeserializationAllowed == false {
    errors.append(.left(.invalidValue(result: result, value: value)))
    return .failure(NonEmptyArray(errors)!)
  }

  guard validator?.isValid(result) != false else {
    errors.append(.left(.invalidValue(result: result, value: value)))
    return .failure(NonEmptyArray(errors)!)
  }

  return errors.isEmpty
    ? .success(result)
    : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
}
