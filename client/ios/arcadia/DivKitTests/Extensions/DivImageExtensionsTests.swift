// Copyright 2018 Yandex LLC. All rights reserved.

@testable import LayoutKit

import XCTest

import CommonCore
import DivKit

final class DivImageExtensionsTests: XCTestCase {
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

  func test_WhenWidthIsWrapContent_ThrowsError() {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "width_wrap_content"),
      DivBlockModelingError(
        "DivImage has wrap_content width",
        path: .root
      )
    )
  }

  func test_WhenHeightIsWrapContent_ThrowsError() throws {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "height_wrap_content"),
      DivBlockModelingError(
        "DivImage without aspect has wrap_content height",
        path: .root
      )
    )
  }
}

private func makeBlock(fromFile filename: String) throws -> Block {
  try DivImageTemplate.make(
    fromFile: filename,
    subdirectory: "div-image",
    context: .default
  )
}
