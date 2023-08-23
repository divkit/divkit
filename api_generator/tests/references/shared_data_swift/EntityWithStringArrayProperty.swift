// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStringArrayProperty {
  public static let type: String = "entity_with_string_array_property"
  public let array: [Expression<String>] // at least 1 elements

  public func resolveArray(_ resolver: ExpressionResolver) -> [String]? {
    array.map { resolver.resolveStringBasedValue(expression: $0, initializer: { $0 }) }.compactMap { $0 }
  }

  static let arrayValidator: AnyArrayValueValidator<Expression<String>> =
    makeArrayValidator(minItems: 1)

  init(
    array: [Expression<String>]
  ) {
    self.array = array
  }
}

#if DEBUG
extension EntityWithStringArrayProperty: Equatable {
  public static func ==(lhs: EntityWithStringArrayProperty, rhs: EntityWithStringArrayProperty) -> Bool {
    guard
      lhs.array == rhs.array
    else {
      return false
    }
    return true
  }
}
#endif
