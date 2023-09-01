@testable import DivKit
@testable import LayoutKit

import XCTest

import CommonCorePublic

final class DivStateExtensionsTests: XCTestCase {
  private let timerScheduler = TestTimerScheduler()
  func test_WhenVisibilityWasTriggeredAndStateChanges_ReportsVisibilityAgain() throws {
    let context = DivBlockModelingContext(scheduler: timerScheduler)

    let block = try makeBlock(fromFile: "states_visibility", context: context)
    let rect = CGRect(
      origin: .zero,
      size: CGSize(width: 100, height: block.intrinsicContentHeight(forWidth: 100))
    )
    let view = block.makeBlockView()
    XCTAssertEqual(getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timerScheduler), 1)

    context.stateManager.setState(stateBlockPath: "mystate", stateID: "second")

    let block2 = try makeBlock(fromFile: "states_visibility", context: context)
    let view2 = block2.reuse(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: nil
    )
    XCTAssertEqual(getViewVisibilityCallCount(view: view2, rect: rect, timerScheduler: timerScheduler), 1)
  }

  func test_WhenVisibilityWasTriggeredAndStateDoesNotChange_DoesNotReportVisibilityAgain() throws {
    let context = DivBlockModelingContext(scheduler: timerScheduler)

    let block = try makeBlock(fromFile: "states_visibility", context: context)
    let rect = CGRect(
      origin: .zero,
      size: CGSize(width: 100, height: block.intrinsicContentHeight(forWidth: 100))
    )
    let view = block.makeBlockView()
    XCTAssertEqual(getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timerScheduler), 1)

    let block2 = try makeBlock(fromFile: "states_visibility", context: context)
    let view2 = block2.reuse(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: nil
    )
    XCTAssertEqual(getViewVisibilityCallCount(view: view2, rect: rect, timerScheduler: timerScheduler), 0)
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivStateTemplate.make(
    fromFile: filename,
    subdirectory: "div-state",
    context: context
  )
}
