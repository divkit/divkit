// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class EntityWithArray {
  public static let type: String = "entity_with_array"
  public let array: [Entity] // at least 1 elements

  static let arrayValidator: AnyArrayValueValidator<Entity> =
    makeArrayValidator(minItems: 1)

  init(
    array: [Entity]
  ) {
    self.array = array
  }
}

#if DEBUG
extension EntityWithArray: Equatable {
  public static func ==(lhs: EntityWithArray, rhs: EntityWithArray) -> Bool {
    guard
      lhs.array == rhs.array
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithArray: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["array"] = array.map { $0.toDictionary() }
    return result
  }
}
