// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivCurrencyInputMask: DivInputMaskBase, Sendable {
  public static let type: String = "currency"
  public let locale: Expression<String>?
  public let rawTextVariable: String

  public func resolveLocale(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveString(locale)
  }

  public convenience init(dictionary: [String: Any], context: ParsingContext) throws {
    self.init(
      locale: try dictionary.getOptionalExpressionField("locale", context: context),
      rawTextVariable: try dictionary.getField("raw_text_variable", context: context)
    )
  }

  init(
    locale: Expression<String>? = nil,
    rawTextVariable: String
  ) {
    self.locale = locale
    self.rawTextVariable = rawTextVariable
  }
}

#if DEBUG
extension DivCurrencyInputMask: Equatable {
  public static func ==(lhs: DivCurrencyInputMask, rhs: DivCurrencyInputMask) -> Bool {
    guard
      lhs.locale == rhs.locale,
      lhs.rawTextVariable == rhs.rawTextVariable
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCurrencyInputMask: Serializable {
  @_optimize(size)
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["locale"] = locale?.toValidSerializationValue()
    result["raw_text_variable"] = rawTextVariable
    return result
  }
}
