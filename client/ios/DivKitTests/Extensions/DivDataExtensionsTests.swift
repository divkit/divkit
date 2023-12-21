@testable import DivKit
@testable import LayoutKit

import XCTest

import BaseUIPublic
import CommonCorePublic

final class DivDataExtensionsTests: XCTestCase {
  func test_WhenStateIsNil_TakesFirstState() throws {
    let block = try data
      .makeBlock(context: .default)
      .withoutStateBlock()
    let expectedBlock = try data.states[0].div.value
      .makeBlock(context: .default)
    XCTAssertTrue(block == expectedBlock)
  }

  func test_WhenStateIsPresent_TakesCorrespondingState() throws {
    let context = DivBlockModelingContext()
    context.stateManager.setStateWithHistory(
      path: DivData.rootPath,
      stateID: DivStateID(rawValue: "1")
    )
    let block = try data
      .makeBlock(context: context)
      .withoutStateBlock()
    let expectedBlock = try data.states[1].div.value
      .makeBlock(context: .default)
    XCTAssertTrue(block == expectedBlock)
  }

  func test_WhenStateIsNotFound_TakesFirstState() throws {
    let context = DivBlockModelingContext()
    context.stateManager.setStateWithHistory(
      path: DivData.rootPath,
      stateID: DivStateID(rawValue: "100500")
    )
    let block = try data
      .makeBlock(context: context)
      .withoutStateBlock()
    let expectedBlock = try data.states[0].div.value
      .makeBlock(context: .default)
    XCTAssertTrue(block == expectedBlock)
  }

  func test_ActionPathContainsCardId() throws {
    let block = try divData(
      divText(
        actions: [DivAction(logId: "action_log_id")],
        text: "0"
      )
    )
    .makeBlock(context: .default)
    .withoutStateBlock() as? DecoratingBlock

    XCTAssertEqual(block?.actions?.first.path, UIElementPath.root + "action_log_id")
  }

  func test_ActionPathContainsExternalCardLogId() throws {
    let context = DivBlockModelingContext()
      .modifying(cardLogId: "external_card_log_id")
    let block = try divData(
      divText(
        actions: [DivAction(logId: "action_log_id")],
        text: "0"
      )
    )
    .makeBlock(context: context)
    .withoutStateBlock() as? DecoratingBlock

    XCTAssertEqual(
      block?.actions?.first.path,
      UIElementPath("external_card_log_id") + "action_log_id"
    )
  }

  func test_GalleryPathContainsRoot() throws {
    let block = try divData(
      divGallery(
        items: [
          divText(
            text: "0",
            width: divFixedSize(10)
          ),
        ]
      )
    )
    .makeBlock(context: .default)
    .withoutStateBlock() as! WrapperBlock
    let galleryBlock = block.child as! GalleryBlock

    XCTAssertEqual(galleryBlock.model.path, UIElementPath.root + 0 + "gallery")
  }

  func test_WhenStateChanges_ReportsVisibilityForNewState() throws {
    let timerScheduler = TestTimerScheduler()
    let context = DivBlockModelingContext(scheduler: timerScheduler)

    for rootStateId in [nil, "1", "0", "1"] {
      if let rootStateId {
        context.stateManager.setStateWithHistory(
          path: DivData.rootPath,
          stateID: DivStateID(rawValue: rootStateId)
        )
      }

      let block = try makeBlock(fromFile: "root_states_visibility", context: context)

      let rect = CGRect(
        origin: .zero,
        size: CGSize(width: 100, height: block.intrinsicContentHeight(forWidth: 100))
      )
      let view = block.makeBlockView()
      XCTAssertEqual(
        getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timerScheduler),
        1
      )
    }
  }
}

private let data = divData(
  states: [
    .init(div: divText(text: "0"), stateId: 0),
    .init(div: divText(text: "1"), stateId: 1),
    .init(div: divText(text: "2"), stateId: 2),
  ]
)

extension Block {
  fileprivate func withoutStateBlock() -> Block {
    if let stateBlock = self as? StateBlock {
      return stateBlock.child
    }
    return self
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivDataTemplate.make(
    fromFile: filename,
    subdirectory: "div-data",
    context: context
  )
}
