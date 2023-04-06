// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCurrencyInputMask {
  public static let type: String = "currency"
  public let locale: Expression<String>? // at least 1 char

  public func resolveLocale(_ resolver: ExpressionResolver) -> String? {
    resolver.resolveStringBasedValue(expression: locale, initializer: { $0 })
  }

  static let localeValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  init(
    locale: Expression<String>? = nil
  ) {
    self.locale = locale
  }
}

#if DEBUG
extension DivCurrencyInputMask: Equatable {
  public static func ==(lhs: DivCurrencyInputMask, rhs: DivCurrencyInputMask) -> Bool {
    guard
      lhs.locale == rhs.locale
    else {
      return false
    }
    return true
  }
}
#endif

extension DivCurrencyInputMask: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["locale"] = locale?.toValidSerializationValue()
    return result
  }
}
