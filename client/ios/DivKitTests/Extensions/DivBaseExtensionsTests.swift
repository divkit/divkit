@testable import LayoutKit

import XCTest

import CommonCorePublic
import DivKit
import NetworkingPublic

final class DivBaseExtensionsTests: XCTestCase {
  private let timer = TestTimerScheduler()

  func test_WhenCreatesBlockAfterItBeingGone_ReportsVisibility() throws {
    try expectVisibilityActionsToRun(
      forVisibleBlockFile: "div-text-visibility-actions-visible",
      invisibleBlockFile: "div-text-visibility-actions-gone"
    )
  }

  func test_WhenCreatesBlockAfterItBeingInvisible_ReportsVisibility() throws {
    try expectVisibilityActionsToRun(
      forVisibleBlockFile: "div-text-visibility-actions-visible",
      invisibleBlockFile: "div-text-visibility-actions-invisible"
    )
  }

  func test_WhenReusesBlockWithAnotherDivCardID_ExpectToCancelPreviousBlockTimers() throws {
    let context = DivBlockModelingContext(scheduler: timer)

    let firstBlock = try makeBlock(fromFile: "div-text-visibility-actions-visible", context: context)

    // trigger visibility actions for first time
    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let view = firstBlock.makeBlockView()
    view.frame = rect
    view.layoutIfNeeded()
    view.onVisibleBoundsChanged(from: rect, to: rect)

    let oldTimers = timer.timers

    let secondBlock = try makeBlock(
      fromFile: "div-text-visibility-actions-visible",
      context: DivBlockModelingContext(
        cardId: "another_cardId",
        stateManager: DivStateManager(),
        imageHolderFactory: ImageHolderFactory(make: { _, _ in FakeImageHolder() })
      )
    )
    let view2 = secondBlock.reuse(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil, superview: nil)
    view2.frame = rect
    view2.layoutIfNeeded()
    view.onVisibleBoundsChanged(from: rect, to: rect)

    XCTAssertEqual(oldTimers.allSatisfy { !$0.isValid }, true)
  }

  func test_WhenBlockHasVisibilityAndDisappearAction_DoNotShareVisibilityCounter() throws {
    let context = DivBlockModelingContext(scheduler: timer)

    let blockVisibleFirst = try makeBlock(fromFile: "div-text-visibility-and-disappear-action", context: context)

    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let view = blockVisibleFirst.makeBlockView()
    XCTAssertEqual(getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timer), 1)

    let invisibleRect: CGRect = .zero
    XCTAssertEqual(getViewVisibilityCallCount(view: view, rect: rect, visibilityRect: invisibleRect, timerScheduler: timer), 1)
  }

  private func expectVisibilityActionsToRun(
    forVisibleBlockFile file: String,
    invisibleBlockFile: String
  ) throws {
    let context = DivBlockModelingContext(scheduler: timer)

    let blockVisibleFirst = try makeBlock(fromFile: file, context: context)

    // trigger visibility actions for first time
    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let view = blockVisibleFirst.makeBlockView()
    XCTAssertEqual(getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timer), 1)

    // expect to drop lastVisibleBounds
    let _ = try makeBlock(fromFile: invisibleBlockFile, context: context)
    let view2 = blockVisibleFirst.makeBlockView()

    XCTAssertEqual(getViewVisibilityCallCount(view: view2, rect: rect, timerScheduler: timer), 1)
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivTextTemplate.make(
    fromFile: filename,
    subdirectory: "div-base",
    context: context
  )
}
