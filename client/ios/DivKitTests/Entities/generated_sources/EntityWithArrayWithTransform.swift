// Generated code. Do not modify.

@testable import DivKit

import Foundation
import Serialization
import VGSL

public final class EntityWithArrayWithTransform {
  public static let type: String = "entity_with_array_with_transform"
  public let array: [Expression<Color>] // at least 1 elements

  public func resolveArray(_ resolver: ExpressionResolver) -> [Color]? {
    array.map { resolver.resolveColor($0) }.compactMap { $0 }
  }

  static let arrayValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 1)

  init(
    array: [Expression<Color>]
  ) {
    self.array = array
  }
}

#if DEBUG
extension EntityWithArrayWithTransform: Equatable {
  public static func ==(lhs: EntityWithArrayWithTransform, rhs: EntityWithArrayWithTransform) -> Bool {
    guard
      lhs.array == rhs.array
    else {
      return false
    }
    return true
  }
}
#endif

extension EntityWithArrayWithTransform: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["array"] = array.map { $0.toValidSerializationValue() }
    return result
  }
}
