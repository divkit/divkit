@testable import LayoutKit

import XCTest

import CommonCorePublic
import DivKit

final class DivIndicatorTests: DivKitSnapshotTestCase {
  override func setUp() {
    super.setUp()
    rootDirectory = "snapshot_test_data"
    subdirectory = "div-indicator"
  }

  func test_FixedWidthMaxItemsRectangle() {
    testDivsForDifferentStates("fixed-width-max_items_rectangle.json")
  }

  func test_FixedWidthMaxItemsRectangleWorm() {
    testDivsForDifferentStates("fixed-width-max_items_rectangle_worm.json")
  }

  func test_FixedWidthMaxItemsRectangleSlider() {
    testDivsForDifferentStates("fixed-width-max_items_rectangle_slider.json")
  }
}

extension DivIndicatorTests {
  fileprivate func testDivsForDifferentStates(
    _ fileName: String,
    functionName: String = #function
  ) {
    for state in testPagerViewStates {
      let blocksState = [
        pagerPath: state,
      ]

      var testName = functionName
      testName.removeLast(2)
      testName += "_\(state.currentPage)()"
      testDivs(fileName, testName: testName, blocksState: blocksState)
    }
  }
}

private let pagerPath = UIElementPath(testCardId) + "pager_id"

private let testPagerViewStates = [
  PagerViewState(numberOfPages: 11, floatCurrentPage: 0),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 1.5),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 5.2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 7.7),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 9.4),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 10),
]
