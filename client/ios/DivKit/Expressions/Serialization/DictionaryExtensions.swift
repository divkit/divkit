import Foundation
import Serialization
import VGSL

extension [String: Any] {
  func getOptionalExpressionField<T: RawRepresentable>(
    _ key: String
  ) -> Field<Expression<T>>? {
    getOptionalExpressionField(key, transform: T.init(rawValue:))
  }

  func getOptionalExpressionField<T: ValidSerializationValue>(
    _ key: String
  ) -> Field<Expression<T>>? {
    getOptionalExpressionField(key, transform: { $0 as T })
  }

  func getOptionalExpressionField<T, U>(
    _ key: String,
    transform: (U) -> T?
  ) -> Field<Expression<T>>? {
    getOptionalField(
      key,
      transform: { expressionTransform($0, transform: transform) }
    )
  }

  func getOptionalExpressionArray<T: ValidSerializationValue>(
    _ key: String
  ) -> Field<[Expression<T>]>? {
    getOptionalExpressionArray(key, transform: { $0 as T })
  }

  func getOptionalExpressionArray<T, U>(
    _ key: String,
    transform: (U) -> T?
  ) -> Field<[Expression<T>]>? {
    getOptionalArray(
      key,
      transform: { (value: U) in
        expressionTransform(value, transform: transform)
      }
    )
  }
}
