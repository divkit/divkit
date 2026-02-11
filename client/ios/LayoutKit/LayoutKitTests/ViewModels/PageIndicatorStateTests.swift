@testable import LayoutKit
import XCTest

final class PageIndicatorStateTests: XCTestCase {
  func test_EdgeIndicatorInMiddlePosition_WithThreeVisibleItems_UsesInactiveShapeSize() {
    let params = makeParams(position: 4, numberOfPages: 10, maxVisibleItems: 3)
    let state = IndicatorState(
      index: params.head,
      currentPosition: 4,
      params: params,
      numberOfPages: 10
    )

    XCTAssertEqual(state.kind, .normal)
    XCTAssertEqual(state.progress, 1)
  }

  func test_EdgeIndicatorAtStartPosition_WithThreeVisibleItems_UsesInactiveMinimumShapeSize() {
    let params = makeParams(position: 0, numberOfPages: 10, maxVisibleItems: 3)
    let state = IndicatorState(
      index: params.tail + 1,
      currentPosition: 0,
      params: params,
      numberOfPages: 10
    )

    XCTAssertEqual(state.kind, .normal)
    XCTAssertEqual(state.progress, 0)
  }

  func test_EdgeIndicatorAtEndPosition_WithThreeVisibleItems_UsesInactiveMinimumShapeSize() {
    let params = makeParams(position: 9, numberOfPages: 10, maxVisibleItems: 3)
    let state = IndicatorState(
      index: params.head,
      currentPosition: 9,
      params: params,
      numberOfPages: 10
    )

    XCTAssertEqual(state.kind, .normal)
    XCTAssertEqual(state.progress, 0)
  }

  func test_EdgeIndicatorInMiddlePosition_WithFiveVisibleItems_UsesInactiveMinimumShapeSize() {
    let params = makeParams(position: 4, numberOfPages: 10, maxVisibleItems: 5)
    let state = IndicatorState(
      index: params.head,
      currentPosition: 4,
      params: params,
      numberOfPages: 10
    )

    XCTAssertEqual(state.kind, .normal)
    XCTAssertEqual(state.progress, 0)
  }

  private func makeParams(
    position: CGFloat,
    numberOfPages: Int,
    maxVisibleItems: Int
  ) -> PageIndicatorLayerParams {
    PageIndicatorLayerParams(
      numberOfPages: numberOfPages,
      itemPlacement: .stretch(spacing: 0, maxVisibleItems: maxVisibleItems),
      position: position,
      boundsSize: CGSize(width: 120, height: 20)
    )
  }
}
