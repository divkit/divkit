import LayoutKit
import UIKit
import VGSL
import XCTest

final class TextInputBlockTests: XCTestCase {
  func test_updateReturnsSameBlockForSameFocus() {
    let block = TextInputBlock(
      hint: NSAttributedString(),
      textValue: Binding(name: "name", value: Property(initialValue: "Test")),
      textTypo: Typo(),
      path: "",
      layoutDirection: .leftToRight
    )

    XCTAssertTrue(try block.updated(path: "", isFocused: false) === block)
  }

  func test_updateReturnsDifferentBlockForDifferentFocus() {
    let block = TextInputBlock(
      hint: NSAttributedString(),
      textValue: Binding(name: "name", value: Property(initialValue: "Test")),
      textTypo: Typo(),
      path: "",
      layoutDirection: .leftToRight
    )

    XCTAssertTrue(try block.updated(path: "", isFocused: true) !== block)
  }

  // DIVKIT-9723: Verify that TextInputBlock correctly stores the maskValidator for masked inputs.
  // The cursor correction fix in textFieldDidChange() relies on maskedViewModel being non-nil,
  // which is set up only when maskValidator is provided to TextInputBlock.
  func test_blockWithMaskValidator_storesMaskValidator() {
    let patternElement = PatternElement(
      key: "#",
      regex: try! NSRegularExpression(pattern: "\\d"),
      placeholder: "_"
    )
    let formatter = FixedLengthMaskFormatter(
      pattern: "+7 (###) ###-##-##",
      alwaysVisible: false,
      patternElements: [patternElement]
    )
    let maskValidator = MaskValidator(formatter: formatter)

    let block = TextInputBlock(
      hint: NSAttributedString(),
      textValue: Binding(name: "phone", value: Property(initialValue: "+7 (900) 123-45-67")),
      rawTextValue: Binding(name: "phone_raw", value: Property(initialValue: "9001234567")),
      textTypo: Typo(),
      maskValidator: maskValidator,
      path: "",
      layoutDirection: .leftToRight
    )

    XCTAssertNotNil(block.maskValidator)
  }

  // DIVKIT-9723: Verify that the MaskValidator produces correct formatted output for a phone mask.
  // This validates the mask formatting that the cursor correction fix depends on.
  func test_maskValidator_formatsPhoneNumberCorrectly() {
    let patternElement = PatternElement(
      key: "#",
      regex: try! NSRegularExpression(pattern: "\\d"),
      placeholder: "_"
    )
    let formatter = FixedLengthMaskFormatter(
      pattern: "+7 (###) ###-##-##",
      alwaysVisible: false,
      patternElements: [patternElement]
    )
    let maskValidator = MaskValidator(formatter: formatter)

    let result = maskValidator.formatted(rawText: "9001234567")
    XCTAssertEqual(result.text, "+7 (900) 123-45-67")
  }

  func test_maskedInput_keepsTypingOrder_whenKeystrokesOutpaceViewUpdate() {
    for multiLineMode in [false, true] {
      let input = makeMaskedInput(multiLineMode: multiLineMode)
      input.type("1")
      drainMainQueue()

      input.type("0")
      input.type("2")
      drainMainQueue()

      let mode = "multiLineMode: \(multiLineMode)"
      XCTAssertEqual(input.rawText, "102", mode)
      XCTAssertEqual(input.displayedText, "+7 (102) ", mode)
      XCTAssertEqual(input.selectedRange, NSRange(location: 7, length: 0), mode)
    }
  }

  func test_maskedInput_insertsAtCaret_whenKeystrokesOutpaceViewUpdate() {
    let input = makeMaskedInput()
    input.type("123456")
    drainMainQueue()

    input.select(NSRange(location: 5, length: 0))
    input.type("8")
    input.type("9")
    drainMainQueue()

    XCTAssertEqual(input.rawText, "18923456")
    XCTAssertEqual(input.displayedText, "+7 (189) 234-56-")
  }

  func test_maskedInput_deletesInOrder_whenKeystrokesOutpaceViewUpdate() {
    let input = makeMaskedInput()
    input.type("102")
    drainMainQueue()

    input.type("")
    input.type("")
    input.type("")
    drainMainQueue()

    XCTAssertEqual(input.rawText, "")
    XCTAssertEqual(input.displayedText, "+7 (")
  }

  func test_maskedInput_replacesSelection_whenKeystrokesOutpaceViewUpdate() {
    let input = makeMaskedInput()
    input.type("123")
    drainMainQueue()

    input.select(NSRange(location: 0, length: input.displayedText.utf16.count))
    input.type("4")
    input.type("5")
    drainMainQueue()

    XCTAssertEqual(input.rawText, "45")
    XCTAssertEqual(input.displayedText, "+7 (45")
  }

