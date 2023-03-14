import CoreFoundation

import CommonCore
import Serialization

@frozen
public indirect enum Field<T> {
  case value(T)
  case link(Link)
}

extension Field {
  @usableFromInline
  func resolveValue(
    validator: AnyValueValidator<T>? = nil,
    valueForLink: (Link) -> DeserializationResult<T>
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
}

extension Field {
  @inlinable
  public func value(
    validatedBy validator: AnyValueValidator<T>? = nil
  ) -> DeserializationResult<T> {
    resolveValue(
      validator: validator,
      valueForLink: { _ in .noValue }
    )
  }

  public var link: Link? {
    switch self {
    case .value: return nil
    case let .link(link): return link
    }
  }

  @inlinable
  public func resolveValue<U: ValidSerializationValue>(
    context: Context,
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
  public func resolveOptionalValue<U: ValidSerializationValue>(
    context: Context,
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
  public func resolveOptionalValue<U: ValidSerializationValue>(
    context: Context,
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

  @inlinable
  public func resolveValue<U: ValidSerializationValue, C>(
    context: Context,
    transform: (U) -> C?,
    validator: AnyArrayValueValidator<C>
  ) -> DeserializationResult<T> where T == [C] {
    switch self {
    case let .value(value):
      guard validator.isValid(value) != false else {
        return .failure(NonEmptyArray(.invalidValue(result: value, value: nil)))
      }
      return .success(value)
    case let .link(link):
      return safeValueForLink(
        { try context.templateData.getArray($0, transform: transform, validator: validator) },
        link: link
      )
    }
  }
}

extension Field where T == CFString {
  @inlinable
  public func resolveValue(
    context: Context,
    validator: AnyValueValidator<T>? = nil
  ) -> DeserializationResult<CFString> {
    resolveValue(
      validator: validator,
      valueForLink: { link in
        safeValueForLink(
          { try context.templateData.getField($0, validator: validator) },
          link: link
        )
      }
    )
  }
}

extension Field where T: ValidSerializationValue {
  @inlinable
  public func resolveValue(context: Context) -> DeserializationResult<T> {
    resolveValue(context: context, transform: { $0 as T })
  }

  @inlinable
  public func resolveValue(
    context: Context,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveValue(context: context, transform: { $0 as T }, validator: validator)
  }

  @inlinable
  public func resolveOptionalValue(context: Context) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: { $0 as T })
  }

  @inlinable
  public func resolveOptionalValue(
    context: Context,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: { $0 as T }, validator: validator)
  }
}

extension Field where T: RawRepresentable, T.RawValue: ValidSerializationValue {
  @inlinable
  public func resolveValue(context: Context) -> DeserializationResult<T> {
    resolveValue(context: context, transform: T.init(rawValue:))
  }

  @inlinable
  public func resolveValue(
    context: Context,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveValue(context: context, transform: T.init(rawValue:), validator: validator)
  }

  @inlinable
  public func resolveOptionalValue(context: Context) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: T.init(rawValue:))
  }

  @inlinable
  public func resolveOptionalValue(
    context: Context,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> {
    resolveOptionalValue(context: context, transform: T.init(rawValue:), validator: validator)
  }
}

extension Field {
  @inlinable
  public func resolveParent<U: TemplateValue>(
    templates: Templates,
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
  public func tryResolveParent<U: TemplateValue>(templates: Templates) -> Field<[U]>?
    where T == [U] {
    try? resolveParent(templates: templates)
  }
}

extension Field where T: TemplateValue, T: TemplateDeserializable {
  public typealias ResolvedValue = T.ResolvedValue

  @inlinable
  public func resolveParent(templates: Templates) throws -> Field<T> {
    switch self {
    case let .link(link):
      return .link(link)
    case let .value(value):
      return try .value(value.resolveParent(templates: templates))
    }
  }

