// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivActionFocusElement {
  public static let type: String = "focus_element"
  public let elementId: Expression<String>

  public func resolveElementId(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: elementId, initializer: { $0 })
  }

  init(
    elementId: Expression<String>
  ) {
    self.elementId = elementId
  }
}

#if DEBUG
extension DivActionFocusElement: Equatable {
  public static func ==(lhs: DivActionFocusElement, rhs: DivActionFocusElement) -> Bool {
    guard
      lhs.elementId == rhs.elementId
    else {
      return false
    }
    return true
  }
}
#endif

extension DivActionFocusElement: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["element_id"] = elementId.toValidSerializationValue()
    return result
  }
}
