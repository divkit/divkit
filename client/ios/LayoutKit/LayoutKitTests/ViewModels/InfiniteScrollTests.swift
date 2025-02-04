@testable import LayoutKit
@testable import VGSLUI
import XCTest

final class InfiniteScrollTests: XCTestCase {
  func test_newPosition() throws {
    let origins: [CGFloat] = [0, 20, 40, 60, 80]

    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 10, origins: origins, bufferSize: 1),
      .init(offset: 70, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 9, origins: origins, bufferSize: 1),
      .init(offset: 69, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 0, origins: origins, bufferSize: 1),
      .init(offset: 60, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 70, origins: origins, bufferSize: 1),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 71, origins: origins, bufferSize: 1),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 81, origins: origins, bufferSize: 1),
      .init(offset: 21, page: 1)
    )
  }

  func test_newPositionWithItemSpacing() throws {
    let origins: [CGFloat] = [0, 21, 42, 63, 84]
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 9, origins: origins, bufferSize: 1),
      .init(offset: 72, page: 3)
    )
  }

  func test_newPositionWithPadding() throws {
    let origins: [CGFloat] = [10, 30, 50, 70, 90]
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 19, origins: origins, bufferSize: 1),
      .init(offset: 79, page: 3)
    )
  }

  func test_newPositionWith3BufferItems() throws {
    let origins: [CGFloat] = [0, 20, 40, 60, 80, 100, 120, 140, 160]
    let bufferSize = 3

    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 60, origins: origins, bufferSize: bufferSize),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 59, origins: origins, bufferSize: bufferSize),
      .init(offset: 119, page: 5)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 120, origins: origins, bufferSize: bufferSize),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(currentOffset: 121, origins: origins, bufferSize: bufferSize),
      .init(offset: 61, page: 3)
    )
  }

  func test_newPositionWithLeadingAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let alignment = Alignment.leading

    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 38,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      .init(offset: 158, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 39,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 160,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      .init(offset: 40, page: 1)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 159,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      nil
    )
  }

  func test_newPositionWithCenterAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let alignment = Alignment.center

    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 39,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      .init(offset: 159, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 40,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 161,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      .init(offset: 41, page: 1)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 160,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      nil
    )
  }

  func test_newPositionWithTrailingAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]
    let alignment = Alignment.trailing

    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 40,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      .init(offset: 160, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 41,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 162,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      .init(offset: 42, page: 1)
    )
    XCTAssertEqual(
      InfiniteScroll.getNewPosition(
        currentOffset: 161,
        origins: origins,
        bufferSize: 1,
        alignment: alignment
      ),
      nil
    )
  }

  func test_newPositionWithBoundsSizeAndCenterAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]

    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndCenterAlignmentNewPosition(
        currentOffset: 10,
        origins: origins
      ),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndCenterAlignmentNewPosition(
        currentOffset: 9,
        origins: origins
      ),
      .init(offset: 129, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndCenterAlignmentNewPosition(
        currentOffset: 130,
        origins: origins
      ),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndCenterAlignmentNewPosition(
        currentOffset: 131,
        origins: origins
      ),
      .init(offset: 11, page: 1)
    )
  }

  func test_newPositionWithBoundsSizeAndTrailingAlignment() throws {
    let origins: [CGFloat] = [0, 40, 80, 120, 160]

    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndTrailingAlignmentNewPosition(
        currentOffset: -20,
        origins: origins
      ),
      .init(offset: 100, page: 3)
    )
    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndTrailingAlignmentNewPosition(
        currentOffset: -19,
        origins: origins
      ),
      nil
    )
    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndTrailingAlignmentNewPosition(
        currentOffset: 102,
        origins: origins
      ),
      .init(offset: -18, page: 1)
    )
    XCTAssertEqual(
      InfiniteScroll.oneBufferWithBoundsSizeAndTrailingAlignmentNewPosition(
        currentOffset: 101,
        origins: origins
      ),
      nil
    )
  }
}

extension InfiniteScroll {
  fileprivate static func oneBufferWithBoundsSizeAndCenterAlignmentNewPosition(
    currentOffset: CGFloat,
    origins: [CGFloat]
  ) -> InfiniteScroll.Position? {
    InfiniteScroll.getNewPosition(
      currentOffset: currentOffset,
      origins: origins,
      bufferSize: 1,
      boundsSize: 60,
      alignment: .center
    )
  }

  fileprivate static func oneBufferWithBoundsSizeAndTrailingAlignmentNewPosition(
    currentOffset: CGFloat,
    origins: [CGFloat]
  ) -> InfiniteScroll.Position? {
    InfiniteScroll.getNewPosition(
      currentOffset: currentOffset,
      origins: origins,
      bufferSize: 1,
      boundsSize: 60,
      alignment: .trailing
    )
  }
}
