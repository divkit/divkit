@testable import LayoutKit
import VGSL
import XCTest

final class LayeredBlockLayoutTests: XCTestCase {
  func test_WhenChildIsHorizontallyResizable_FrameWidthIsEqualToBoundsWidth() {
    let block = LayeredBlock(children: [horizontallyResizableBlock])

    let blockFrames = block.makeChildrenFrames(size: testSize)

    XCTAssertEqual(blockFrames.first?.width, testSize.width)
  }

  func test_WhenChildIsVerticallyResizable_FrameHeightIsEqualToBoundsHeight() {
    let block = LayeredBlock(children: [verticallyResizableBlock])

    let blockFrames = block.makeChildrenFrames(size: testSize)

    XCTAssertEqual(blockFrames.first?.height, testSize.height)
  }

  func test_WhenChildFillsHeight_StretchesToContainerHeightClampedByMax() {
    // A match_parent-height overlap child is modeled as constrained wrap_content + fillsHeight: it
    // stretches to the container height, but a max_size cap keeps it from growing past its bound.
    func child(maxHeight: CGFloat) -> LayeredBlock.Child {
      LayeredBlock.Child(
        content: TextBlock(
          widthTrait: .intrinsic,
          heightTrait: .intrinsic(constrained: true, minSize: 0, maxSize: maxHeight),
          text: NSAttributedString(string: "x"),
          accessibilityElement: nil
        ),
        fillsHeight: true
      )
    }
    let block = LayeredBlock(children: [child(maxHeight: 80), child(maxHeight: .infinity)])

    let blockFrames = block.makeChildrenFrames(size: testSize)

    // testSize.height == 200: capped child holds 80, unconstrained child fills to 200.
    XCTAssertEqual(blockFrames.map(\.height), [80, 200])
  }

  func test_WhenHorizontalChildrenAlignmentIsCenter_IntrinsicWidthChildIsCentered() {
    let block = LayeredBlock(
      horizontalChildrenAlignment: .center,
      children: [verticallyResizableBlock]
    )

    let blockFrames = block.makeChildrenFrames(size: testSize)

    XCTAssertEqual(
      blockFrames.first?.center.x ?? 0,
      testSize.width / 2,
      accuracy: 1 / PlatformDescription.screenScale()
    )
  }

  func test_WhenHorizontalChildrenAlignmentIsTrailing_IntrinsicWidthChildIsRightAligned() {
    let block = LayeredBlock(
      horizontalChildrenAlignment: .trailing,
      children: [verticallyResizableBlock]
    )

    let blockFrames = block.makeChildrenFrames(size: testSize)

    XCTAssertEqual(blockFrames.first?.maxX, testSize.width)
  }

  func test_WhenVerticalChildrenAlignmentIsCenter_IntrinsicWidthChildIsCentered() {
    let block = LayeredBlock(
      verticalChildrenAlignment: .center,
      children: [horizontallyResizableBlock]
    )

    let blockFrames = block.makeChildrenFrames(size: testSize)

    XCTAssertEqual(
      blockFrames.first?.center.y ?? 0,
      testSize.height / 2,
      accuracy: 1 / PlatformDescription.screenScale()
    )
  }

  func test_WhenVerticalChildrenAlignmentIsTrailing_IntrinsicWidthChildIsBottomAligned() {
    let block = LayeredBlock(
      verticalChildrenAlignment: .trailing,
      children: [horizontallyResizableBlock]
    )

    let blockFrames = block.makeChildrenFrames(size: testSize)

    XCTAssertEqual(blockFrames.first?.maxY, testSize.height)
  }
}

private let horizontallyResizableBlock = SeparatorBlock(direction: .horizontal)
private let verticallyResizableBlock = SeparatorBlock(direction: .vertical)
private let testSize = CGSize(width: 100, height: 200)
