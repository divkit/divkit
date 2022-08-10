// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivAppearanceSetTransition {
  public static let type: String = "set"
  public let items: [DivAppearanceTransition] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<DivAppearanceTransition> =
    makeArrayValidator(minItems: 1)

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
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toDictionary() }
    return result
  }
}
