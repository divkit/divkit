import CoreFoundation

import CommonCorePublic
import Serialization

@frozen
public indirect enum Field<T> {
  case value(T)
  case link(TemplatedPropertyLink)
}

extension Field {
  @usableFromInline
  func resolveValue(
    validator: AnyValueValidator<T>? = nil,
    valueForLink: (TemplatedPropertyLink) -> DeserializationResult<T>
  ) -> DeserializationResult<T> {
    switch self {
    case let .value(value):
      guard validator?.isValid(value) != false else {
        return .failure(NonEmptyArray(.invalidValue(result: value, value: nil)))
      }
      return .success(value)
    case let .link(link):
      return valueForLink(link)
    }
  }

  @inlinable
  func value(
    validatedBy validator: AnyValueValidator<T>? = nil
  ) -> DeserializationResult<T> {
    resolveValue(
      validator: validator,
      valueForLink: { _ in .noValue }
    )
  }

  var link: TemplatedPropertyLink? {
    switch self {
    case .value: return nil
    case let .link(link): return link
    }
  }

  @inlinable
  func resolveValue<U: ValidSerializationValue>(
    context: TemplatesContext,
    transform: (U) -> T?,
    validator: AnyValueValidator<T>? = nil
  ) -> DeserializationResult<T> {
    resolveValue(
      validator: validator,
      valueForLink: {
        safeValueForLink(
          { try context.templateData.getField($0, transform: transform, validator: validator) },
          link: $0
        )
      }
    )
  }

  @inlinable
  func resolveOptionalValue<U: ValidSerializationValue>(
    context: TemplatesContext,
    transform: (U) -> T?
  ) -> DeserializationResult<T> {
    resolveValue(
      valueForLink: {
        let result = safeValueForLink(
          { try context.templateData.getField($0, transform: transform) },
          link: $0
        )
        if case let .failure(errors) = result {
          if errors.count == 1, case .noData = errors.first {
            return .noValue
          }
        }
        return result
      }
    )
  }

  @inlinable
  func resolveOptionalValue<U: ValidSerializationValue>(
    context: TemplatesContext,
    transform: (U) -> T?,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    let result = resolveOptionalValue(context: context, transform: transform)
    if case .noValue = result {
      return .noValue
    }
    guard let resultValue = result.value, validator.isValid(resultValue) else {
      var errors: NonEmptyArray<DeserializationError> =
        NonEmptyArray(.invalidValue(result: result.value, value: nil))
      if let resultErrors = result.errorsOrWarnings {
        errors.append(contentsOf: resultErrors)
      }
      return .failure(errors)
    }
    return result
  }
}

extension Field where T: ValidSerializationValue {
  @inlinable
  func resolveValue(context: TemplatesContext) -> DeserializationResult<T> {
    resolveValue(context: context, transform: { $0 as T })
  }

  @inlinable
  func resolveValue(
    context: TemplatesContext,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveValue(context: context, transform: { $0 as T }, validator: validator)
  }

  @inlinable
  func resolveOptionalValue(context: TemplatesContext) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: { $0 as T })
  }

  @inlinable
  func resolveOptionalValue(
    context: TemplatesContext,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: { $0 as T }, validator: validator)
  }
}

extension Field where T: RawRepresentable, T.RawValue: ValidSerializationValue {
  @inlinable
  func resolveOptionalValue(
    context: TemplatesContext,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: T.init(rawValue:), validator: validator)
  }
}

extension Field {
  @inlinable
  func resolveParent<U: TemplateValue>(
    templates: [TemplateName: Any],
    validator: AnyArrayValueValidator<U>? = nil
  ) throws -> Field<[U]> where T == [U] {
    switch self {
    case .link:
      return self
    case let .value(value):
      var result: [U] = []
      result.reserveCapacity(value.count)
      for index in value.indices {
        do {
          result.append(try value[index].resolveParent(templates: templates))
        } catch {
          if validator?.isPartialDeserializationAllowed == false {
            throw error
          }
        }
      }
      if result.count != value.count,
         validator?.isPartialDeserializationAllowed == false {
        throw DeserializationError.invalidValue(
          result: result,
          value: value
        )
      }
      return .value(result)
    }
  }

