import LayoutKit
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
}
