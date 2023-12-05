// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization

public final class EntityWithRawArray {
  public static let type: String = "entity_with_raw_array"
  public let array: Expression<[Any]>

  public func resolveArray(_ resolver: ExpressionResolver) -> [Any]? {
    resolver.resolveArrayValue(expression: array)
  }

  init(
    array: Expression<[Any]>
  ) {
    self.array = array
  }
}

#if DEBUG
// WARNING: this == is incomplete because of [Any] in class fields
extension EntityWithRawArray: Equatable {
  public static func ==(lhs: EntityWithRawArray, rhs: EntityWithRawArray) -> Bool {
    return true
  }
}
#endif
