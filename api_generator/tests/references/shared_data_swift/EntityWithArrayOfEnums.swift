// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithArrayOfEnums {
  @frozen
  public enum Item: String, CaseIterable {
    case first = "first"
    case second = "second"
  }

  public static let type: String = "entity_with_array_of_enums"
  public let items: [Item] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<EntityWithArrayOfEnums.Item> =
    makeArrayValidator(minItems: 1)

  init(
    items: [Item]
  ) {
    self.items = items
  }
}

#if DEBUG
extension EntityWithArrayOfEnums: Equatable {
  public static func ==(lhs: EntityWithArrayOfEnums, rhs: EntityWithArrayOfEnums) -> Bool {
    guard
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif
