import XCTest

@testable import LayoutKit

final class InfiniteScrollTests: XCTestCase {
  func test_newPosition() throws {
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 10, itemsCount: 5, size: 100),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 9, itemsCount: 5, size: 100),
      .init(offset: 69, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 0, itemsCount: 5, size: 100),
      .init(offset: 60, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 70, itemsCount: 5, size: 100),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 71, itemsCount: 5, size: 100),
      .init(offset: 11, page: 1)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 80, itemsCount: 5, size: 100),
      .init(offset: 20, page: 1)
    )
  }
}
