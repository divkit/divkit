// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization
import TemplatesSupport

public final class DivKeyboardInput {
  @frozen
  public enum KeyboardType: String, CaseIterable {
    case singleLineText = "single_line_text"
    case multiLineText = "multi_line_text"
    case phone = "phone"
    case number = "number"
    case email = "email"
    case uri = "uri"
    case decimal = "decimal"
  }

  public static let type: String = "keyboard"
  public let keyboardType: Expression<KeyboardType> // default value: multi_line_text

  public func resolveKeyboardType(_ resolver: ExpressionResolver) -> KeyboardType {
    resolver.resolveStringBasedValue(expression: keyboardType, initializer: KeyboardType.init(rawValue:)) ?? KeyboardType.multiLineText
  }

  static let keyboardTypeValidator: AnyValueValidator<DivKeyboardInput.KeyboardType> =
    makeNoOpValueValidator()

  init(
    keyboardType: Expression<KeyboardType>? = nil
  ) {
    self.keyboardType = keyboardType ?? .value(.multiLineText)
  }
}

#if DEBUG
extension DivKeyboardInput: Equatable {
  public static func ==(lhs: DivKeyboardInput, rhs: DivKeyboardInput) -> Bool {
    guard
      lhs.keyboardType == rhs.keyboardType
    else {
      return false
    }
    return true
  }
}
#endif

extension DivKeyboardInput: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["keyboard_type"] = keyboardType.toValidSerializationValue()
    return result
  }
}
