@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

final class DivInputExtensionsTests: XCTestCase {
  private let variableStorage = DivVariableStorage()
  private var context: DivBlockModelingContext!

  override func setUp() {
    variableStorage.put(name: "input_variable", value: .string("Hello!"))
    context = DivBlockModelingContext(variableStorage: variableStorage)
  }

  func test_WithTextVariable() {
    let block = makeBlock(
      divInput(textVariable: "input_variable"),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(label: "Hello!"),
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAccessibility() {
    let block = makeBlock(
      divInput(
        accessibility: DivAccessibility(description: .value("Description")),
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      accessibilityElement: accessibility(label: "Description"),
      context: context
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPasswordType() {
    let block = makeBlock(
      divInput(
        keyboardType: .password,
        textVariable: "input_variable"
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      autocorrection: false,
      isSecure: true,
      multilineMode: false,
      context: context
    )

    assertEqual(block, expectedBlock)
  }
}

private func makeExpectedBlockBlock(
  accessibilityElement: AccessibilityElement? = nil,
  autocorrection: Bool = true,
  isSecure: Bool = false,
  multilineMode: Bool = true,
  context: DivBlockModelingContext
) -> StateBlock {
  StateBlock(
    child: DecoratingBlock(
      child: TextInputBlock(
        hint: NSAttributedString(string: ""),
        textValue: context.makeBinding(variableName: "input_variable", defaultValue: ""),
        textTypo: Typo(font: fontSpecifiers.text.font(weight: .regular, size: 12))
          .with(color: Color.colorWithARGBHexCode(0xFF_00_00_00)),
        multiLineMode: multilineMode,
        path: .root + "0" + "input",
        layoutDirection: .leftToRight,
        autocorrection: autocorrection,
        isSecure: isSecure
      ),
      accessibilityElement: accessibilityElement ?? accessibility(label: "Hello!")
    ),
    ids: []
  )
}
