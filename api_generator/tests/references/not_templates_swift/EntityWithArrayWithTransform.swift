// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithArrayWithTransform: Sendable {
  public static let type: String = "entity_with_array_with_transform"
  public let array: [Expression<Color>] // at least 1 elements

  public func resolveArray(_ resolver: ExpressionResolver) -> [Color]? {
    array.map { resolver.resolveColor($0) }.compactMap { $0 }
  }

  static let arrayValidator: AnyArrayValueValidator<Expression<Color>> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      array: try dictionary.getExpressionArray("array", transform: Color.color(withHexString:), validator: Self.arrayValidator, context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["array"] = array.map { $0.toValidSerializationValue() }
    return result
  }
}
