@testable import LayoutKit

import XCTest

final class BlockTooltipTests: XCTestCase {
  private let tooltipSize = CGSize(width: 100.0, height: 100.0)
  private let targetRect = CGRect(x: 50.0, y: 50.0, width: 200.0, height: 50.0)
  private let boundsRect = CGRect(x: 0.0, y: 0.0, width: 300.0, height: 500.0)

  private lazy var block: EmptyBlock = .init(
    widthTrait: .fixed(tooltipSize.width),
    heightTrait: .fixed(tooltipSize.height)
  )

  private var testCaseOffsets: [CGPoint] {
    [
      CGPoint(x: 0.0, y: 0.0), // fits
      CGPoint(x: 200.0, y: 0.0), // doesn't fit horisontally
      CGPoint(x: 0.0, y: 450.0), // doesn't fit vertically
    ]
  }

  private func makeTooltip(offset: CGPoint) -> BlockTooltip {
    BlockTooltip(
      id: "tooltip",
      block: block,
      duration: 0,
      offset: offset,
      position: .center
    )
  }

  func test_TooltipThatFitsCalculation() {
    for (index, offset) in testCaseOffsets.enumerated() {
      let tooltip = makeTooltip(offset: offset)

      let resultRect = tooltip.calculateFrame(
        targeting: targetRect,
        constrainedBy: boundsRect
      )

      XCTAssertEqual(
        resultRect.intersection(boundsRect),
        resultRect,
        "The tooltip with offset \(index) doesn't fit on the screen"
      )
    }
  }
}
