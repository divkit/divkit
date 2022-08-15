@testable import LayoutKit

import XCTest

import CommonCore
import DivKit

final class DivGridExtensionsTests: XCTestCase {
  func test_WhenGridHasAction_AddsItToBlock() throws {
    let block = try makeBlock(fromFile: "with-action") as? DecoratingBlock
    let expectedPayload = JSONObject.object(["key": .string("value")])
    let expectedAction = UserInterfaceAction(
      payloads: [
        .json(expectedPayload),
        .divAction(
          params: UserInterfaceAction.DivActionParams(
            action: .object(
              [
                "log_id": .string("test_log_id"),
                "url": .string("https://ya.ru"),
                "payload": expectedPayload,
                "menu_items": .array([.object(["text": .string("menu")])]),
              ]
            ),
            cardId: DivKitTests.cardId.rawValue,
            source: .tap,
            url: URL(string: "https://ya.ru")!
          )
        ),
      ],
      path: .root + "test_log_id"
    )
    XCTAssertEqual(block?.actions, NonEmptyArray(expectedAction))
  }

  func test_WhenGridHasHorizontalIncompatibleTraits_ThrowsError() throws {
    let expectedError = GridBlock.Error(
      payload: .incompatibleLayoutTraits(direction: .horizontal),
      path: gridPath
    )
    XCTAssertThrowsError(
      try makeBlock(fromFile: "incompatible-horizontal-traits"),
      expectedError
    )
  }

  func test_WhenGridHasVerticalIncompatibleTraits_ThrowsError() throws {
    let expectedError = GridBlock.Error(
      payload: .incompatibleLayoutTraits(direction: .vertical),
      path: gridPath
    )
    XCTAssertThrowsError(
      try makeBlock(fromFile: "incompatible-vertical-traits"),
      expectedError
    )
  }

  func test_WhenGridDoesNotFormRect_ThrowsError() throws {
    let makeError: (GridBlock.Error.Payload) -> GridBlock.Error = {
      .init(payload: $0, path: gridPath)
    }
    let makeEmptyCellError: (_ row: Int, _ column: Int) -> GridBlock.Error = {
      makeError(.unableToFormGrid(.emptyCell(at: .init(row: $0, column: $1))))
    }
    let makeNoSpaceError: (_ item: Int) -> GridBlock.Error = {
      makeError(.unableToFormGrid(.noSpaceForItem(at: $0)))
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
    subdirectory: "div-grid"
  )
}

private let gridPath = UIElementPath.root + DivGrid.type
