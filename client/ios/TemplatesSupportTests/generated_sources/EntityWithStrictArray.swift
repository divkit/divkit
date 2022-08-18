// Generated code. Do not modify.

@testable import DivKit

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

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

extension EntityWithStrictArray: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["array"] = array.map { $0.toDictionary() }
    return result
  }
}
