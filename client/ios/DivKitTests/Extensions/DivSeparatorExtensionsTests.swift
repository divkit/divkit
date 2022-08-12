// Copyright 2018 Yandex LLC. All rights reserved.

@testable import LayoutKit

import XCTest

import CommonCore
import DivKit

final class DivSeparatorExtensionsTests: XCTestCase {
  func test_WhenDivHasAction_CreatesBlockWithIt() throws {
    let block = try makeBlock(fromFile: "with_action") as? DecoratingBlock

    XCTAssertEqual(block?.actions, Expected.actions)
  }

  func test_WhenDivHasSetStateAction_CreatesBlockWithIt() throws {
    let block = try makeBlock(
      fromFile: "with_set_state_action"
    ) as? DecoratingBlock

    XCTAssertEqual(block?.actions, Expected.setStateActions)
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
