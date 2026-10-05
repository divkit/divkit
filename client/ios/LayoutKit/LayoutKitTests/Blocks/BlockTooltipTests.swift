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

  func test_CloseRequestedDuringAppear_BlocksInteraction() throws {
    var handledActionCount = 0
    var closeCallCount = 0
    let contentView = TestView(frame: CGRect(x: 20, y: 20, width: 100, height: 100))
    let outsideActions = [
      UserInterfaceAction(path: UIElementPath("outside_1")),
      UserInterfaceAction(path: UIElementPath("outside_2")),
    ]
    var tooltipView: TooltipContainerView!
    tooltipView = makeTooltipContainer(
      view: contentView,
      tapOutsideActions: outsideActions,
      animationIn: [makeAnimation(kind: .fade, start: 0, end: 1, duration: 1)],
      handleAction: { _ in
        handledActionCount += 1
        if handledActionCount == 1 {
          tooltipView.close(animated: true)
        }
      }
    ) {
      closeCallCount += 1
    }
    let (window, previousKeyWindow) = addToWindow(tooltipView)
    defer {
      tooltipView.close(animated: false)
      window.isHidden = true
      previousKeyWindow?.makeKeyAndVisible()
    }
    tooltipView.layoutIfNeeded()

    tooltipView.animateAppear()
    let backgroundElement = try XCTUnwrap(
      tooltipView.accessibilityElements?.last as? UIAccessibilityElement
    )
    _ = backgroundElement.accessibilityActivate()
    XCTAssertEqual(handledActionCount, 2)
    XCTAssertNil(tooltipView.accessibilityElements)

    let pointOutsideContent = CGPoint(
      x: contentView.frame.minX - 1,
      y: contentView.frame.minY - 1
    )
    XCTAssertTrue(tooltipView.hitTest(pointOutsideContent, with: nil) === tooltipView)
    tooltipView.perform(
      uiActionEvent: UIActionEvent(
        uiAction: UserInterfaceAction(path: UIElementPath("test")),
        originalSender: contentView
      ),
      from: contentView
    )
    XCTAssertEqual(handledActionCount, 3)

    tooltipView.close(animated: false)
    XCTAssertNil(tooltipView.superview)
    XCTAssertEqual(closeCallCount, 1)
  }

  func test_AnimatedCloseWithSubstrate_CompletesSynchronouslyOutsideWindow() {
    var closeCallCount = 0
    let contentView = TestView(frame: CGRect(x: 20, y: 20, width: 100, height: 100))
    let substrateView = TestView()
    let tooltipView = makeTooltipContainer(
      view: contentView,
      substrateView: substrateView,
      animationOut: [makeAnimation(kind: .fade, start: 1, end: 0, duration: 1)]
    ) {
      closeCallCount += 1
    }
    let parentView = UIView()
    parentView.addSubview(tooltipView)
    layout(tooltipView)

    tooltipView.animateAppear()
    tooltipView.close(animated: true)

    XCTAssertEqual(contentView.closeVisibilityChangeCount, 1)
    XCTAssertEqual(substrateView.closeVisibilityChangeCount, 1)
    XCTAssertNil(tooltipView.superview)
    XCTAssertEqual(closeCallCount, 1)
  }

  func test_SubstrateCloseOpacity_UsesCurrentPresentationValueWhileAppearing() {
    XCTAssertEqual(
      TooltipContainerView.substrateStartOpacity(
        presentationOpacity: 0.42,
        modelOpacity: 1,
        isAppearing: true
      ),
      0.42,
      accuracy: 0.001
    )
  }

  func test_SubstrateCloseOpacity_DoesNotJumpToModelValueBeforePresentationStarts() {
    XCTAssertEqual(
      TooltipContainerView.substrateStartOpacity(
        presentationOpacity: nil,
        modelOpacity: 1,
        isAppearing: true
      ),
      0
    )
  }

  func test_NonModalTooltip_CloseRequestedDuringAppear_DoesNotConsumeTouches() {
    let tooltipView = makeTooltipContainer(
      mode: .nonModal,
      view: TestView(frame: CGRect(x: 20, y: 20, width: 100, height: 100)),
      animationIn: [makeAnimation(kind: .fade, start: 0, end: 1, duration: 1)]
    ) {}
    let (window, previousKeyWindow) = addToWindow(tooltipView)
    defer {
      tooltipView.close(animated: false)
      window.isHidden = true
      previousKeyWindow?.makeKeyAndVisible()
    }

    tooltipView.animateAppear()
    tooltipView.close(animated: true)

    XCTAssertNil(tooltipView.hitTest(CGPoint(x: 5, y: 5), with: nil))
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

  func test_ModalTooltip_WithCloseByTapOutside_ExportsBackgroundAccessibilityElement() {
    let tooltipView = makeTooltipContainerView(
      mode: .modal,
      closeByTapOutside: true,
      backgroundAccessibilityDescription: "Close tooltip"
    )
    layout(tooltipView)

    let backgroundElement = tooltipView.accessibilityElements?.last as? UIAccessibilityElement
    XCTAssertEqual(tooltipView.accessibilityElements?.count, 2)
    XCTAssertEqual(backgroundElement?.accessibilityLabel, "Close tooltip")
    XCTAssertEqual(backgroundElement?.accessibilityTraits, .button)
    XCTAssertEqual(backgroundElement?.accessibilityFrameInContainerSpace, tooltipView.bounds)
  }

  func test_ModalTooltip_WithoutCloseByTapOutside_DoesNotExportBackgroundAccessibilityElement() {
    let tooltipView = makeTooltipContainerView(mode: .modal, closeByTapOutside: false)
    layout(tooltipView)

    XCTAssertNil(tooltipView.accessibilityElements)
  }

  func test_NonModalTooltip_WithCloseByTapOutside_DoesNotExportBackgroundAccessibilityElement() {
    let tooltipView = makeTooltipContainerView(
      mode: .nonModal,
      closeByTapOutside: true,
      backgroundAccessibilityDescription: "Close tooltip"
    )
    layout(tooltipView)

    XCTAssertNil(tooltipView.accessibilityElements)
  }

  func test_NonModalTooltip_WithoutCloseByTapOutside_DoesNotExportBackgroundAccessibilityElement() {
    let tooltipView = makeTooltipContainerView(mode: .nonModal, closeByTapOutside: false)
    layout(tooltipView)

    XCTAssertNil(tooltipView.accessibilityElements)
  }

  func test_NonModalTooltip_HitTestOutsideTooltip_ReturnsNil() {
    for closeByTapOutside in [true, false] {
      let tooltipView = makeTooltipContainerView(
        mode: .nonModal,
        closeByTapOutside: closeByTapOutside
      )
      layout(tooltipView)

      XCTAssertNil(tooltipView.hitTest(pointOutsideTooltip, with: nil))
    }
  }

  func test_ModalTooltip_HitTestOutsideTooltip_ReturnsContainer() {
    let tooltipView = makeTooltipContainerView(mode: .modal, closeByTapOutside: true)
    layout(tooltipView)

    XCTAssertTrue(tooltipView.hitTest(pointOutsideTooltip, with: nil) === tooltipView)
  }

  private func layout(_ tooltipView: TooltipContainerView) {
    tooltipView.frame = boundsRect
    tooltipView.forceLayout()
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

fileprivate let pointOutsideTooltip = CGPoint(x: 20.0, y: 20.0)

fileprivate func makeTooltipContainerView(
  mode: BlockTooltip.Mode,
  closeByTapOutside: Bool,
  backgroundAccessibilityDescription: String? = nil
) -> TooltipContainerView {
  TooltipContainerView(
    tooltip: DefaultTooltipManager.Tooltip(
      params: BlockTooltipParams(
        id: "tooltip",
        mode: mode,
        duration: 0,
        closeByTapOutside: closeByTapOutside,
        backgroundAccessibilityDescription: backgroundAccessibilityDescription
      ),
      view: TestView(frame: CGRect(x: 100.0, y: 100.0, width: 100.0, height: 100.0)),
      substrateView: nil,
      bringToTopId: nil
    ),
    handleAction: { _ in },
    onCloseAction: {},
    getViewById: { _ in nil }
  )
}

fileprivate func makeTooltipContainer(
  mode: BlockTooltip.Mode = .modal,
  view: TestView = TestView(),
  substrateView: TestView? = nil,
  tapOutsideActions: [UserInterfaceAction] = [],
  animationIn: [TransitioningAnimation]? = nil,
  animationOut: [TransitioningAnimation]? = nil,
  handleAction: @escaping (UIActionEvent) -> Void = { _ in },
  onCloseAction: @escaping () -> Void
) -> TooltipContainerView {
  TooltipContainerView(
    tooltip: DefaultTooltipManager.Tooltip(
      params: BlockTooltipParams(
        id: "tooltip",
        mode: mode,
        duration: 0,
        closeByTapOutside: true,
        tapOutsideActions: tapOutsideActions,
        animationIn: animationIn,
        animationOut: animationOut
      ),
      view: view,
      substrateView: substrateView,
      bringToTopId: nil
    ),
    handleAction: handleAction,
    onCloseAction: onCloseAction,
    getViewById: { _ in nil }
  )
}

fileprivate func makeAnimation(
  kind: TransitioningAnimation.Kind,
  start: Double,
  end: Double,
  duration: TimeInterval
) -> TransitioningAnimation {
  TransitioningAnimation(
    kind: kind,
    start: start,
    end: end,
    duration: duration,
    delay: 0,
    timingFunction: .linear
  )
}

@discardableResult
fileprivate func addToWindow(_ tooltipView: TooltipContainerView) -> (UIWindow, UIWindow?) {
  let previousKeyWindow = UIApplication.shared.connectedScenes
    .compactMap { $0 as? UIWindowScene }
    .flatMap(\.windows)
    .first(where: \.isKeyWindow)
  let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 320, height: 480))
  tooltipView.frame = window.bounds
  window.addSubview(tooltipView)
  window.makeKeyAndVisible()
  return (window, previousKeyWindow)
}

fileprivate class TestView: UIView, BlockViewProtocol {
  var effectiveBackgroundColor: UIColor?
  var visibleBoundsChanges: [(from: CGRect, to: CGRect)] = []

  var closeVisibilityChangeCount: Int {
    visibleBoundsChanges.filter { $0.from != .zero && $0.to == .zero }.count
  }

  func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    visibleBoundsChanges.append((from: from, to: to))
  }
}
