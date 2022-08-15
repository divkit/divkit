import Foundation

import CommonCore
import Serialization
import TemplatesSupport

extension Field {
  public func resolveValue<E: ValidSerializationValue>(
    context: Context
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveValue(
      context: context,
      transform: { $0 as E }
    )
  }

  public func resolveValue<E: ValidSerializationValue>(
    context: Context,
    validator: AnyValueValidator<E>
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveValue(
      context: context,
      transform: { $0 as E },
      validator: validator
    )
  }

  public func resolveValue<E: RawRepresentable>(
    context: Context,
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

  public func resolveValue<U: ValidSerializationValue, E>(
    context: Context,
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

  public func resolveValue<U: ValidSerializationValue, E>(
    context: Context,
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
      return safeValueForLink(
        { try context.templateData.getArray(
          $0,
          transform: { (value: U) in
            expressionTransform(value, transform: transform, validator: nil)
          },
          validator: validator
        ) },
        link: link
      )
    }
  }

  public func resolveOptionalValue<E: ValidSerializationValue>(
    context: Context
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveOptionalValue(
      context: context,
      transform: { $0 as E }
    )
  }

  public func resolveOptionalValue<E: ValidSerializationValue>(
    context: Context,
    validator: AnyValueValidator<E>
  ) -> DeserializationResult<T> where T == Expression<E> {
    resolveOptionalValue(
      context: context,
      transform: { $0 as E },
      validator: validator
    )
  }

  public func resolveOptionalValue<E: RawRepresentable>(
    context: Context,
    validator: AnyValueValidator<E>? = nil
  ) -> DeserializationResult<T> where T == Expression<E>, E.RawValue == String {
    let result = resolveValue(
      context: context,
      validator: validator
    )
    if case let .failure(errors) = result,
       errors.count == 1,
       case .left(.noData) = errors.first {
      return .noValue
    }
    return result
  }

  public func resolveOptionalValue<U: ValidSerializationValue, E>(
    context: Context,
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
       case .left(.noData) = errors.first {
      return .noValue
    }
    return result
  }

  fileprivate func resolveExpression<E>(
    valueForLink: (Link) -> DeserializationResult<T>
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
  public func resolveValue(
    context: Context,
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
