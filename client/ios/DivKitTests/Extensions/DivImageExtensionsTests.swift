@testable import DivKit
@testable import LayoutKit

import XCTest

final class DivImageExtensionsTests: XCTestCase {
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
