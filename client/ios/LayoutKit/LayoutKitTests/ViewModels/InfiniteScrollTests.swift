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
}
