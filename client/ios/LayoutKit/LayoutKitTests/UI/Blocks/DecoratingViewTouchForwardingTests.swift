@testable import LayoutKit
import UIKit
import VGSL
import XCTest

final class DecoratingViewTouchForwardingTests: XCTestCase {
  func test_touchesOnPassiveNestedView_reachActionableAncestor() throws {
    let block = DecoratingBlock(
      child: DecoratingBlock(child: EmptyBlock.zeroSized),
      actions: NonEmptyArray(action("tap")),
      actionAnimation: ActionAnimation(touchDown: [fade(1, 0)], touchUp: [fade(0, 1)]),
      pressStartActions: NonEmptyArray(action("press_start")),
      pressEndActions: NonEmptyArray(action("press_end"))
    )
    let (view, spy) = makeViewInActionSpySuperview(block)
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    window.addSubview(spy)
    window.makeKeyAndVisible()
    view.layoutIfNeeded()

    XCTAssertEqual(
      view.subviews.count,
      1,
      "expected the nested decorating view to be the only subview"
    )
    let nestedView = try XCTUnwrap(view.subviews.first)

    nestedView.touchesBegan([], with: nil)
    pump(0.08)

    XCTAssertEqual(view.alpha, 0, accuracy: 0.01, "touch-down animation did not play")
    XCTAssertEqual(spy.performedPaths, ["press_start"])

    nestedView.touchesEnded([], with: nil)
    pump(0.3)

    XCTAssertEqual(view.alpha, 1, accuracy: 0.01, "touch-up animation did not play")
    XCTAssertEqual(spy.performedPaths, ["press_start", "press_end"])
  }

  func test_passiveView_forwardsTouchesToNextResponder() {
    let block = DecoratingBlock(child: EmptyBlock.zeroSized)
    let (view, superview) = makeViewInTouchCountingSuperview(block)

    view.touchesBegan([], with: nil)
    view.touchesMoved([], with: nil)
    view.touchesEnded([], with: nil)
    view.touchesBegan([], with: nil)
    view.touchesCancelled([], with: nil)

    XCTAssertEqual(superview.began, 2)
    XCTAssertEqual(superview.moved, 1)
    XCTAssertEqual(superview.ended, 1)
    XCTAssertEqual(superview.cancelled, 1)
  }

  func test_actionableView_doesNotForwardTouches() {
    let block = DecoratingBlock(
      child: EmptyBlock.zeroSized,
      actions: NonEmptyArray(action("tap"))
    )
    let (view, superview) = makeViewInTouchCountingSuperview(block)

    view.touchesBegan([], with: nil)
    view.touchesEnded([], with: nil)

    XCTAssertEqual(superview.began, 0)
    XCTAssertEqual(superview.ended, 0)
  }

  func test_animationOnlyView_doesNotForwardTouches() {
    let block = DecoratingBlock(
      child: EmptyBlock.zeroSized,
      actionAnimation: ActionAnimation(touchDown: [fade(1, 0)], touchUp: [fade(0, 1)])
    )
    let (view, superview) = makeViewInTouchCountingSuperview(block)

    view.touchesBegan([], with: nil)
    view.touchesEnded([], with: nil)

    XCTAssertEqual(superview.began, 0)
    XCTAssertEqual(superview.ended, 0)
  }

  private func action(_ path: String) -> UserInterfaceAction {
    UserInterfaceAction(path: UIElementPath(path))
  }

  private func fade(_ start: Double, _ end: Double) -> TransitioningAnimation {
    TransitioningAnimation(
      kind: .fade, start: start, end: end, duration: 0.05, delay: 0, timingFunction: .linear
    )
  }

  private func makeView(_ block: DecoratingBlock) -> BlockView {
    let view = block.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 50, height: 50)
    block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)
    return view
  }

  private func makeViewInTouchCountingSuperview(
    _ block: DecoratingBlock
  ) -> (view: BlockView, superview: TouchCountingView) {
    let view = makeView(block)
    let superview = TouchCountingView(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    superview.addSubview(view)
    return (view, superview)
  }

  private func makeViewInActionSpySuperview(
    _ block: DecoratingBlock
  ) -> (view: BlockView, superview: ActionSpyView) {
    let view = makeView(block)
    let superview = ActionSpyView(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    superview.addSubview(view)
    return (view, superview)
  }

  private func pump(_ seconds: TimeInterval) {
    let end = Date(timeIntervalSinceNow: seconds)
    while Date() < end {
      RunLoop.current.run(mode: .default, before: end)
    }
  }
}

private final class TouchCountingView: UIView {
  private(set) var began = 0
  private(set) var moved = 0
  private(set) var ended = 0
  private(set) var cancelled = 0

  override func touchesBegan(_: Set<UITouch>, with _: UIEvent?) {
    began += 1
  }

  override func touchesMoved(_: Set<UITouch>, with _: UIEvent?) {
    moved += 1
  }

  override func touchesEnded(_: Set<UITouch>, with _: UIEvent?) {
    ended += 1
  }

  override func touchesCancelled(_: Set<UITouch>, with _: UIEvent?) {
    cancelled += 1
  }
}

private final class ActionSpyView: UIView, UIActionEventPerforming {
  private(set) var performedPaths: [String] = []

  func perform(uiActionEvent event: UIActionEvent, from _: AnyObject) {
    performedPaths.append(event.uiAction.path.description)
  }
}
