// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivChangeSetTransition {
  public static let type: String = "set"
  public let items: [DivChangeTransition] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<DivChangeTransition> =
    makeArrayValidator(minItems: 1)

  init(
    items: [DivChangeTransition]
  ) {
    self.items = items
  }
}

#if DEBUG
extension DivChangeSetTransition: Equatable {
  public static func ==(lhs: DivChangeSetTransition, rhs: DivChangeSetTransition) -> Bool {
    guard
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif

extension DivChangeSetTransition: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toDictionary() }
    return result
  }
}