  @inlinable
  public func resolveValue(
    context: Context,
    useOnlyLinks: Bool
  ) -> DeserializationResult<ResolvedValue> {
    switch self {
    case let .link(link):
      let valueDictResult: DeserializationResult<TemplateData> = safeValueForLink(
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
  public func tryResolveParent(templates: Templates) -> Field<T>? {
    switch self {
    case .link:
      return self
    case let .value(value):
      return value.tryResolveParent(templates: templates).map(Field.value)
    }
  }
}

extension Field where T: Deserializable {
  @inlinable
  public func resolveValue(context: Context) -> DeserializationResult<T> {
    switch self {
    case let .value(value):
      return .success(value)
    case let .link(link):
      return safeValueForLink(
        { try context.templateData.getField($0) },
        link: link
      )
    }
  }
}

extension Field where T: TemplateValue, T: TemplateDeserializable {
  @inlinable
  public func resolveOptionalValue(
    context: Context,
    useOnlyLinks: Bool
  ) -> DeserializationResult<T.ResolvedValue> {
    resolveValue(context: context, useOnlyLinks: useOnlyLinks)
  }

  @inlinable
  public func resolveValue(
    context: Context,
    validator: AnyValueValidator<T.ResolvedValue>,
    useOnlyLinks: Bool
  ) -> DeserializationResult<T.ResolvedValue> {
    let result = resolveValue(context: context, useOnlyLinks: useOnlyLinks)
    guard let value = result.value, validator.isValid(value) else {
      return .failure(NonEmptyArray(.invalidValue(result: result.value, value: nil)))
    }
    return result
  }

  @inlinable
  public func resolveOptionalValue(
    context: Context,
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

  @inlinable
  public func value(
    validatedBy validator: AnyValueValidator<T.ResolvedValue>? = nil,
    templates: Templates,
    templateToType: TemplateToType
  ) -> DeserializationResult<T.ResolvedValue> {
    switch self {
    case let .value(value):
      let context = Context(
        templates: templates,
        templateToType: templateToType,
        templateData: [:]
      )
      let resolvedValue = try? value
        .resolveParent(templates: templates)
        .resolveValue(context: context, useOnlyLinks: true)
      guard let result = resolvedValue,
            let resultValue = result.value,
            validator?.isValid(resultValue) != false else {
        return .failure(NonEmptyArray(.invalidValue(result: resolvedValue?.value, value: nil)))
      }

      return result
    case .link:
      return .noValue
    }
  }
}

extension Field {
  @inlinable
  public func resolveValue<U: TemplateDeserializable & TemplateValue>(
    context: Context,
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
      return safeValueForLink(
        { try context.getArray($0, validator: validator, type: T.Element.self) },
        link: link
      )
    }
  }

  @inlinable
  public func resolveOptionalValue<U: TemplateDeserializable & TemplateValue>(
    context: Context,
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

  @inlinable
  public func value<U: TemplateDeserializable & TemplateValue>(
    validatedBy validator: AnyArrayValueValidator<U.ResolvedValue>? = nil,
    templates: Templates,
    templateToType: TemplateToType
  ) -> DeserializationResult<[U.ResolvedValue]> where T == [U] {
    switch self {
    case let .value(value):
      let context = Context(templates: templates, templateToType: templateToType, templateData: [:])
      let result = try? value
        .resolveParent(templates: templates)
        .resolveValue(context: context, validator: validator)
      return result ?? .failure(NonEmptyArray(.generic))
    case .link:
      return .noValue
    }
  }
}

extension Field {
  @inlinable
  public func resolveValue<U: Deserializable>(context: Context) -> DeserializationResult<T>
    where T == [U] {
    switch self {
    case let .value(value):
      return .success(value)
    case let .link(link):
      return safeValueForLink(
        { try context.templateData.getArray($0) },
        link: link
      )
    }
  }

  @inlinable
  public func resolveValue<U: Deserializable>(
    context: Context,
    validator: AnyValueValidator<T>
  ) -> DeserializationResult<T> where T == [U] {
    let result = resolveValue(context: context)
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

@inlinable
public func safeValueForLink<T>(
  _ valueForLink: (Link) throws -> T,
  link: Link
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
