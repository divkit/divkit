import Foundation

import Serialization
import TemplatesSupport

extension Dictionary where Key == String, Value == Any {
  public func getOptionalField(
    _ key: String
  ) throws -> Field<Expression<CFString>>? {
    Field.makeOptional(
      valueGetter: (try? getOptionalField(
        key,
        transform: { expressionTransform($0, transform: { $0 }) }
      )).flatMap { $0 },
      linkGetter: link(for: key)
    )
  }

  public func getOptionalField<T: RawRepresentable>(
    _ key: String
  ) throws -> Field<Expression<T>>? {
    try getOptionalField(
      key,
      transform: T.init(rawValue:)
    )
  }

  public func getOptionalField<T: ValidSerializationValue>(
    _ key: String
  ) throws -> Field<Expression<T>>? {
    try getOptionalField(
      key,
      transform: { expressionTransform($0, transform: { $0 as T }) }
    )
  }

  public func getOptionalField<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyValueValidator<Expression<T>>? = nil
  ) throws -> Field<Expression<T>>? {
    try getOptionalField(
      key,
      transform: { expressionTransform($0, transform: transform) },
      validator: validator
    )
  }

  public func getOptionalArray<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyArrayValueValidator<Expression<T>>? = nil
  ) throws -> Field<[Expression<T>]>? {
    try getOptionalArray(
      key,
      transform: { (value: U) in
        expressionTransform(value, transform: transform)
      },
      validator: validator
    )
  }
}
