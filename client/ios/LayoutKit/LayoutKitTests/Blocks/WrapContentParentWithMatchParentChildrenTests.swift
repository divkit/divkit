import CoreGraphics
import LayoutKit
import VGSL
import XCTest

// Regression suite for match_parent children inside a wrap_content parent: the parent's
// wrap_content must reflect each child's intrinsic content size (clamped by the match_parent
// min/max), not the layout-simulated zero size, and not a forced wrap_content downgrade.
final class WrapContentParentWithMatchParentChildrenTests: XCTestCase {
  func test_noConstraints_heightIsTallestChild() {
    let row = makeRow(leftContent: 80, rightContent: 100)
    XCTAssertEqual(row.intrinsicContentHeight(forWidth: 360), 100)
  }

  func test_leftMinExceedsContent_doesNotShrinkRight() {
    let row = makeRow(leftContent: 80, leftMin: 90, rightContent: 100)
    XCTAssertEqual(row.intrinsicContentHeight(forWidth: 360), 100)
  }

  func test_leftMaxClampsLeft_rightDominates() {
    let row = makeRow(leftContent: 100, leftMax: 80, rightContent: 90)
    XCTAssertEqual(row.intrinsicContentHeight(forWidth: 360), 90)
  }

  func test_rightMinExceedsContent_dominatesLeft() {
    let row = makeRow(leftContent: 80, rightContent: 70, rightMin: 90)
    XCTAssertEqual(row.intrinsicContentHeight(forWidth: 360), 90)
  }

  func test_rightMaxClampsRight_leftLosesToCappedRight() {
    let row = makeRow(leftContent: 80, rightContent: 100, rightMax: 90)
    XCTAssertEqual(row.intrinsicContentHeight(forWidth: 360), 90)
  }

  func test_singleMatchParentItem_minPropagatesToIntrinsicHeight() {
    let leaf = TextBlock(
      widthTrait: .intrinsic,
      heightTrait: .fixed(80),
      text: NSAttributedString(string: "x")
    )
    let item = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      heightTrait: .weighted(.default, minSize: 90, maxSize: .infinity),
      children: [leaf]
    )
    XCTAssertEqual(item.intrinsicContentHeight(forWidth: 200), 90)
  }

  private func makeRow(
    leftContent: CGFloat,
    leftMin: CGFloat = 0,
    leftMax: CGFloat = .infinity,
    rightContent: CGFloat,
    rightMin: CGFloat = 0,
    rightMax: CGFloat = .infinity
  ) -> ContainerBlock {
    func leaf(_ h: CGFloat) -> Block {
      TextBlock(
        widthTrait: .intrinsic,
        heightTrait: .fixed(h),
        text: NSAttributedString(string: "x")
      )
    }
    func item(contentH: CGFloat, minH: CGFloat, maxH: CGFloat) -> Block {
      try! ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
        heightTrait: .weighted(.default, minSize: minH, maxSize: maxH),
        children: [leaf(contentH)]
      )
    }
    return try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .fixed(360),
      heightTrait: .intrinsic,
      children: [
        item(contentH: leftContent, minH: leftMin, maxH: leftMax),
        item(contentH: rightContent, minH: rightMin, maxH: rightMax),
      ]
    )
  }
}
