// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithRequiredProperty {
  public static let type: String = "entity_with_required_property"
  public let property: Expression<String> // at least 1 char

  public func resolveProperty(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(property, initializer: { $0 })
  }

  static let propertyValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    property: Expression<String>
  ) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithRequiredProperty: Equatable {
  public static func ==(lhs: EntityWithRequiredProperty, rhs: EntityWithRequiredProperty) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif
