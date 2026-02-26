// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivAppearanceSetTransition: Sendable {
  public static let type: String = "set"
  public let items: [DivAppearanceTransition] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<DivAppearanceTransition> =
    makeArrayValidator(minItems: 1)

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      items: try dictionary.getArray("items", transform: { (dict: [String: Any]) in try? DivAppearanceTransition(dictionary: dict, context: context) }, validator: Self.itemsValidator, context: context)
    )
  }

  init(
    items: [DivAppearanceTransition]
  ) {
    self.items = items
  }
}

#if DEBUG
extension DivAppearanceSetTransition: Equatable {
  public static func ==(lhs: DivAppearanceSetTransition, rhs: DivAppearanceSetTransition) -> Bool {
    guard
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif

extension DivAppearanceSetTransition: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toDictionary() }
    return result
  }
}
