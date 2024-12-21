@testable import DivKit
@testable import LayoutKit
import VGSL
import XCTest

final class DivGridExtensionsTests: XCTestCase {
  func test_WhenGridHasHorizontalIncompatibleTraits_ThrowsError() throws {
    let expectedError = DivBlockModelingError(
      "Grid block error: cannot create horizontally resizable grid with intrinsic width trait",
      path: gridPath
    )
    XCTAssertThrowsError(
      try makeBlock(fromFile: "incompatible-horizontal-traits"),
      expectedError
    )
  }

  func test_WhenGridHasVerticalIncompatibleTraits_ThrowsError() throws {
    let expectedError = DivBlockModelingError(
      "Grid block error: cannot create vertically resizable grid with intrinsic height trait",
      path: gridPath
    )
    XCTAssertThrowsError(
      try makeBlock(fromFile: "incompatible-vertical-traits"),
      expectedError
    )
  }

  func test_WhenGridDoesNotFormRect_ThrowsError() throws {
    let makeError: (String) -> DivBlockModelingError = {
      DivBlockModelingError($0, path: gridPath)
    }
    let makeEmptyCellError: (_ row: Int, _ column: Int) -> DivBlockModelingError = {
      makeError("Grid block error: empty cell at (\($0), \($1))")
    }
    let makeNoSpaceError: (_ item: Int) -> DivBlockModelingError = {
      makeError("Grid block error: no space for item at index \($0)")
    }

    let fileToError = [
      "invalid-row-span1": makeEmptyCellError(1, 0),
      "invalid-row-span2": makeEmptyCellError(1, 1),
      "invalid-row-span3": makeEmptyCellError(1, 1),
      "invalid-column-span1": makeNoSpaceError(0),
      "invalid-column-span2": makeNoSpaceError(1),
      "invalid-column-span3": makeEmptyCellError(1, 1),
    ]

    try fileToError.forEach {
      XCTAssertThrowsError(try makeBlock(fromFile: $0.key), $0.value)
    }
  }
}

private func makeBlock(fromFile filename: String) throws -> Block {
  try DivGridTemplate.make(
    fromFile: filename,
    subdirectory: "div-grid",
    context: .default
  )
}

private let gridPath = UIElementPath.root + DivGrid.type
