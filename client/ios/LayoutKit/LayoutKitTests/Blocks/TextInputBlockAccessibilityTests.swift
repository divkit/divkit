#if os(iOS)
import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct TextInputBlockAccessibilityTests {
  @Test
  func hintIsNotSeparateAccessibilityElement() throws {
    let view = makeView(hint: "Hint")
    let label = try hintLabel(in: view)

    #expect(!label.isAccessibilityElement)
  }

  @Test
  func nativeFieldLabelIsHint() throws {
    let view = makeView(hint: "Hint")

    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == ["Hint", "Hint"])
  }

  @Test
  func nativeFieldLabelIsNilWhenHintIsEmpty() throws {
    let view = makeView(hint: "")

    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == [nil, nil])
  }

  @Test
  func nativeFieldLabelContainsValidationMessageWhenValidationFailed() throws {
    let view = makeView(
      hint: "Hint",
      text: "1",
      validators: [
        makeValidator(isValid: false, message: "Too short"),
        makeValidator(isValid: false, message: "Only letters"),
      ]
    )

    #expect(
      try nativeFields(in: view).map(\.accessibilityLabel)
        == ["Hint. Too short. Only letters", "Hint. Too short. Only letters"]
    )
  }

  @Test
  func nativeFieldLabelIsHintWhenValidationPassed() throws {
    let view = makeView(
      hint: "Hint",
      text: "123456",
      validators: [makeValidator(isValid: true, message: "Too short")]
    )

    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == ["Hint", "Hint"])
  }

  @Test
  func nativeFieldLabelIsNilWhenHintIsEmptyAndValidationPassed() throws {
    let view = makeView(
      hint: "",
      text: "123456",
      validators: [makeValidator(isValid: true, message: "Too short")]
    )

    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == [nil, nil])
  }

  @Test
  func nativeFieldLabelUpdatesOnReconfigure() throws {
    let view = makeView(hint: "Hint")

    makeBlock(hint: "New hint", inputType: .default).configureBlockView(view)

    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == ["New hint", "New hint"])
  }

  @Test(arguments: ["", "secret"])
  func secureFieldPreservesNativeValueAndExposesAccessibilityMetadata(text: String) throws {
    let block = makeBlock(
      hint: "Password",
      text: text,
      inputType: .default,
      validators: [makeValidator(isValid: false, message: "Too short")],
      accessibilityHint: "At least eight characters",
      accessibilityStateDescription: "Required",
      isSecure: true
    )
    let original = makeBlock(hint: "Password", text: text, inputType: .default, isSecure: true)
    let view = original.makeBlockView()
    original.configureBlockView(view)
    let field = try #require(view.subviews.compactMap { $0 as? UITextField }.first)
    let nativeValue = field.accessibilityValue

    block.configureBlockView(view)
    #expect(field.isSecureTextEntry)
    #expect(field.text == text)
    #expect(field.accessibilityLabel == "Password. Required. Too short")
    #expect(field.accessibilityHint == "At least eight characters")
    #expect(field.accessibilityValue == nativeValue)
  }

  @Test
  func nativeAccessibilityMetadataIsClearedOnReconfigure() throws {
    let block = makeBlock(
      hint: "Password",
      inputType: .default,
      accessibilityHint: "At least eight characters",
      accessibilityStateDescription: "Required",
      isSecure: true
    )
    let view = block.makeBlockView()
    block.configureBlockView(view)

    makeBlock(hint: "", inputType: .default).configureBlockView(view)

    #expect(try nativeFields(in: view).map(\.accessibilityHint) == [nil, nil])
    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == [nil, nil])
  }

  @Test
  func nativeAccessibilityMetadataIsPreservedOnFocusAndSizeUpdates() throws {
    let block = makeBlock(
      hint: "Password",
      inputType: .default,
      accessibilityHint: "At least eight characters",
      accessibilityStateDescription: "Required",
      isSecure: true
    )
    let updated = try block.updated(path: "", isFocused: true).modifying(widthTrait: .fixed(100))
    let view = updated.makeBlockView()
    updated.configureBlockView(view)

    #expect(
      try nativeFields(in: view).map(\.accessibilityHint)
        == ["At least eight characters", "At least eight characters"]
    )
    #expect(
      try nativeFields(in: view).map(\.accessibilityLabel)
        == ["Password. Required", "Password. Required"]
    )
  }

  @Test
  func selectKeepsHintAsSeparateAccessibilityElement() throws {
    let view = makeView(
      hint: "Hint",
      inputType: .selection([.init(value: "a", text: "A")])
    )

    #expect(try hintLabel(in: view).isAccessibilityElement)
    #expect(try nativeFields(in: view).map(\.accessibilityLabel) == [nil, nil])
  }
}

@MainActor
private func makeView(
  hint: String,
  text: String = "",
  inputType: TextInputBlock.InputType = .default,
  validators: [TextInputValidator]? = nil
) -> BlockView {
  let block = makeBlock(hint: hint, text: text, inputType: inputType, validators: validators)
  let view = block.makeBlockView()
  block.configureBlockView(view)
  return view
}

private func makeBlock(
  hint: String,
  text: String = "",
  inputType: TextInputBlock.InputType,
  validators: [TextInputValidator]? = nil,
  accessibilityHint: String? = nil,
  accessibilityStateDescription: String? = nil,
  isSecure: Bool = false
) -> TextInputBlock {
  TextInputBlock(
    hint: NSAttributedString(string: hint),
    accessibilityHint: accessibilityHint,
    accessibilityStateDescription: accessibilityStateDescription,
    textValue: Binding(name: "name", value: Property(initialValue: text)),
    textTypo: Typo(),
    multiLineMode: !isSecure,
    inputType: inputType,
    path: "",
    validators: validators,
    layoutDirection: .leftToRight,
    isSecure: isSecure
  )
}

private func makeValidator(isValid: Bool, message: String) -> TextInputValidator {
  TextInputValidator(
    isValid: Binding(name: "valid", value: Property(initialValue: false)),
    allowEmpty: false,
    validator: { _ in isValid },
    message: { message }
  )
}

@MainActor
private func hintLabel(in view: UIView) throws -> UILabel {
  try #require(view.subviews.compactMap { $0 as? UILabel }.first)
}

@MainActor
private func nativeFields(in view: UIView) throws -> [UIView] {
  let fields = view.subviews.filter { $0 is UITextField || $0 is UITextView }
  try #require(fields.count == 2)
  return fields
}
#endif
