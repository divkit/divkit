// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithOptionalProperty {
  public static let type: String = "entity_with_optional_property"
  public let property: String? // at least 1 char

  static let propertyValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(property: String? = nil) {
    self.property = property
  }
}

#if DEBUG
extension EntityWithOptionalProperty: Equatable {
  public static func ==(lhs: EntityWithOptionalProperty, rhs: EntityWithOptionalProperty) -> Bool {
    guard
      lhs.property == rhs.property
    else {
      return false
    }
    return true
  }
}
#endif
