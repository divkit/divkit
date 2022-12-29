@testable import LayoutKit

import XCTest

import CommonCore
import DivKit

final class DivIndicatorTests: DivKitSnapshotTestCase {
  override func setUp() {
    super.setUp()
    rootDirectory = "snapshot_test_data"
    subdirectory = "div-indicator"
  }

  func test_ActiveSize() {
    testDivs("active_size.json")
  }

  func test_CornersRadius() {
    testDivs("corners_radius.json")
  }

  func test_FixedHeight() {
    testDivs("fixed-height.json")
  }

  func test_FixedWidthMaxItemsCircle() {
    testDivs("fixed-width-max_items_circle.json")
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

  func test_Margins() {
    testDivs("margins.json")
  }

  func test_ParentWidthMaxItems() {
    testDivs("match_parent-width-max_items.json")
  }

  func test_MinimumSize() {
    testDivs("minimum_size.json")
  }

  func test_Paddings() {
    testDivs("paddings.json")
  }

  func test_WrapContentHeight() {
    testDivs("wrap_content-height.json")
  }

  func test_WrapContentWidthMaxItems() {
    testDivs("wrap_content-width-max_items.json")
  }

  func test_StretchItems() {
    testDivs("stretch_items.json")
  }

  func test_StretchItemsMaxItemsConstraint() {
    testDivs("stretch_items_max_items_constraint.json")
  }
}

extension DivIndicatorTests {
  fileprivate func testDivs(
    _ fileName: String,
    functionName: String = #function
  ) {
    testDivs(fileName, testName: functionName, blocksState: defaultPagerViewState)
  }

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

private let defaultPagerViewState = [
  pagerPath: PagerViewState(numberOfPages: 11, currentPage: 1),
]

private let testPagerViewStates = [
  PagerViewState(numberOfPages: 11, floatCurrentPage: 0),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 1.5),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 5.2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 7.7),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 9.4),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 10),
]
