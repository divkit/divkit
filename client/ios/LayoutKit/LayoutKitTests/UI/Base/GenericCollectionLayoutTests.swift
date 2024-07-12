import CoreGraphics
import XCTest

import LayoutKit
import VGSL

final class GenericCollectionLayoutTests: XCTestCase {
  func test_WhenConstructingFromFramesAndPageSize_IncreasesContentSizeToFitLastPageSize() {
    let me = GenericCollectionLayout(frames: frames, pageSize: pageSize)

    XCTAssertEqual(me.contentSize.truncatingRemainder(dividingBy: pageSize), .zero)
  }

  func test_WhenConstructingFromZeroFrameAndPageSize_IncreasesContentSizeToFitLastPageSize() {
    let me = GenericCollectionLayout(frames: [.zero], pageSize: pageSize)

    XCTAssertEqual(me.contentSize.truncatingRemainder(dividingBy: pageSize), .zero)
  }

  func test_WhenConstructingWithoutFramesAndPageSize_IncreasesContentSizeToFitLastPageSize() {
    let me = GenericCollectionLayout(frames: [], pageSize: pageSize)

    XCTAssertEqual(me.contentSize.truncatingRemainder(dividingBy: pageSize), .zero)
  }

  func test_WhenConstructingFromFramesAndZeroPageSize_DoesNotIncreaseContentSize() {
    let frame = frames[0]
    let me = GenericCollectionLayout(frames: [frame], pageSize: .zero)

    XCTAssertEqual(me.contentSize, CGSize(width: frame.maxX, height: frame.maxY))
  }
}

extension CGSize {
  fileprivate func truncatingRemainder(dividingBy other: CGSize) -> CGSize {
    CGSize(
      width: width.truncatingRemainder(dividingBy: other.width),
      height: height.truncatingRemainder(dividingBy: other.height)
    )
  }
}

private let frames: [CGRect] = [
  .init(x: 0, y: 0, width: 100, height: 100),
  .init(x: 0, y: 0, width: 200, height: 200),
]

private let pageSize = CGSize(width: 320, height: 480)
