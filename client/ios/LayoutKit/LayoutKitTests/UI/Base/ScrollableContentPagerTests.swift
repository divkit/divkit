// Copyright 2017 Yandex LLC. All rights reserved.

import XCTest

import LayoutKit

final class ScrollableContentPagerTests: XCTestCase {
  private let pager = ScrollableContentPager()
  private var isHorizontal = true

  override func invokeTest() {
    for isHorizontal in [false, true] {
      self.isHorizontal = isHorizontal
      super.invokeTest()
    }
  }

  override func setUp() {
    pager.setPageOrigins([0, 100, 250, 300], withPagingEnabled: true, isHorizontal: isHorizontal)
  }

  func test_WhenMovingForward_SnapsToNextPage() {
    pager.setInitialOffset(20)

    let resultOffset = pager.targetPageOffset(forProposedOffset: 170, velocity: 0.1)!

    XCTAssertEqual(resultOffset, 100)
  }

  func test_WhenMovingForwardAndResultPageIsSame_ForcesToNextPage() {
    pager.setInitialOffset(20)

    let resultOffset = pager.targetPageOffset(forProposedOffset: 49, velocity: 0.1)!

    XCTAssertEqual(resultOffset, 100)
  }

  func test_WhenMovingBackward_SnapsToPreviousPage() {
    pager.setInitialOffset(260)

    let resultOffset = pager.targetPageOffset(forProposedOffset: 49, velocity: -0.1)!

    XCTAssertEqual(resultOffset, 0) // over one page
  }

  func test_WhenMovingBackwardAndResultPageIsSame_ForcesToPreviousPage() {
    pager.setInitialOffset(110)

    let resultOffset = pager.targetPageOffset(forProposedOffset: 51, velocity: -0.1)!

    XCTAssertEqual(resultOffset, 0)
  }

  func test_WhenBouncingLeft_SnapsToFirstPage() {
    pager.setInitialOffset(110)

    let resultOffset = pager.targetPageOffset(forProposedOffset: -10, velocity: -0.1)!

    XCTAssertEqual(resultOffset, 0)
  }

  func test_WhenBouncingRight_SnapsToLastPage() {
    pager.setInitialOffset(300)

    let resultOffset = pager.targetPageOffset(forProposedOffset: 400, velocity: 0.1)!

    XCTAssertEqual(resultOffset, 300)
  }

  func test_ForDisabledPaging_TargetPageOffsetFails() {
    pager.setPageOrigins([100, 250], withPagingEnabled: false, isHorizontal: isHorizontal)
    pager.setInitialOffset(100)

    let resultOffset = pager.targetPageOffset(forProposedOffset: 150, velocity: -1)

    XCTAssertNil(resultOffset)
  }

  func test_WithNoInitialOffset_TargetPageOffsetFails() {
    let resultOffset = pager.targetPageOffset(forProposedOffset: 150, velocity: 1)

    XCTAssertNil(resultOffset)
  }
}
