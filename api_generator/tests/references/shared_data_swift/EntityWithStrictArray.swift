// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithStrictArray {
  public static let type: String = "entity_with_strict_array"
  public let array: [Entity] // at least 1 elements; all received elements must be valid

  static let arrayValidator: AnyArrayValueValidator<Entity> =
    makeStrictArrayValidator(minItems: 1)

  init(
    array: [Entity]
  ) {
    self.array = array
  }
}

#if DEBUG
extension EntityWithStrictArray: Equatable {
  public static func ==(lhs: EntityWithStrictArray, rhs: EntityWithStrictArray) -> Bool {
    guard
      lhs.array == rhs.array
    else {
      return false
    }
    return true
  }
}
#endif
