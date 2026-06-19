@testable import LayoutKit
@testable import VGSLUI
import XCTest

final class InfiniteScrollTests: XCTestCase {
  func test_newPosition() throws {
    let origins: [CGFloat] = [0, 20, 40, 60, 80]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 1)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 10),
      .init(offset: 70, page: 3)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 9),
      .init(offset: 69, page: 3)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 0),
      .init(offset: 60, page: 3)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 70),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 71),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 81),
      .init(offset: 21, page: 1)
    )
  }

  func test_newPositionWithItemSpacing() throws {
    let origins: [CGFloat] = [0, 21, 42, 63, 84]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 1)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 9),
      .init(offset: 72, page: 3)
    )
  }

  func test_newPositionWithPadding() throws {
    let origins: [CGFloat] = [10, 30, 50, 70, 90]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 1)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 19),
      .init(offset: 79, page: 3)
    )
  }

  func test_newPositionWith3BufferItems() throws {
    let origins: [CGFloat] = [0, 20, 40, 60, 80, 100, 120, 140, 160]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 3)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 60),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 59),
      .init(offset: 119, page: 5)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 120),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 121),
      .init(offset: 61, page: 3)
    )
  }

  func test_newPositionWithLeadingAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 1, alignment: .leading)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 38),
      .init(offset: 158, page: 3)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 39),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 160),
      .init(offset: 40, page: 1)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 159),
      nil
    )
  }

  func test_newPositionWithCenterAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 1, alignment: .center)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 39),
      .init(offset: 159, page: 3)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 40),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 161),
      .init(offset: 41, page: 1)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 160),
      nil
    )
  }

  func test_newPositionWithTrailingAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let scroll = InfiniteScroll(origins: origins, bufferSize: 1, alignment: .trailing)

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 40),
      .init(offset: 160, page: 3)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 41),
      nil
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 162),
      .init(offset: 42, page: 1)
    )
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 161),
      nil
    )
  }

  func test_newPositionWithBoundsSizeAndCenterAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let scroll = InfiniteScroll(
      origins: origins, bufferSize: 1, boundsSize: 60, alignment: .center
    )

    XCTAssertEqual(scroll.getNewPosition(currentOffset: 10), nil)
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 9),
      .init(offset: 129, page: 3)
    )
    XCTAssertEqual(scroll.getNewPosition(currentOffset: 130), nil)
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 131),
      .init(offset: 11, page: 1)
    )
  }

  func test_newPositionWithBoundsSizeAndTrailingAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let scroll = InfiniteScroll(
      origins: origins, bufferSize: 1, boundsSize: 60, alignment: .trailing
    )

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: -20),
      .init(offset: 100, page: 3)
    )
    XCTAssertEqual(scroll.getNewPosition(currentOffset: -19), nil)
    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: 102),
      .init(offset: -18, page: 1)
    )
    XCTAssertEqual(scroll.getNewPosition(currentOffset: 101), nil)
  }

  func test_newPositionWithInsetsAndStartAlignment_lowerBound() throws {
    let origins: [CGFloat] = [10, 50, 90, 130, 170]
    let scroll = InfiniteScroll(
      origins: origins,
      bufferSize: 1,
      alignment: .leading,
      insetMode: .fixed(values: SideInsets(leading: 15, trailing: 10))
    )

    // origins[1] + alignment.correction - leading = 50 + (-1) - 15
    let thresholdOffset: CGFloat = 34

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: thresholdOffset - 1),
      .init(offset: 153, page: 3)
    )

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: thresholdOffset),
      nil
    )
  }

  func test_newPositionWithInsetsAndStartAlignment_upperBound() throws {
    let origins: [CGFloat] = [10, 50, 90, 130, 170]
    let scroll = InfiniteScroll(
      origins: origins,
      bufferSize: 1,
      alignment: .leading,
      insetMode: .fixed(values: SideInsets(leading: 15, trailing: 10))
    )

    // origins[4] + alignment.correction - trailing = 170 + (-1) - 10
    let thresholdOffset: CGFloat = 159

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: thresholdOffset + 1),
      .init(offset: 40, page: 1)
    )

    XCTAssertEqual(
      scroll.getNewPosition(currentOffset: thresholdOffset),
      nil
    )
  }

  func test_getNewPositionForState_lastToFirst_teleportsToBufferedCopyOfLast() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 6),
      newPosition: .paging(index: 2),
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertEqual(teleportedTo, 1)
    XCTAssertEqual(result, .paging(index: 2))
  }

  func test_getNewPositionForState_firstToLast_teleportsToBufferedCopyOfFirst() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 2),
      newPosition: .paging(index: 6),
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertEqual(teleportedTo, 7)
    XCTAssertEqual(result, .paging(index: 6))
  }

  func test_getNewPositionForState_intermediateTransition_returnsNewPositionUnchanged() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 4),
      newPosition: .paging(index: 2),
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertNil(teleportedTo)
    XCTAssertEqual(result, .paging(index: 2))
  }

  func test_getNewPositionForState_nilOldPosition_returnsNil() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: nil,
      newPosition: .paging(index: 2),
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertNil(teleportedTo)
    XCTAssertNil(result)
  }

  // MARK: - getNewPositionForState directional (set_next_item / set_previous_item)

  func test_getNewPositionForState_forward_lastToFirst_teleportsForwardAcrossSeam() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 6),
      newPosition: .paging(index: 2),
      direction: .forward,
      updateToPosition: { teleportedTo = $0 }
    )

    // Teleport just before the first real page, then animate forward onto it.
    XCTAssertEqual(teleportedTo, 1)
    XCTAssertEqual(result, .paging(index: 2))
  }

  func test_getNewPositionForState_forward_normalStep_doesNotTeleport() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 4),
      newPosition: .paging(index: 5),
      direction: .forward,
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertNil(teleportedTo)
    XCTAssertEqual(result, .paging(index: 5))
  }

  func test_getNewPositionForState_backward_firstToLast_teleportsBackwardAcrossSeam() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 2),
      newPosition: .paging(index: 6),
      direction: .backward,
      updateToPosition: { teleportedTo = $0 }
    )

    // Teleport just after the last real page, then animate backward onto it.
    XCTAssertEqual(teleportedTo, 7)
    XCTAssertEqual(result, .paging(index: 6))
  }

  func test_getNewPositionForState_backward_normalStep_doesNotTeleport() {
    let scroll = makePagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 5),
      newPosition: .paging(index: 4),
      direction: .backward,
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertNil(teleportedTo)
    XCTAssertEqual(result, .paging(index: 4))
  }

  // MARK: - Two real items (the pager.json case): firstReal and lastReal are adjacent, so the

  // legacy index-based heuristic cannot tell a normal step from a wrap. Direction disambiguates.

  func test_twoItems_next_fromFirst_animatesForwardWithoutTeleport() {
    let scroll = makeTwoItemPagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 2), // Item 0 (first real)
      newPosition: .paging(index: 3), // Item 1 (last real)
      direction: .forward,
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertNil(teleportedTo)
    XCTAssertEqual(result, .paging(index: 3))
  }

  func test_twoItems_next_fromLast_wrapsForwardAcrossSeam() {
    let scroll = makeTwoItemPagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 3), // Item 1 (last real)
      newPosition: .paging(index: 2), // Item 0 (first real)
      direction: .forward,
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertEqual(teleportedTo, 1) // buffered copy of last → animate forward to 2
    XCTAssertEqual(result, .paging(index: 2))
  }

  func test_twoItems_previous_fromLast_animatesBackwardWithoutTeleport() {
    let scroll = makeTwoItemPagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 3), // Item 1 (last real)
      newPosition: .paging(index: 2), // Item 0 (first real)
      direction: .backward,
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertNil(teleportedTo)
    XCTAssertEqual(result, .paging(index: 2))
  }

  func test_twoItems_previous_fromFirst_wrapsBackwardAcrossSeam() {
    let scroll = makeTwoItemPagerScroll()
    var teleportedTo: CGFloat?

    let result = scroll.getNewPositionForState(
      oldPosition: .paging(index: 2), // Item 0 (first real)
      newPosition: .paging(index: 3), // Item 1 (last real)
      direction: .backward,
      updateToPosition: { teleportedTo = $0 }
    )

    XCTAssertEqual(teleportedTo, 4) // buffered copy of first → animate backward to 3
    XCTAssertEqual(result, .paging(index: 3))
  }

  // MARK: - getNewPositionForState

  // 5 real items + bufferSize 2 → origins indices:
  //   0,1  = bufferedCopiesOfLast (Items 3,4 duplicates)
  //   2..6 = real Items 0..4    (first=2, last=6)
  //   7,8  = bufferedCopiesOfFirst (Items 0,1 duplicates)
  private func makePagerScroll() -> InfiniteScroll {
    InfiniteScroll(
      origins: [0, 20, 40, 60, 80, 100, 120, 140, 160],
      bufferSize: 2
    )
  }

  // 2 real items + bufferSize 2 → origins indices:
  //   0,1 = buffered copies of last (Items 0,1 duplicates)
  //   2,3 = real Items 0,1   (first=2, last=3 — adjacent!)
  //   4,5 = buffered copies of first (Items 0,1 duplicates)
  private func makeTwoItemPagerScroll() -> InfiniteScroll {
    InfiniteScroll(
      origins: [0, 20, 40, 60, 80, 100],
      bufferSize: 2
    )
  }
}
