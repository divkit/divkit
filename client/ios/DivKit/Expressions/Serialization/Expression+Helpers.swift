import Foundation

import CommonCorePublic
import Serialization

@inlinable
func expressionTransform<T, U>(
  _ value: Any?,
  transform: (U) -> T?,
  validator: AnyValueValidator<T>? = nil
) -> Expression<T>? {
  do {
    if let rawValue = value as? String,
       !rawValue.isEmpty,
       let resolver = try ExpressionLink<T>(
         rawValue: rawValue,
         validator: validator,
         errorTracker: { DivKitLogger.error($0.description) }
       ) {
      return .link(resolver)
    }
  } catch {
    return nil
  }

  guard let value = value else {
    return nil
  }

  guard let value = value as? U else {
    DivKitLogger.error("Failed to cast value: \(value)")
    return nil
  }

  if let transformedValue = transform(value), validator?.isValid(transformedValue) != false {
    return .value(transformedValue)
  }

  return nil
}

@inlinable
func deserialize<T: ValidSerializationValue>(
  _ value: Any,
  validator: AnyValueValidator<T>? = nil
) -> DeserializationResult<Expression<T>> {
  deserialize(value, transform: { $0 }, validator: validator)
}

@inlinable
func deserialize<T: ValidSerializationValue, U>(
  _ value: Any,
  transform: (T) -> U?,
  validator: AnyValueValidator<U>? = nil
) -> DeserializationResult<Expression<U>> {
  guard let result: Expression<U> = expressionTransform(
    value,
    transform: transform,
    validator: validator
  ) else {
    return .failure(NonEmptyArray(.typeMismatch(
      expected: "Expression<\(U.self)>",
      representation: value
    )))
  }

  return .success(result)
}

@inlinable
func deserialize<T: ValidSerializationValue, U>(
  _ value: Any,
  transform: (T) -> U?,
  validator: AnyArrayValueValidator<Expression<U>>? = nil
) -> DeserializationResult<[Expression<U>]> {
  deserialize(
    value,
    transform: { (rawElement: T) -> DeserializationResult<Expression<U>> in
      deserialize(rawElement, transform: transform)
    },
    validator: validator
  )
}

@inlinable
func deserialize<T: RawRepresentable>(
  _ value: Any,
  validator: AnyValueValidator<T>? = nil
) -> DeserializationResult<Expression<T>> where T.RawValue == String {
  guard let result: Expression<T> = expressionTransform(
    value,
    transform: T.init(rawValue:),
    validator: validator
  ) else {
    return .failure(NonEmptyArray(.typeMismatch(
      expected: "Expression<\(T.self)>",
      representation: value
    )))
  }

  if case let .value(value) = result, validator?.isValid(value) == false {
    return .failure(NonEmptyArray(.invalidValue(result: result, value: value)))
  }

  return .success(result)
}
