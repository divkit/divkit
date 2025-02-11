@testable import LayoutKit
import XCTest

final class BlockTooltipTests: XCTestCase {
  private let tooltipSize = CGSize(width: 100.0, height: 100.0)
  private let targetRect = CGRect(x: 50.0, y: 50.0, width: 200.0, height: 50.0)
  private let boundsRect = CGRect(x: 0.0, y: 0.0, width: 300.0, height: 500.0)

  private var testCaseOffsets: [CGPoint] {
    [
      CGPoint(x: 0.0, y: 0.0), // fits
      CGPoint(x: tooltipSize.width, y: 0.0), // doesn't fit horisontally
      CGPoint(x: -tooltipSize.width, y: 0.0), // doesn't fit horisontally
      CGPoint(x: 0.0, y: tooltipSize.height), // doesn't fit vertically
      CGPoint(x: 0.0, y: -tooltipSize.height), // doesn't fit vertically
    ]
  }

  func test_TooltipThatFitsCalculation() {
    for (index, offset) in testCaseOffsets.enumerated() {
      let tooltip = makeTooltip(
        offset: offset,
        block: EmptyBlock(
          widthTrait: .fixed(tooltipSize.width),
          heightTrait: .fixed(tooltipSize.height)
        )
      )

      let resultRect = tooltip.calculateFrame(
        targeting: targetRect,
        constrainedBy: boundsRect
      )

      XCTAssertTrue(
        resultRect.isInside(boundsRect),
        "The tooltip with offset \(index) doesn't fit on the screen"
      )
    }
  }

  func test_TooltipWindowClose() {
    let tooltipView = TooltipContainerView(
      tooltipView: TestView(),
      closeByTapOutside: true,
      tapOutsideActions: [],
      isModal: true,
      handleAction: { _ in },
      onCloseAction: {}
    )

    let mainView = UIView()
    mainView.addSubview(tooltipView)

    tooltipView.bounds = CGRect(
      x: 0.0, y: 0.0,
      width: 20.0, height: 20.0
    )
    tooltipView.forceLayout()
    XCTAssertEqual(tooltipView.superview, mainView)

    tooltipView.bounds = CGRect(
      x: 0.0, y: 0.0,
      width: 100.0, height: 100.0
    )
    tooltipView.forceLayout()

    let predicate = NSPredicate { _, _ in
      tooltipView.superview == nil
    }

    let expectSuperviewChange = expectation(for: predicate, evaluatedWith: nil)
    wait(for: [expectSuperviewChange], timeout: 2)
  }
}

extension CGRect {
  fileprivate func isInside(_ boundsRect: CGRect) -> Bool {
    intersection(boundsRect) == self
  }
}

fileprivate func makeTooltip(offset: CGPoint, block: Block) -> BlockTooltip {
  BlockTooltip(
    id: "tooltip",
    block: block,
    duration: 0,
    offset: offset,
    position: .center
  )
}

fileprivate class TestView: UIView, BlockViewProtocol {
  var effectiveBackgroundColor: UIColor?

  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
}
