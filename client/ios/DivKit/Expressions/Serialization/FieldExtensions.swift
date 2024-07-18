import Foundation

import Serialization
import VGSL

extension Field {
  @inlinable
  func resolveValue<E: ValidSerializationValue>(
    context: TemplatesContext,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveValue(
      context: context,
      transform: { $0 as E },
      validator: validator
    )
  }

  @inlinable
  func resolveValue<E: RawRepresentable>(
    context: TemplatesContext,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E>, E.RawValue == String {
    switch self {
    case let .value(value):
      return .success(value)
    case let .link(link):
      return safeValueForLink {
        try context.templateData.getField(
          link,
          transform: {
            expressionTransform($0, transform: E.init(rawValue:), validator: validator)
          }
        )
      }
    }
  }

  @inlinable
  func resolveValue<U: ValidSerializationValue, E>(
    context: TemplatesContext,
    transform: (U) -> E?,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E> {
    switch self {
    case let .value(value):
      return .success(value)
    case let .link(link):
      return safeValueForLink {
        try context.templateData.getField(
          link,
          transform: { expressionTransform($0, transform: transform, validator: validator) }
        )
      }
    }
  }

  @inlinable
  func resolveValue<U: ValidSerializationValue, E>(
    context: TemplatesContext,
    transform: (U) -> E?,
    validator: AnyArrayValueValidator<Expression<E>>
  ) -> DeserializationResult<T> where T == [Expression<E>] {
    switch self {
    case let .value(value):
      guard validator.isValid(value) != false else {
        return .failure(NonEmptyArray(.invalidValue(result: value, value: nil)))
      }
      return .success(value)
    case let .link(link):
      return context.templateData.getArray(
        link,
        transform: { (value: U) in
          expressionTransform(value, transform: transform)
        },
        validator: validator
      )
    }
  }

  @inlinable
  func resolveOptionalValue<E: ValidSerializationValue>(
    context: TemplatesContext,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveOptionalValue(
      context: context,
      transform: { $0 as E },
      validator: validator
    )
  }

  @inlinable
  func resolveOptionalValue<E: RawRepresentable>(
    context: TemplatesContext,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E>, E.RawValue == String {
    let result = resolveValue(
      context: context,
      validator: validator
    )
    if case let .failure(errors) = result,
       errors.count == 1,
       case .noData = errors.first {
      return .noValue
    }
    return result
  }

  @inlinable
  func resolveOptionalValue<U: ValidSerializationValue, E>(
    context: TemplatesContext,
    transform: (U) -> E?,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E> {
    let result = resolveValue(
      context: context,
      transform: transform,
      validator: validator
    )
    if case let .failure(errors) = result,
       errors.count == 1,
       case .noData = errors.first {
      return .noValue
    }
    return result
  }
}
