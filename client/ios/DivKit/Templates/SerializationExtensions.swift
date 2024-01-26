import CoreFoundation

import CommonCorePublic
import Serialization

/// Deserialization for Field<T>?
extension [String: Any] {
  @inlinable
  func link(for key: String) -> String? {
    self["$" + key] as? String
  }

  @inlinable
  func getOptionalField<T, U>(
    _ key: String,
    transform: (U) -> T?
  ) -> Field<T>? {
    if let value: T = try? getOptionalField(key, transform: transform) {
      return .value(value)
    }
    if let link = link(for: key) {
      return .link(link)
    }
    return nil
  }

  @inlinable
  func getOptionalField<T: RawRepresentable>(_ key: String) -> Field<T>? {
    getOptionalField(key, transform: T.init(rawValue:))
  }

  @inlinable
  func getOptionalField<T: ValidSerializationValue>(_ key: String) -> Field<T>? {
    getOptionalField(key, transform: { $0 as T })
  }

  @inlinable
  func getOptionalField<T: TemplateValue>(
    _ key: String,
    templateToType: [TemplateName: String]
  ) -> Field<T>? {
    getOptionalField(
      key,
      transform: { (dict: Self) in try? T(dictionary: dict, templateToType: templateToType) }
    )
  }
}

/// Deserializaton for Field<[T]> and [T]
extension [String: Any] {
  @inlinable
  func getOptionalArray<T, U>(
    _ key: String,
    transform: (U) throws -> T,
    validator: AnyArrayValueValidator<T>? = nil
  ) -> Field<[T]>? {
    if let value: [T] = try? getOptionalArray(
      key,
      transform: transform,
      validator: validator
    ) {
      return .value(value)
    }
    return link(for: key).map { .link($0) }
  }

  @inlinable
  func getOptionalArray<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyArrayValueValidator<T>? = nil
  ) -> Field<[T]>? {
    getOptionalArray(
      key,
      transform: { (value: U) throws -> T in
        guard let result = transform(value) else {
          throw DeserializationError.generic
        }
        return result
      },
      validator: validator
    )
  }

  @inlinable
  func getOptionalArray<T: RawRepresentable>(_ key: String) -> Field<[T]>? {
    getOptionalArray(key, transform: T.init(rawValue:))
  }

  @inlinable
  func getOptionalArray<T: TemplateValue>(
    _ key: String,
    templateToType: [TemplateName: String]
  ) -> Field<[T]>? {
    getOptionalArray(
      key,
      transform: { (dict: Self) in try? T(dictionary: dict, templateToType: templateToType) }
    )
  }
}

extension [String: Any] {
  @inlinable
  func getField<T: TemplateValue>(
    _ key: String,
    templateToType: [TemplateName: String]
  ) throws -> T {
    try getField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict, templateToType: templateToType) }
    )
  }
}

extension TemplatesContext {
  @inlinable
  func getArray<T: TemplateValue>(
    _ key: String,
    validator: AnyArrayValueValidator<T.ResolvedValue>? = nil,
    type: T.Type
  ) -> DeserializationResult<[T.ResolvedValue]> {
    templateData.getArray(
      key,
      transform: makeTemplateDeserializer(
        templates: templates,
        templateToType: templateToType,
        type: type
      ),
      validator: validator
    )
  }
}

@inlinable
func deserialize<T: TemplateValue>(
  _ value: Any,
  templates: [TemplateName: Any],
  templateToType: [TemplateName: String],
  type: T.Type
) -> DeserializationResult<T.ResolvedValue> {
  deserialize(
    value,
    transform: makeTemplateDeserializer(
      templates: templates,
      templateToType: templateToType,
      type: type
    )
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
  { dict in
    let context = TemplatesContext(
      templates: templates,
      templateToType: templateToType,
      templateData: dict
    )
    return T.resolveValue(context: context, useOnlyLinks: false)
  }
}
