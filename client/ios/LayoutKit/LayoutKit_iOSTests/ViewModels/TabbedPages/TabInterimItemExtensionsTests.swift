@testable import LayoutKit

import Foundation
import XCTest

import CommonCorePublic

final class TabInterimItemExtensionsTests: XCTestCase {
  func test_InterimElementChangesProportionallyToIndex() {
    let index: CGFloat = 0.5

    let element = elements.interim(at: index)

    let expected = elements[0] / 2 + elements[1] / 2
    XCTAssertTrue(element.isApproximatelyEqualTo(expected))
  }

  func test_WhenIndexIsApproximatelyEqualToInteger_InterimElementIsEqualToItemAtThisIndex() {
    let index: CGFloat = 1

    let element = elements.interim(at: index)

    let expected = elements[1]
    XCTAssertTrue(element.isApproximatelyEqualTo(expected))
  }

  func test_WhenIndexLessThanZero_UsesFirstElement() {
    let index: CGFloat = -0.5

    let element = elements.interim(at: index)

    let expected = elements.first!
    XCTAssertTrue(element.isApproximatelyEqualTo(expected))
  }

  func test_WhenIndexGreaterThanLastIndex_UsesLastElement() {
    let index: CGFloat = 1.5

    let element = elements.interim(at: index)

    let expectedWidth = elements.last!
    XCTAssertTrue(element.isApproximatelyEqualTo(expectedWidth))
  }
}

private let elements: [CGFloat] = [34, 25]
