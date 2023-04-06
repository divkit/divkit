import Foundation

import BasePublic
import Serialization

extension Dictionary where Key == String, Value == Any {
  func getOptionalExpressionField(
    _ key: String
  ) throws -> Field<Expression<CFString>>? {
    Field.makeOptional(
      valueGetter: (try? getOptionalField(
        key,
        transform: { expressionTransform($0, transform: { safeCFCast($0 as CFTypeRef) }) }
      )).flatMap { $0 },
      linkGetter: link(for: key)
    )
  }

  func getOptionalExpressionField<T: RawRepresentable>(
    _ key: String
  ) throws -> Field<Expression<T>>? {
    try getOptionalExpressionField(key, transform: T.init(rawValue:))
  }

  func getOptionalExpressionField<T: ValidSerializationValue>(
    _ key: String
  ) throws -> Field<Expression<T>>? {
    try getOptionalExpressionField(key, transform: { $0 as T })
  }

  func getOptionalExpressionField<T, U>(
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

  func getOptionalExpressionArray<T: ValidSerializationValue>(
    _ key: String
  ) throws -> Field<[Expression<T>]>? {
    try getOptionalExpressionArray(key, transform: { $0 as T })
  }

  func getOptionalExpressionArray<T, U>(
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
