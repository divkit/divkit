import CoreFoundation

import CommonCorePublic
import Serialization

extension Field {
  @inlinable
  static func makeOptional(
    valueGetter: @autoclosure () -> T?,
    linkGetter: @autoclosure () -> TemplatedPropertyLink?
  ) -> Field<T>? {
    if let value = valueGetter() {
      return .value(value)
    } else if let link = linkGetter() {
      return .link(link)
    } else {
      return nil
    }
  }
}

/// Deserialization for Field<T>?
extension Dictionary where Key == String, Value == Any {
  @inlinable
  func link(for key: String) -> TemplatedPropertyLink? {
    self["$" + key] as? TemplatedPropertyLink
  }

  @inlinable
  func getOptionalField<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyValueValidator<T>? = nil
  ) throws -> Field<T>? {
    Field.makeOptional(
      valueGetter: (try? self.getOptionalField(key, transform: transform, validator: validator))
        .flatMap { $0 },
      linkGetter: link(for: key)
    )
  }

  @inlinable
  func getOptionalField<T: RawRepresentable>(_ key: String) throws -> Field<T>? {
    try getOptionalField(
      key,
      transform: T.init(rawValue:)
    )
  }

  @inlinable
  func getOptionalField<T: ValidSerializationValue>(_ key: String) throws -> Field<T>? {
    try getOptionalField(
      key,
      transform: { $0 as T }
    )
  }

  @inlinable
  func getOptionalField<T: TemplateValue>(
    _ key: String,
    templateToType: [TemplateName: String],
    validator: AnyValueValidator<T>? = nil
  ) throws -> Field<T>? {
    try getOptionalField(
      key,
      transform: { (dict: Self) in try? T(dictionary: dict, templateToType: templateToType) },
      validator: validator
    )
  }
}

/// Deserializaton for Field<[T]> and [T]
extension Dictionary where Key == String, Value == Any {
  @inlinable
  func getOptionalArray<T, U>(
    _ key: String,
    transform: (U) throws -> T,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> Field<[T]>? {
    if let value: [T] = (try? getOptionalArray(
      key,
      transform: transform,
      validator: validator
    )).flatMap({ $0 }) {
      return .value(value)
    }

    return link(for: key).map { .link($0) }
  }

  @inlinable
  func getOptionalArray<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> Field<[T]>? {
    try getOptionalArray(key, transform: { (value: U) throws -> T in
      guard let result = transform(value) else {
        throw DeserializationError.generic
      }
      return result
    }, validator: validator)
  }

  @inlinable
  func getOptionalArray<T: RawRepresentable>(_ key: String) throws -> Field<[T]>? {
    try getOptionalArray(
      key,
      transform: T.init(rawValue:)
    )
  }

  @inlinable
  func getOptionalArray<T: TemplateValue>(
    _ key: String,
    templateToType: [TemplateName: String],
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> Field<[T]>? {
    try getOptionalArray(
      key,
      transform: { (dict: Self) in try? T(dictionary: dict, templateToType: templateToType) },
      validator: validator
    )
  }
}

extension Dictionary where Key == String, Value == Any {
  @inlinable
  func getField<T: TemplateValue>(
    _ key: String,
    templateToType: [TemplateName: String],
    validator: AnyValueValidator<T>? = nil
  ) throws -> T {
    try getField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict, templateToType: templateToType) },
      validator: validator
    )
  }
}

extension TemplatesContext {
  @usableFromInline
  func deserializeTemplate<T: TemplateValue>(
    _ dict: [String: Any],
    type: T.Type
  ) -> DeserializationResult<T.ResolvedValue> {
    let deserializer = makeTemplateDeserializer(
      templates: templates,
      templateToType: templateToType,
      type: type
    )
    return deserializer(dict)
  }

  @inlinable
  func getArray<T: TemplateValue>(
    _ key: String,
    validator: AnyArrayValueValidator<T.ResolvedValue>? = nil,
    type: T.Type
  ) throws -> [T.ResolvedValue] {
    try templateData.getArray(
      key,
      transform: { deserializeTemplate($0, type: type).value },
      validator: validator
    )
  }

  @inlinable
  func getArray<T: TemplateValue>(
    _ key: String,
    validator: AnyArrayValueValidator<T.ResolvedValue>? = nil,
    type: T.Type
  ) -> DeserializationResult<[T.ResolvedValue]> {
    templateData.getArray(
      key,
      transform: { deserializeTemplate($0, type: type) },
      validator: validator
    )
  }
}

@inlinable
func deserialize<T: TemplateValue>(
  _ value: Any,
  templates: [TemplateName: Any],
  templateToType: [TemplateName: String],
  validator: AnyValueValidator<T.ResolvedValue>? = nil,
  type: T.Type
) -> DeserializationResult<T.ResolvedValue> {
  deserialize(
    value,
    transform: makeTemplateDeserializer(
      templates: templates,
      templateToType: templateToType,
      type: type
    ),
    validator: validator
  )
}

@inlinable
func deserialize<T: TemplateValue>(
  _ value: Any,
  templates: [TemplateName: Any],
  templateToType: [TemplateName: String],
  validator: AnyArrayValueValidator<T.ResolvedValue>? = nil,
  type: T.Type
) -> DeserializationResult<[T.ResolvedValue]> {
  deserialize(
    value,
    transform: makeTemplateDeserializer(
      templates: templates,
      templateToType: templateToType,
      type: type
    ),
    validator: validator
  )
}

@usableFromInline
func makeTemplateDeserializer<T: TemplateValue>(
  templates: [TemplateName: Any],
  templateToType: [TemplateName: String],
  type _: T.Type
) -> (([String: Any]) -> DeserializationResult<T.ResolvedValue>) {
  return { dict in
    let context = TemplatesContext(
      templates: templates,
      templateToType: templateToType,
      templateData: dict
    )
    return T.resolveValue(context: context, useOnlyLinks: false)
  }
}
