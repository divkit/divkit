import Foundation

public final class TextInputValidator {
  private let allowEmpty: Bool
  private let validator: (String) -> Bool

  public let message: () -> String?
  public var isValid: Binding<Bool>

  public init(
    isValid: Binding<Bool>,
    allowEmpty: Bool,
    validator: @escaping (String) -> Bool,
    message: @escaping () -> String?
  ) {
    self.isValid = isValid
    self.allowEmpty = allowEmpty
    self.validator = validator
    self.message = message
  }

  public func validate(_ text: String) -> Bool {
    return (allowEmpty && text.isEmpty) || validator(text)
  }
}
