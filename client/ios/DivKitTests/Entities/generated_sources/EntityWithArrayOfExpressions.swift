// Generated code. Do not modify.

@testable import DivKit

import CommonCorePublic
import Foundation
import Serialization

public final class EntityWithArrayOfExpressions {
  public static let type: String = "entity_with_array_of_expressions"
  public let items: [Expression<String>] // at least 1 elements

  public func resolveItems(_ resolver: ExpressionResolver) -> [String]? {
    items.map { resolver.resolveStringBasedValue(expression: $0, initializer: { $0 }) }.compactMap { $0 }
  }

  static let itemsValidator: AnyArrayValueValidator<Expression<String>> =
    makeArrayValidator(minItems: 1)

  init(
    items: [Expression<String>]
  ) {
    self.items = items
  }
}

#if DEBUG
extension EntityWithArrayOfExpressions: Equatable {
  public static func ==(lhs: EntityWithArrayOfExpressions, rhs: EntityWithArrayOfExpressions) -> Bool {
    guard
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithArrayOfExpressions: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toValidSerializationValue() }
    return result
  }
}
