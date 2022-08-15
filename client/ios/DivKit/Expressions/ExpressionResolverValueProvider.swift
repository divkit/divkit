import Foundation

public protocol ExpressionResolverValueProvider {
  func typedValue<T>() -> T?
}
