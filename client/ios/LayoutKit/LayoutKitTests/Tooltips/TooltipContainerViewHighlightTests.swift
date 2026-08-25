#if os(iOS)
import Foundation
@testable import LayoutKit
import Testing
import UIKit

@Suite
@MainActor
struct TooltipContainerViewHighlightTests {
  @Test
  func snapshotFollowsHighlightedView_whenItsAncestorMoves() {
    let scene = HighlightScene()

    #expect(scene.containerView.subviews.count == 3)
    #expect(scene.snapshotView.frame == CGRect(x: 50, y: 100, width: 80, height: 40))

    scene.movableView.frame = scene.movableView.frame.offsetBy(dx: 20, dy: 30)
    scene.containerView.layoutIfNeeded()

    #expect(scene.snapshotView.frame == CGRect(x: 70, y: 130, width: 80, height: 40))
  }

  @Test
  func snapshotKeepsLastFrame_whenHighlightedViewLeavesWindow() {
    let scene = HighlightScene()

    #expect(scene.containerView.subviews.count == 3)

    scene.highlightedView.removeFromSuperview()
    scene.containerView.setNeedsLayout()
    scene.containerView.layoutIfNeeded()

    #expect(scene.snapshotView.frame == CGRect(x: 50, y: 100, width: 80, height: 40))
    #expect(!scene.snapshotView.isHidden)
  }

  @Test
  func snapshotStopsFollowingHighlightedView_afterClose() {
    let scene = HighlightScene()

    #expect(scene.containerView.subviews.count == 3)

    scene.containerView.close(animated: true)
    scene.containerView.layoutIfNeeded()
    let frameAfterClose = scene.snapshotView.frame

    scene.movableView.frame = scene.movableView.frame.offsetBy(dx: 20, dy: 30)
    scene.containerView.layoutIfNeeded()

    #expect(scene.snapshotView.frame == frameAfterClose)
  }
}

@MainActor
private final class HighlightScene {
  let window: UIWindow
  let movableView: UIView
  let highlightedView: TestBlockView
  let containerView: TooltipContainerView
  let snapshotView: UIView

  init() {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 400, height: 600))
    let movableView = UIView(frame: window.bounds)
    let highlightedView = TestBlockView(frame: CGRect(x: 50, y: 100, width: 80, height: 40))
    window.addSubview(movableView)
    movableView.addSubview(highlightedView)

    let containerView = TooltipContainerView(
      tooltip: DefaultTooltipManager.Tooltip(
        params: BlockTooltipParams(
          id: "tooltip",
          mode: .modal,
          duration: 0,
          closeByTapOutside: true
        ),
        view: TestBlockView(),
        substrateView: TestBlockView(),
        bringToTopId: "highlighted_view"
      ),
      handleAction: { _ in },
      onCloseAction: {},
      getViewById: { viewId in
        viewId.rawValue == "highlighted_view" ? highlightedView : nil
      }
    )

    window.addSubview(containerView)
    containerView.frame = window.bounds
    containerView.layoutIfNeeded()

    self.window = window
    self.movableView = movableView
    self.highlightedView = highlightedView
    self.containerView = containerView
    snapshotView = containerView.subviews[1]
  }
}

private final class TestBlockView: UIView, BlockViewProtocol {
  var effectiveBackgroundColor: UIColor?

  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}
}
#endif
