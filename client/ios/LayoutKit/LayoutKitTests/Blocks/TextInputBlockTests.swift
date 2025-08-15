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
}
