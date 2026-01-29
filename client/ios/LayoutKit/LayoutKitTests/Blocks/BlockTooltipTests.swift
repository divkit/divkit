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
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestView(),
        substrateView: nil,
        bringToTopId: nil
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { _ in nil }
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

  func test_TooltipWithSubstrateView() {
    let substrateView = TestView()
    let tooltipView = TooltipContainerView(
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip_with_substrate",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestView(),
        substrateView: substrateView,
        bringToTopId: nil
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { _ in nil }
    )

    let mainView = UIView()
    mainView.addSubview(tooltipView)

    tooltipView.bounds = CGRect(x: 0.0, y: 0.0, width: 100.0, height: 100.0)
    tooltipView.forceLayout()

    XCTAssertEqual(
      tooltipView.subviews.first,
      substrateView,
      "Substrate view should be first subview (below tooltip)"
    )

    XCTAssertEqual(
      substrateView.frame,
      tooltipView.bounds,
      "Substrate view should fill the entire container"
    )
  }

  func test_GetViewById_ReturnsView_CreatesSnapshot() {
    let targetView = TestView()
    targetView.frame = CGRect(x: 10.0, y: 20.0, width: 50.0, height: 30.0)

    var getViewByIdCallCount = 0

    let substrateView = TestView()
    let tooltipView = TooltipContainerView(
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestView(),
        substrateView: substrateView,
        bringToTopId: "target_view"
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { viewId in
        getViewByIdCallCount += 1
        if viewId.rawValue == "target_view" {
          return targetView
        }
        return nil
      }
    )

    let mainView = UIView()
    mainView.addSubview(tooltipView)
    tooltipView.bounds = CGRect(x: 0.0, y: 0.0, width: 100.0, height: 100.0)
    tooltipView.forceLayout()

    XCTAssertEqual(
      tooltipView.subviews.count,
      3,
      "Should have substrate, snapshot, and tooltip views"
    )
    XCTAssertEqual(getViewByIdCallCount, 1)
    XCTAssertTrue(tooltipView.subviews[0] === substrateView, "First subview should be substrate")
    XCTAssertTrue(
      tooltipView.subviews[1] !== targetView,
      "Second subview should be snapshot, not original view"
    )
    XCTAssertFalse(
      tooltipView.subviews[1].isUserInteractionEnabled,
      "Snapshot should not be user-interactive"
    )
  }

  func test_BringToTopId_WithoutSubstrateView_DoesNotCreateSnapshot() {
    let targetView = TestView()
    targetView.frame = CGRect(x: 10.0, y: 20.0, width: 50.0, height: 30.0)

    var getViewByIdCallCount = 0

    let tooltipView = TooltipContainerView(
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestView(),
        substrateView: nil,
        bringToTopId: "target_view"
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { viewId in
        getViewByIdCallCount += 1
        if viewId.rawValue == "target_view" {
          return targetView
        }
        return nil
      }
    )

    let mainView = UIView()
    mainView.addSubview(tooltipView)
    tooltipView.bounds = CGRect(x: 0.0, y: 0.0, width: 100.0, height: 100.0)
    tooltipView.forceLayout()

    XCTAssertEqual(
      getViewByIdCallCount,
      0,
      "getViewById should not be called without substrate view"
    )
    XCTAssertEqual(tooltipView.subviews.count, 1, "Should only have tooltip view")
  }

  func test_BringToTopId_NilValue_DoesNotCallGetViewById() {
    var getViewByIdCallCount = 0

    let substrateView = TestView()
    let tooltipView = TooltipContainerView(
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestView(),
        substrateView: substrateView,
        bringToTopId: nil
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { _ in
        getViewByIdCallCount += 1
        return nil
      }
    )

    let mainView = UIView()
    mainView.addSubview(tooltipView)
    tooltipView.bounds = CGRect(x: 0.0, y: 0.0, width: 100.0, height: 100.0)
    tooltipView.forceLayout()

    XCTAssertEqual(
      getViewByIdCallCount,
      0,
      "getViewById should not be called when bringToTopId is nil"
    )
    XCTAssertEqual(tooltipView.subviews.count, 2, "Should have substrate and tooltip views")
  }

  func test_SnapshotView_UpdatesFrameOnLayout() {
    let window = UIWindow(frame: CGRect(x: 0.0, y: 0.0, width: 400.0, height: 600.0))

    let targetView = TestView()
    targetView.frame = CGRect(x: 50.0, y: 100.0, width: 80.0, height: 40.0)

    let containerView = UIView(frame: window.bounds)
    window.addSubview(containerView)
    containerView.addSubview(targetView)

    let substrateView = TestView()
    let tooltipView = TooltipContainerView(
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestView(),
        substrateView: substrateView,
        bringToTopId: "target_view"
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { viewId in
        if viewId.rawValue == "target_view" {
          return targetView
        }
        return nil
      }
    )

    window.addSubview(tooltipView)
    tooltipView.frame = window.bounds
    tooltipView.layoutIfNeeded()

    let snapshotView = tooltipView.subviews[1]
    let expectedFrame = tooltipView.convert(
      targetView.convert(targetView.bounds, to: window),
      from: window
    )

    XCTAssertEqual(
      snapshotView.frame,
      expectedFrame,
      "Snapshot frame should match target view position in window"
    )
  }
}

extension CGRect {
  fileprivate func isInside(_ boundsRect: CGRect) -> Bool {
    intersection(boundsRect) == self
  }
}

fileprivate func makeTooltip(offset: CGPoint, block: Block) -> BlockTooltip {
  BlockTooltip(
    block: block,
    params: BlockTooltipParams(
      id: "tooltip",
      mode: .modal,
      duration: 0,
      closeByTapOutside: true
    ),
    offset: offset,
    position: .center
  )
}

fileprivate class TestView: UIView, BlockViewProtocol {
  var effectiveBackgroundColor: UIColor?

  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
}
