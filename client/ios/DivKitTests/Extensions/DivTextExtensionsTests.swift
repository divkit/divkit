@testable import LayoutKit

import XCTest

import CommonCore
import DivKit

final class DivTextExtensionsTests: XCTestCase {
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
}

private func makeBlock(fromFile filename: String) throws -> Block {
  try DivTextTemplate.make(
    fromFile: filename,
    subdirectory: "div-text",
    context: .default
  )
}
