// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class EntityWithArrayWithTransform {
  public static let type: String = "entity_with_array_with_transform"
  public let array: [Color] // at least 1 elements

  static let arrayValidator: AnyArrayValueValidator<Color> =
    makeArrayValidator(minItems: 1)

  init(array: [Color]) {
    self.array = array
  }
}

#if DEBUG
extension EntityWithArrayWithTransform: Equatable {
  public static func ==(
    lhs: EntityWithArrayWithTransform,
    rhs: EntityWithArrayWithTransform
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
