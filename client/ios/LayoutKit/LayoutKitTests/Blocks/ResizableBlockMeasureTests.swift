import LayoutKit
import VGSL
import XCTest

final class ResizableBlockMeasureTests: XCTestCase {
  func test_FillsAvailableSpace() {
    var blockMeasure = ResizableBlockMeasure(
      resizableBlockCount: 11,
      lengthAvailablePerWeightUnit: 20.0 / 11,
      lengthAvailableForResizableBlocks: 20
    )

    var totalLength: CGFloat = 0
    for _ in 0..<11 {
      let blockLength = blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!))
      XCTAssertTrue(blockLength.isApproximatelyEqualTo(blockLength.flooredToScreenScale))
      totalLength += blockLength
    }

    XCTAssertTrue(totalLength.isApproximatelyEqualTo(20))
  }

  func test_DistributedEvenly() {
    var blockMeasure = ResizableBlockMeasure(
      resizableBlockCount: 3,
      lengthAvailablePerWeightUnit: 10,
      lengthAvailableForResizableBlocks: 1000
    )

    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 10)!))
        .isApproximatelyEqualTo(100)
    )
    XCTAssertTrue(blockMeasure.measureNext(.nonResizable).isApproximatelyEqualTo(0))
    XCTAssertTrue(blockMeasure.measureNext(.nonResizable).isApproximatelyEqualTo(0))
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 65)!))
        .isApproximatelyEqualTo(650)
    )
    XCTAssertTrue(blockMeasure.measureNext(.nonResizable).isApproximatelyEqualTo(0))
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 25)!))
        .isApproximatelyEqualTo(250)
    )
  }
}
