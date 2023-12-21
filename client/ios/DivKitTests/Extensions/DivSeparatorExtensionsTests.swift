@testable import DivKit
@testable import LayoutKit

import XCTest

import CommonCorePublic

final class DivSeparatorExtensionsTests: XCTestCase {
  func test_WithDelimiterStyle() throws {
    let block = makeBlock(
      divSeparator(
        delimiterStyle: DivSeparator.DelimiterStyle(
          color: .value(color("#AABBCCDD"))
        )
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: SeparatorBlock(
          color: color("#AABBCCDD")
        ),
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WhenDivHasHorizontalOrientation_AndIntrinsicWidth_ThrowsError() throws {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "horizontal_wrap_content_width"),
      DivBlockModelingError(
        "DivSeparator has wrap_content size by orientation dimension",
        path: .root
      )
    )
  }

  func test_WhenDivHasVerticalOrientation_AndIntrinsicHeight_ThrowsError() throws {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "vertical_wrap_content_height"),
      DivBlockModelingError(
        "DivSeparator has wrap_content size by orientation dimension",
        path: .root
      )
    )
  }
}

private func makeBlock(fromFile filename: String) throws -> Block {
  try DivSeparatorTemplate.make(
    fromFile: filename,
    subdirectory: "div-separator",
    context: .default
  )
}
