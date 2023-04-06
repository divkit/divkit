import Foundation

import CommonCorePublic
import Serialization

extension Field {
  @inlinable
  func resolveValue<E: ValidSerializationValue>(
    context: TemplatesContext
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveValue(
      context: context,
      transform: { $0 as E }
    )
  }

  @inlinable
  func resolveValue<E: ValidSerializationValue>(
    context: TemplatesContext,
    validator: AnyValueValidator<E>
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
    resolveExpression(
      valueForLink: { safeValueForLink(
        { try context.templateData.getField(
          $0,
          transform: { expressionTransform(
            $0,
            transform: E.init(rawValue:),
            validator: validator?.isValid
          ) },
          validator: nil
        ) },
        link: $0
      ) }
    )
  }

  @inlinable
  func resolveValue<U: ValidSerializationValue, E>(
    context: TemplatesContext,
    transform: (U) -> E?,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveExpression(
      valueForLink: { safeValueForLink(
        { try context.templateData.getField(
          $0,
          transform: { expressionTransform(
            $0,
            transform: transform,
            validator: validator?.isValid
          ) },
          validator: nil
        ) },
        link: $0
      ) }
    )
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
          expressionTransform(value, transform: transform, validator: nil)
        },
        validator: validator
      )
    }
  }

  @inlinable
  func resolveOptionalValue<E: ValidSerializationValue>(
    context: TemplatesContext
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveOptionalValue(
      context: context,
      transform: { $0 as E }
    )
  }

  @inlinable
  func resolveOptionalValue<E: ValidSerializationValue>(
    context: TemplatesContext,
    validator: AnyValueValidator<E>
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

  @usableFromInline
  func resolveExpression<E>(
    valueForLink: (TemplatedPropertyLink) -> DeserializationResult<T>
  ) -> DeserializationResult<T> where T == Expression<E> {
    switch self {
    case let .value(value):
      return .success(value)
    case let .link(link):
      return valueForLink(link)
    }
  }
}

extension Field where T == Expression<CFString> {
  func resolveValue(
    context: TemplatesContext,
    validator: AnyValueValidator<CFString>? = nil
  ) -> DeserializationResult<T> {
    resolveExpression(
      valueForLink: { safeValueForLink(
        { try context.templateData.getField(
          $0,
          transform: { expressionTransform(
            $0,
            transform: { (rawValue: String) in safeCFCast(rawValue as CFTypeRef) },
            validator: validator?.isValid
          ) },
          validator: nil
        ) },
        link: $0
      ) }
    )
  }
}
