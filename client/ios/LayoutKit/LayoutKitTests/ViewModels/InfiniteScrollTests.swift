import XCTest

@testable import LayoutKit

final class InfiniteScrollTests: XCTestCase {
  func test_newPosition() throws {
    let origins: [CGFloat] = [0, 20, 40, 60, 80]
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 10, origins: origins),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 9, origins: origins),
      .init(offset: 69, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 0, origins: origins),
      .init(offset: 60, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 70, origins: origins),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 71, origins: origins),
      .init(offset: 11, page: 1)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 80, origins: origins),
      .init(offset: 20, page: 1)
    )
  }

  func test_newPositionWithItemSpacing() throws {
    let origins: [CGFloat] = [0, 21, 42, 63, 84]
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 9, origins: origins),
      .init(offset: 72, page: 3)
    )
  }

  func test_newPositionWithPadding() throws {
    let origins: [CGFloat] = [10, 30, 50, 70, 90]
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 19, origins: origins),
      .init(offset: 79, page: 3)
    )
  }
}
