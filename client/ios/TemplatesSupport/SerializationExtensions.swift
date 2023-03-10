import CoreFoundation

import CommonCore
import Serialization

extension Field {
  @inlinable
  public init(
    valueGetter: @autoclosure () -> T?,
    linkGetter: @autoclosure () -> Link?
  ) throws {
    if let value = valueGetter() {
      self = .value(value)
    } else if let link = linkGetter() {
      self = .link(link)
    } else {
      throw DeserializationError.generic
    }
  }

  @inlinable
  public static func makeOptional(
    valueGetter: @autoclosure () -> T?,
    linkGetter: @autoclosure () -> Link?
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

/// Deserialization for Field<T>
extension Dictionary where Key == String, Value == Any {
  public func link(for key: String) -> Link? {
    self["$" + key] as? Link
  }

  @usableFromInline
  func makeField<T: ValidSerializationValue, U>(
    _ key: String,
    transform: (T) throws -> U,
    validator: AnyValueValidator<U>? = nil
  ) throws -> Field<U> {
    try Field(
      valueGetter: (try? getOptionalField(key, transform: transform, validator: validator))
        .flatMap { $0 },
      linkGetter: link(for: key)
    )
  }

  @usableFromInline
  func makeField<T: ValidSerializationValue, U>(
    _ key: String,
    transform: (T) throws -> U?
  ) throws -> Field<U> {
    try makeField(key, transform: { (value: T) throws -> U in
      guard let result = try transform(value) else {
        throw DeserializationError.generic
      }
      return result
    })
  }

  public func getField(_ key: String) throws -> Field<CFString> {
    try Field(
      valueGetter: (try? getOptionalField(key)).flatMap { $0 },
      linkGetter: link(for: key)
    )
  }

  @inlinable
  public func getField<T: RawRepresentable>(_ key: String) throws -> Field<T>
    where T.RawValue: ValidSerializationValue {
    try makeField(key, transform: { T(rawValue: $0) })
  }

  @inlinable
  public func getField<T: ValidSerializationValue>(_ key: String) throws -> Field<T> {
    try makeField(key, transform: { $0 as T })
  }

  @inlinable
  public func getField<T: Deserializable>(_ key: String) throws -> Field<T> {
    try makeField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict) }
    )
  }

  @inlinable
  public func getField<T: TemplateDeserializable>(
    _ key: String,
    templateToType: TemplateToType,
    validator: AnyValueValidator<T>? = nil
  ) throws -> Field<T> {
    try makeField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict, templateToType: templateToType) },
      validator: validator
    )
  }
}

/// Deserialization for Field<T>?
extension Dictionary where Key == String, Value == Any {
  @inlinable
  public func getOptionalField<T, U>(
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

  public func getOptionalField(_ key: String) throws -> Field<CFString>? {
    Field.makeOptional(
      valueGetter: (try? getOptionalField(key)).flatMap { $0 },
      linkGetter: link(for: key)
    )
  }

  @inlinable
  public func getOptionalField<T: RawRepresentable>(_ key: String) throws -> Field<T>? {
    try getOptionalField(
      key,
      transform: T.init(rawValue:)
    )
  }

  @inlinable
  public func getOptionalField<T: ValidSerializationValue>(_ key: String) throws -> Field<T>? {
    try getOptionalField(
      key,
      transform: { $0 as T }
    )
  }

  @inlinable
  public func getOptionalField<T: Deserializable>(_ key: String) throws -> Field<T>? {
    try getOptionalField(
      key,
      transform: { (dict: Self) in try? T(dictionary: dict) }
    )
  }

  @inlinable
  public func getOptionalField<T: TemplateDeserializable>(
    _ key: String,
    templateToType: TemplateToType,
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
  public func getOptionalArray<T, U>(
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
  public func getOptionalArray<T, U>(
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
  public func getOptionalArray(_ key: String) throws -> Field<[String]>? {
    try getOptionalArray(
      key,
      transform: { $0 as String }
    )
  }

  @inlinable
  public func getOptionalArray<T: Deserializable>(_ key: String) throws -> Field<[T]>? {
    try getOptionalArray(
      key,
      transform: { (dict: Self) in try T(dictionary: dict) }
    )
  }

  @inlinable
  public func getOptionalArray<T: RawRepresentable>(_ key: String) throws -> Field<[T]>? {
    try getOptionalArray(
      key,
      transform: T.init(rawValue:)
    )
  }

  @inlinable
  public func getOptionalArray<T: TemplateDeserializable>(
    _ key: String,
    templateToType: TemplateToType,
    validator: AnyArrayValueValidator<T>? = nil
  ) throws -> Field<[T]>? {
    try getOptionalArray(
      key,
      transform: { (dict: Self) in try? T(dictionary: dict, templateToType: templateToType) },
      validator: validator
    )
  }
}

/// Deserialization for TemplateDeserializable
extension Dictionary where Key == String, Value == Any {
  @inlinable
  public func getField<T: TemplateDeserializable>(
    _ key: String,
    templateToType: TemplateToType,
    validator: AnyValueValidator<T>? = nil
  ) throws -> T {
    try getField(
      key,
      transform: { (dict: Self) in try T(dictionary: dict, templateToType: templateToType) },
      validator: validator
    )
  }
}

extension Context {
  @usableFromInline
  func deserializeTemplate<T: TemplateValue & TemplateDeserializable>(
    _ dict: TemplateData,
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
  public func getField<T: TemplateValue & TemplateDeserializable>(
    _ key: String,
    validator: AnyValueValidator<T.ResolvedValue>? = nil,
    type: T.Type
  ) throws -> T.ResolvedValue {
    try templateData.getField(
      key,
      transform: { deserializeTemplate($0, type: type).value },
      validator: validator
    )
  }

  @inlinable
  public func getOptionalField<T: TemplateValue & TemplateDeserializable>(
    _ key: String,
    validator: AnyValueValidator<T.ResolvedValue>? = nil,
    type: T.Type
  ) throws -> T.ResolvedValue? {
    try templateData.getOptionalField(
      key,
      transform: { deserializeTemplate($0, type: type).value },
      validator: validator
    )
  }

  @inlinable
  public func getArray<T: TemplateValue & TemplateDeserializable>(
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

  @inlinable
  public func getOptionalArray<T: TemplateValue & TemplateDeserializable>(
    _ key: String,
    validator: AnyArrayValueValidator<T.ResolvedValue>? = nil,
    type: T.Type
  ) throws -> [T.ResolvedValue]? {
    try templateData.getOptionalArray(
      key,
      transform: { deserializeTemplate($0, type: type).value },
      validator: validator
    )
  }
}

@inlinable
public func deserialize<T: TemplateValue & TemplateDeserializable>(
  _ value: Any,
  templates: Templates,
  templateToType: TemplateToType,
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
public func deserialize<T: TemplateValue & TemplateDeserializable>(
  _ value: Any,
  templates: Templates,
  templateToType: TemplateToType,
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
func makeTemplateDeserializer<T: TemplateValue & TemplateDeserializable>(
  templates: Templates,
  templateToType: TemplateToType,
  type _: T.Type
) -> ((TemplateData) -> DeserializationResult<T.ResolvedValue>) {
  return { dict in
    let context = Context(templates: templates, templateToType: templateToType, templateData: dict)
    return T.resolveValue(context: context, useOnlyLinks: false)
  }
}