  func test_maskedInput_typesAfterDeletingSelection_whenKeystrokesOutpaceViewUpdate() {
    let input = makeMaskedInput()
    input.type("123")
    drainMainQueue()

    input.select(NSRange(location: 0, length: input.displayedText.utf16.count))
    input.type("")
    input.type("4")
    input.type("5")
    drainMainQueue()

    XCTAssertEqual(input.rawText, "45")
    XCTAssertEqual(input.displayedText, "+7 (45")
  }

  func test_maskedInput_collapsesSelection_afterDeletingIt() {
    for multiLineMode in [false, true] {
      let input = makeMaskedInput(multiLineMode: multiLineMode)
      input.type("123456")
      drainMainQueue()

      input.select(NSRange(location: 5, length: 2))
      input.type("")
      drainMainQueue()

      let mode = "multiLineMode: \(multiLineMode)"
      XCTAssertEqual(input.selectedRange, NSRange(location: 5, length: 0), mode)

      input.type("9")
      drainMainQueue()

      XCTAssertEqual(input.rawText, "19456", mode)
    }
  }

  func test_maskedInput_appliesNewTypo() {
    let input = makeMaskedInput()
    input.type("1")
    drainMainQueue()

    input.configure(typo: Typo().with(color: .red))
    drainMainQueue()

    XCTAssertEqual(input.displayedTextColor, Color.red.systemColor)
  }

  private func makeMaskedInput(multiLineMode: Bool = false) -> MaskedTextInput {
    let input = MaskedTextInput(multiLineMode: multiLineMode)
    drainMainQueue()
    XCTAssertTrue(input.isFirstResponder)
    return input
  }

  private func drainMainQueue() {
    let drained = expectation(description: "main queue drained")
    DispatchQueue.main.async { drained.fulfill() }
    wait(for: [drained], timeout: 1)
  }
}

private final class MaskedTextInput {
  private let multiLineMode: Bool
  private let text = Property(initialValue: "")
  private let raw = Property(initialValue: "")
  private let view = TextInputBlock.makeBlockView()
  private let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 100))

  var rawText: String { raw.value }

  var displayedText: String {
    multiLineMode ? textView.text : textField.text ?? ""
  }

  var displayedTextColor: UIColor? {
    let attributedText = multiLineMode ? textView.attributedText : textField.attributedText
    guard let attributedText, attributedText.length > 0 else { return nil }
    return attributedText.attribute(.foregroundColor, at: 0, effectiveRange: nil) as? UIColor
  }

  var isFirstResponder: Bool {
    multiLineMode ? textView.isFirstResponder : textField.isFirstResponder
  }

  var selectedRange: NSRange? {
    if multiLineMode {
      return textView.selectedRange
    }
    guard let range = textField.selectedTextRange else { return nil }
    let start = textField.offset(from: textField.beginningOfDocument, to: range.start)
    let end = textField.offset(from: textField.beginningOfDocument, to: range.end)
    return NSRange(location: start, length: end - start)
  }

  private var textField: UITextField {
    view.subviews.compactMap { $0 as? UITextField }.first!
  }

  private var textView: UITextView {
    view.subviews.compactMap { $0 as? UITextView }.first!
  }

  init(multiLineMode: Bool) {
    self.multiLineMode = multiLineMode
    view.frame = window.bounds
    window.addSubview(view)
    window.isHidden = false
    configure(typo: Typo())
    if multiLineMode {
      textView.becomeFirstResponder()
    } else {
      textField.becomeFirstResponder()
    }
  }

  func configure(typo: Typo) {
    let block = TextInputBlock(
      hint: NSAttributedString(),
      textValue: Binding(name: "text", value: text),
      rawTextValue: Binding(name: "raw", value: raw),
      textTypo: typo,
      multiLineMode: multiLineMode,
      maskValidator: MaskValidator(formatter: FixedLengthMaskFormatter(
        pattern: "+7 (###) ###-##-##",
        alwaysVisible: false,
        patternElements: [PatternElement(
          key: "#",
          regex: try! NSRegularExpression(pattern: "\\d"),
          placeholder: "_"
        )]
      )),
      path: "",
      layoutDirection: .leftToRight
    )
    block.configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
  }

  func type(_ string: String) {
    let range = selectedRange ?? NSRange(location: displayedText.utf16.count, length: 0)
    if multiLineMode {
      _ = textView.delegate?.textView?(
        textView,
        shouldChangeTextIn: range,
        replacementText: string
      )
    } else {
      _ = textField.delegate?.textField?(
        textField,
        shouldChangeCharactersIn: range,
        replacementString: string
      )
    }
  }

  func select(_ range: NSRange) {
    if multiLineMode {
      textView.selectedRange = range
    } else if let start = textField.position(
      from: textField.beginningOfDocument,
      offset: range.location
    ), let end = textField.position(from: start, offset: range.length) {
      textField.selectedTextRange = textField.textRange(from: start, to: end)
    }
  }
}
