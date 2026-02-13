// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextRangeMaskBase: Sendable {
  public let isEnabled: Expression<Bool> // default value: true

  public func resolveIsEnabled(_ resolver: ExpressionResolver) -> Bool {
    resolver.resolveNumeric(isEnabled) ?? true
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      isEnabled: try dictionary.getOptionalExpressionField("is_enabled", context: context)
    )
  }

  init(
    isEnabled: Expression<Bool>? = nil
  ) {
    self.isEnabled = isEnabled ?? .value(true)
  }
}

#if DEBUG
extension DivTextRangeMaskBase: Equatable {
  public static func ==(lhs: DivTextRangeMaskBase, rhs: DivTextRangeMaskBase) -> Bool {
    guard
      lhs.isEnabled == rhs.isEnabled
    else {
      return false
    }
    return true
  }
}
#endif

extension DivTextRangeMaskBase: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["is_enabled"] = isEnabled.toValidSerializationValue()
    return result
  }
}