  @inlinable
  func tryResolveParent<U: TemplateValue>(templates: [TemplateName: Any]) -> Field<[U]>?
    where T == [U] {
    try? resolveParent(templates: templates)
  }
}

extension Field where T: TemplateValue {
  @usableFromInline
  typealias ResolvedValue = T.ResolvedValue

  @inlinable
  func resolveParent(templates: [TemplateName: Any]) throws -> Field<T> {
    switch self {
    case let .link(link):
      return .link(link)
    case let .value(value):
      return try .value(value.resolveParent(templates: templates))
    }
  }

  @inlinable
  func resolveValue(
    context: TemplatesContext,
    useOnlyLinks: Bool
  ) -> DeserializationResult<ResolvedValue> {
    switch self {
    case let .link(link):
      let valueDictResult: DeserializationResult<[String: Any]> = safeValueForLink(
        { try context.templateData.getField($0) },
        link: link
      )
      guard let valueDict = valueDictResult.value else {
        if let errorsOrWarnings = valueDictResult.errorsOrWarnings {
          return .failure(errorsOrWarnings)
        }
        return .noValue
      }
      return T.resolveValue(
        context: modified(context) { $0.templateData = valueDict },
        useOnlyLinks: false
      )
    case let .value(value):
      return value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
    }
  }

  @inlinable
  func tryResolveParent(templates: [TemplateName: Any]) -> Field<T>? {
    switch self {
    case .link:
      return self
    case let .value(value):
      return value.tryResolveParent(templates: templates).map(Field.value)
    }
  }

  @inlinable
  func resolveOptionalValue(
    context: TemplatesContext,
    useOnlyLinks: Bool
  ) -> DeserializationResult<T.ResolvedValue> {
    resolveValue(context: context, useOnlyLinks: useOnlyLinks)
  }

  @inlinable
  func resolveOptionalValue(
    context: TemplatesContext,
    validator: AnyValueValidator<T.ResolvedValue>,
    useOnlyLinks: Bool
  ) -> DeserializationResult<T.ResolvedValue> {
    let result = resolveValue(context: context, useOnlyLinks: useOnlyLinks)
    guard let value = result.value else {
      return .noValue
    }
    guard validator.isValid(value) else {
      return .failure(NonEmptyArray(.invalidValue(result: result.value, value: nil)))
    }
    return result
  }
}

extension Field {
  @inlinable
  func resolveValue<U: TemplateValue>(
    context: TemplatesContext,
    validator: AnyArrayValueValidator<U.ResolvedValue>?,
    useOnlyLinks _: Bool
    // swiftformat:disable:next typeSugar
  ) -> DeserializationResult<Array<U>.ResolvedValue> where Array<U> == T {
    switch self {
    case let .value(v):
      let result = try? v
        .resolveParent(templates: context.templates, validator: validator)
        .resolveValue(context: context, validator: validator)
      return result ?? .failure(NonEmptyArray(.generic))
    case let .link(link):
      return context.getArray(link, validator: validator, type: T.Element.self)
    }
  }

  @inlinable
  func resolveOptionalValue<U: TemplateValue>(
    context: TemplatesContext,
    validator: AnyArrayValueValidator<U.ResolvedValue>? = nil,
    useOnlyLinks: Bool
    // swiftformat:disable:next typeSugar
  ) -> DeserializationResult<Array<U>.ResolvedValue> where Array<U> == T {
    let result = resolveValue(context: context, validator: validator, useOnlyLinks: useOnlyLinks)
    if case let .failure(errors) = result, errors.count == 1, case .noData = errors.first {
      return .noValue
    }
    return result
  }
}

@inlinable
func safeValueForLink<T>(
  _ valueForLink: (TemplatedPropertyLink) throws -> T,
  link: TemplatedPropertyLink
) -> DeserializationResult<T> {
  do {
    return try .success(valueForLink(link))
  } catch let error as DeserializationError {
    return .failure(NonEmptyArray(error))
  } catch {
    assertionFailure("Closure should throw only DeserializationError")
    return .failure(NonEmptyArray(.unexpectedError(message: error.localizedDescription)))
  }
}
