// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithRequiredProperty {
  public static let type: String = "entity_with_required_property"
  public let property: String // at least 1 char

  static let propertyValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(property: String) {
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
