// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivSelectionInput {
  public final class Item {
    public let text: Expression<String> // at least 1 char
    public let value: Expression<String>? // at least 1 char

    public func resolveText(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: text, initializer: { $0 })
    }

    public func resolveValue(_ resolver: ExpressionResolver) -> String? {
      resolver.resolveStringBasedValue(expression: value, initializer: { $0 })
    }

    static let textValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    static let valueValidator: AnyValueValidator<String> =
      makeStringValidator(minLength: 1)

    init(
      text: Expression<String>,
      value: Expression<String>? = nil
    ) {
      self.text = text
      self.value = value
    }
  }

  public static let type: String = "selection"
  public let items: [Item] // at least 1 elements

  static let itemsValidator: AnyArrayValueValidator<DivSelectionInput.Item> =
    makeArrayValidator(minItems: 1)

  init(
    items: [Item]
  ) {
    self.items = items
  }
}

#if DEBUG
extension DivSelectionInput: Equatable {
  public static func ==(lhs: DivSelectionInput, rhs: DivSelectionInput) -> Bool {
    guard
      lhs.items == rhs.items
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSelectionInput: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["items"] = items.map { $0.toDictionary() }
    return result
  }
}

#if DEBUG
extension DivSelectionInput.Item: Equatable {
  public static func ==(lhs: DivSelectionInput.Item, rhs: DivSelectionInput.Item) -> Bool {
    guard
      lhs.text == rhs.text,
      lhs.value == rhs.value
    else {
      return false
    }
    return true
  }
}
#endif

extension DivSelectionInput.Item: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["text"] = text.toValidSerializationValue()
    result["value"] = value?.toValidSerializationValue()
    return result
  }
}
