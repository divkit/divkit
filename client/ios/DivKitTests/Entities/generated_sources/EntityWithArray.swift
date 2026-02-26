// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

public final class EntityWithArray: Sendable {
  public static let type: String = "entity_with_array"
  public let array: [Entity] // at least 1 elements

  static let arrayValidator: AnyArrayValueValidator<Entity> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      array: try dictionary.getArray("array", transform: { (dict: [String: Any]) in try? Entity(dictionary: dict, context: context) }, validator: Self.arrayValidator, context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["array"] = array.map { $0.toDictionary() }
    return result
  }
}
