// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivChangeSetTransition: Sendable {
  public static let type: String = "set"
  public let items: [DivChangeTransition] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<DivChangeTransition> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      items: try dictionary.getArray("items", transform: { (dict: [String: Any]) in try? DivChangeTransition(dictionary: dict, context: context) }, validator: Self.itemsValidator, context: context)
    )
  }

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
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toDictionary() }
    return result
  }
}
