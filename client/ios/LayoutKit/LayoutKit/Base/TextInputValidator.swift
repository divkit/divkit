import Foundation

public final class TextInputValidator {
  public let message: () -> String?
  public var isValid: Binding<Bool>

  private let allowEmpty: Bool
  private let validator: (String) -> Bool

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
    (allowEmpty && text.isEmpty) || validator(text)
  }
}
