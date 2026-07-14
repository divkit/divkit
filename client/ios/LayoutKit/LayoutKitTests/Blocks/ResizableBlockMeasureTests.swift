import LayoutKit
import VGSL
import XCTest

final class ResizableBlockMeasureTests: XCTestCase {
  func test_FillsAvailableSpace() {
    let measures: [ResizableBlockMeasure.Measure] = Array(
      repeating: .resizable(LayoutTrait.Weight(rawValue: 1)!),
      count: 11
    )
    var blockMeasure = ResizableBlockMeasure(
      measures: measures,
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
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 10)!),
        .nonResizable,
        .nonResizable,
        .resizable(LayoutTrait.Weight(rawValue: 65)!),
        .nonResizable,
        .resizable(LayoutTrait.Weight(rawValue: 25)!),
      ],
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

  func test_ResizableWithMargins() {
    let margin = 50.0
    var blockMeasure = ResizableBlockMeasure(
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 10)!, reservedSpace: margin),
        .resizable(LayoutTrait.Weight(rawValue: 65)!),
        .resizable(LayoutTrait.Weight(rawValue: 25)!),
      ],
      lengthAvailableForResizableBlocks: 1000
    )

    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 10)!, reservedSpace: margin))
        .isApproximatelyEqualTo(100 + margin)
    )
  }

  func test_MinSizeClamping() {
    // Weight ratio is 1:1 but the first block has a min that exceeds its proportional share.
    // Expectation: first block gets its min, the second block absorbs the remainder.
    var blockMeasure = ResizableBlockMeasure(
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 80),
        .resizable(LayoutTrait.Weight(rawValue: 1)!),
      ],
      lengthAvailableForResizableBlocks: 100
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 80))
        .isApproximatelyEqualTo(80)
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!))
        .isApproximatelyEqualTo(20)
    )
  }

  func test_MaxSizeClamping() {
    var blockMeasure = ResizableBlockMeasure(
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 30),
        .resizable(LayoutTrait.Weight(rawValue: 1)!),
      ],
      lengthAvailableForResizableBlocks: 100
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 30))
        .isApproximatelyEqualTo(30)
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!))
        .isApproximatelyEqualTo(70)
    )
  }

  func test_AllSaturatedAtMax() {
    // Total max < available — distributed up to maxes; container will have residual space.
    var blockMeasure = ResizableBlockMeasure(
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 20),
        .resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 30),
      ],
      lengthAvailableForResizableBlocks: 100
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 20))
        .isApproximatelyEqualTo(20)
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 30))
        .isApproximatelyEqualTo(30)
    )
  }

  func test_AllSaturatedAtMin() {
    // Total min > available — every block gets its min; container overflows.
    var blockMeasure = ResizableBlockMeasure(
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 80),
        .resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 60),
      ],
      lengthAvailableForResizableBlocks: 100
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 80))
        .isApproximatelyEqualTo(80)
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 60))
        .isApproximatelyEqualTo(60)
    )
  }

  func test_CascadingFreezes() {
    // Two children freeze in sequence; the third absorbs all remaining space.
    var blockMeasure = ResizableBlockMeasure(
      measures: [
        .resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 10),
        .resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 50),
        .resizable(LayoutTrait.Weight(rawValue: 1)!),
      ],
      lengthAvailableForResizableBlocks: 90
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 10))
        .isApproximatelyEqualTo(10)
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 50))
        .isApproximatelyEqualTo(50)
    )
    XCTAssertTrue(
      blockMeasure.measureNext(.resizable(LayoutTrait.Weight(rawValue: 1)!))
        .isApproximatelyEqualTo(30)
    )
  }

  func test_MixedMinMaxViolation_doesNotOverfreezeMax() {
    // A wants its min (70 > its 50 share); freezing A drops the per-weight share to 30, so B's
    // max (45) no longer binds and B takes the remaining 30 instead of being pinned to 45.
    // Equal weights, two blocks, one min-violator and one max-violator, in 100pt.
    let a = ResizableBlockMeasure.Measure.resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 70)
    let b = ResizableBlockMeasure.Measure.resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 45)
    var blockMeasure = ResizableBlockMeasure(
      measures: [a, b],
      lengthAvailableForResizableBlocks: 100
    )
    XCTAssertTrue(blockMeasure.measureNext(a).isApproximatelyEqualTo(70))
    XCTAssertTrue(blockMeasure.measureNext(b).isApproximatelyEqualTo(30))
  }

  func test_MixedMinMaxViolation_orderIndependent() {
    // Same blocks as above but with the max-bounded block first — the result must be identical,
    // proving the freeze rule is order-independent (the old per-pass logic would pin B at 45 here).
    let b = ResizableBlockMeasure.Measure.resizable(LayoutTrait.Weight(rawValue: 1)!, maxSize: 45)
    let a = ResizableBlockMeasure.Measure.resizable(LayoutTrait.Weight(rawValue: 1)!, minSize: 70)
    var blockMeasure = ResizableBlockMeasure(
      measures: [b, a],
      lengthAvailableForResizableBlocks: 100
    )
    XCTAssertTrue(blockMeasure.measureNext(b).isApproximatelyEqualTo(30))
    XCTAssertTrue(blockMeasure.measureNext(a).isApproximatelyEqualTo(70))
  }
}
