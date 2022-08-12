// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithStringArrayProperty {
  public static let type: String = "entity_with_string_array_property"
  public let array: [String] // at least 1 elements

  static let arrayValidator: AnyArrayValueValidator<String> =
    makeArrayValidator(minItems: 1)

  init(array: [String]) {
    self.array = array
  }
}

#if DEBUG
extension EntityWithStringArrayProperty: Equatable {
  public static func ==(
    lhs: EntityWithStringArrayProperty,
    rhs: EntityWithStringArrayProperty
  ) -> Bool {
    guard
      lhs.array == rhs.array
    else {
      return false
    }
    return true
  }
}
#endif
