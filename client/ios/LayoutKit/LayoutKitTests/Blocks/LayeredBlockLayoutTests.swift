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
