@testable import DivKit
@testable import LayoutKit

import XCTest

import BaseUIPublic
import CommonCorePublic

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

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextInputBlock(
          hint: NSAttributedString(string: ""),
          textValue: context.makeBinding(variableName: "input_variable", defaultValue: ""),
          textTypo: Typo(font: fontSpecifiers.text.font(weight: .regular, size: 12))
            .with(color: Color.colorWithARGBHexCode(0xFF_00_00_00)),
          path: .root + "0",
          layoutDirection: .leftToRight
        ),
        accessibilityElement: accessibility(label: "Hello!")
      ),
      ids: []
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

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextInputBlock(
          hint: NSAttributedString(string: ""),
          textValue: context.makeBinding(variableName: "input_variable", defaultValue: ""),
          textTypo: Typo(font: fontSpecifiers.text.font(weight: .regular, size: 12))
            .with(color: Color.colorWithARGBHexCode(0xFF_00_00_00)),
          path: .root + "0",
          layoutDirection: .leftToRight
        ),
        accessibilityElement: accessibility(label: "Description")
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }
}
