import Foundation
import Serialization
import VGSL

extension [String: Any] {
  func getExpressionField<T: RawRepresentable>(
    _ key: String,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> Expression<T> where T.RawValue == String {
    try getExpressionField(
      key,
      transform: T.init(rawValue:),
      validator: validator,
      context: context
    )
  }

  func getExpressionField<T: ValidSerializationValue>(
    _ key: String,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> Expression<T> {
    try getExpressionField(key, transform: { $0 as T }, validator: validator, context: context)
  }

  func getExpressionField<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> Expression<T> {
    try getField(
      key,
      transform: { expressionTransform($0, transform: transform, validator: validator) },
      context: context
    )
  }

  func getOptionalExpressionField<T: RawRepresentable>(
    _ key: String,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> Expression<T>? where T.RawValue == String {
    try getOptionalExpressionField(
      key,
      transform: T.init(rawValue:),
      validator: validator,
      context: context
    )
  }

  func getOptionalExpressionField<T: ValidSerializationValue>(
    _ key: String,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> Expression<T>? {
    try getOptionalExpressionField(
      key,
      transform: { $0 as T },
      validator: validator,
      context: context
    )
  }

  func getOptionalExpressionField<T, U>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyValueValidator<T>? = nil,
    context: ParsingContext
  ) throws -> Expression<T>? {
    try getOptionalField(
      key,
      transform: { expressionTransform($0, transform: transform, validator: validator) },
      context: context
    )
  }

  func getExpressionArray<T: ValidSerializationValue>(
    _ key: String,
    validator: AnyArrayValueValidator<Expression<T>>? = nil,
    context: ParsingContext
  ) throws -> [Expression<T>] {
    try getExpressionArray(key, transform: { $0 as T }, validator: validator, context: context)
  }

  func getExpressionArray<T, U: ValidSerializationValue>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyArrayValueValidator<Expression<T>>? = nil,
    context: ParsingContext
  ) throws -> [Expression<T>] {
    try getArray(
      key,
      transform: { (value: U) in expressionTransform(value, transform: transform) },
      validator: validator,
      context: context
    )
  }

  func getOptionalExpressionArray<T: ValidSerializationValue>(
    _ key: String,
    validator: AnyArrayValueValidator<Expression<T>>? = nil,
    context: ParsingContext
  ) throws -> [Expression<T>]? {
    try getOptionalExpressionArray(
      key,
      transform: { $0 as T },
      validator: validator,
      context: context
    )
  }

  func getOptionalExpressionArray<T, U: ValidSerializationValue>(
    _ key: String,
    transform: (U) -> T?,
    validator: AnyArrayValueValidator<Expression<T>>? = nil,
    context: ParsingContext
  ) throws -> [Expression<T>]? {
    try getOptionalArray(
      key,
      transform: { (value: U) in expressionTransform(value, transform: transform) },
      validator: validator,
      context: context
    )
  }
}
