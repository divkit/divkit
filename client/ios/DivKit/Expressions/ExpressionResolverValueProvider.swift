import Foundation

protocol ExpressionResolverValueProvider {
  func typedValue<T>() -> T?
}
