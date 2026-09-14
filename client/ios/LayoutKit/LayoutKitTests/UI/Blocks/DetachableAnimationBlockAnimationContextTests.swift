@testable import LayoutKit
import UIKit
import VGSL
import XCTest

final class DetachableAnimationBlockAnimationContextTests: XCTestCase {
  private struct AnimationInfo {
    let duration: TimeInterval
    let timingFunction: CAMediaTimingFunction?
  }

  private let ambientDuration: TimeInterval = 5.0
  private let transitionDuration: TimeInterval = 0.1

  func test_ChangeBoundsSeeding_DoesNotInheritAmbientAnimation() {
    let (view, parent, window) = makeHostedView(
      animationChange: ChangeBoundsTransition(
        duration: transitionDuration,
        delay: 0,
        timingFunction: .linear
      )
    )
    defer { window.isHidden = true }

    inAmbientAnimation {
      view.changeBoundsWithAnimation(
        in: window,
        startFrame: CGRect(x: 10, y: 10, width: 30, height: 30)
      )
    }

    guard let flightContainer = view.transitionChangeAnimationContainer else {
      XCTFail("flight container was not created")
      return
    }
    XCTAssertEqual(flightContainer.superview, parent)
    assertOnlyOwnTransitionAnimations(in: flightContainer)
  }

  func test_RemoveWithAnimationSeeding_DoesNotInheritAmbientAnimation() {
    let (view, _, window) = makeHostedView(
      animationOut: [fade(start: 1, end: 0)]
    )
    defer { window.isHidden = true }
    let subviewsBefore = Set(window.subviews)

    inAmbientAnimation {
      view.removeWithAnimation(in: window)
    }

    guard let detachedChild = window.subviews.first(where: { !subviewsBefore.contains($0) })
    else {
      XCTFail("child view was not reparented into the container")
      return
    }
    assertOnlyOwnTransitionAnimations(in: detachedChild)
  }

  func test_AddWithAnimationSeeding_DoesNotInheritAmbientAnimation() {
    let (view, _, window) = makeHostedView(
      animationIn: [fade(start: 0, end: 1)]
    )
    defer { window.isHidden = true }

    inAmbientAnimation {
      view.addWithAnimation()
    }

    assertOnlyOwnTransitionAnimations(in: view)
  }

  func test_ChangeBounds_KeepsAmbientAnimationForPendingParentLayout() {
    let (view, parent, window) = makeHostedView(
      animationChange: ChangeBoundsTransition(
        duration: transitionDuration,
        delay: 0,
        timingFunction: .linear
      )
    )
    defer { window.isHidden = true }
    guard let parent = parent as? LayoutingParentView else {
      XCTFail("unexpected parent type")
      return
    }
    window.layoutIfNeeded()
    parent.siblingFrame = CGRect(x: 5, y: 60, width: 25, height: 25)
    parent.setNeedsLayout()

    inAmbientAnimation {
      view.changeBoundsWithAnimation(
        in: window,
        startFrame: CGRect(x: 10, y: 10, width: 30, height: 30)
      )
    }

    let durations = allAnimations(in: parent.sibling).map(\.duration)
    XCTAssertTrue(
      durations.contains { abs($0 - ambientDuration) < 0.001 },
      "pending parent layout no longer follows the ambient animation (durations: \(durations))"
    )
  }

  func test_ChangeBoundsFromZeroStartFrame_SettlesChildWithoutFlight() {
    let (view, _, window) = makeHostedView(
      animationChange: ChangeBoundsTransition(
        duration: transitionDuration,
        delay: 0,
        timingFunction: .linear
      )
    )
    defer { window.isHidden = true }
    view.subviews.first?.frame = .zero

    inAmbientAnimation {
      view.changeBoundsWithAnimation(in: window, startFrame: .zero)
    }

    XCTAssertNil(
      view.transitionChangeAnimationContainer,
      "no flight expected from a zero start frame"
    )
    XCTAssertEqual(
      view.subviews.first?.frame,
      view.bounds,
      "child must settle at its final geometry"
    )
    XCTAssertTrue(allAnimations(in: view).isEmpty, "settling must not animate")
  }

  func test_FirstLayoutInsideAmbientAnimation_DoesNotAnimateNewChild() {
    let (view, _, window) = makeHostedView(
      animationChange: ChangeBoundsTransition(
        duration: transitionDuration,
        delay: 0,
        timingFunction: .linear
      ),
      performInitialLayout: false
    )
    defer { window.isHidden = true }

    inAmbientAnimation {
      window.layoutIfNeeded()
    }

    guard let childView = view.subviews.first else {
      XCTFail("child view was not added")
      return
    }
    XCTAssertEqual(childView.frame, view.bounds)
    let durations = allAnimations(in: view).map(\.duration)
    XCTAssertTrue(
      durations.isEmpty,
      "first layout of a new child inherited an ambient animation (durations: \(durations))"
    )
  }

  func test_StateSwitchInsideAmbientAnimation_DoesNotAnimateNewSubtree() {
    let initialState = StateBlock(
      child: makeTransitioningItem(size: 40),
      ids: ["item"]
    )
    // A container child cannot reuse the previous DetachableAnimationBlockView,
    // so the switch builds a new subtree whose first layout runs during the
    // switch itself.
    let nextState = StateBlock(
      child: try! ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: .fixed(100),
        heightTrait: .fixed(100),
        children: [makeTransitioningItem(size: 60)]
      ),
      ids: ["item"]
    )
    let view = initialState.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    initialState.configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 200))
    window.addSubview(view)
    window.makeKeyAndVisible()
    window.layoutIfNeeded()
    defer { window.isHidden = true }

    inAmbientAnimation {
      nextState.configureBlockView(
        view,
        observer: nil,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    }

    assertOnlyOwnTransitionAnimations(in: window)
  }

  func test_StateSwitchInsideAmbientAnimation_DoesNotAnimateNewPlainContainers() {
    // Mirrors a real header: the transition element keeps its container view
    // across the switch, and the new state inserts a plain container between
    // them. That container is not a transition element, so only the state
    // switch itself can keep it from inheriting the ambient animation.
    let initialState = StateBlock(
      child: makeContent(child: makeTransitioningItem(size: 40)),
      ids: ["content", "item"]
    )
    let nextState = StateBlock(
      child: makeContent(
        child: try! ContainerBlock(
          layoutDirection: .horizontal,
          widthTrait: .fixed(100),
          heightTrait: .fixed(100),
          children: [makeTransitioningItem(size: 60)]
        )
      ),
      ids: ["content", "item"]
    )
    let view = initialState.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    initialState.configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 200))
    window.addSubview(view)
    window.makeKeyAndVisible()
    window.layoutIfNeeded()
    defer { window.isHidden = true }

    inAmbientAnimation {
      nextState.configureBlockView(
        view,
        observer: nil,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    }

    assertOnlyOwnTransitionAnimations(in: window)
  }

  func test_NestedStateSwitchInsideAmbientAnimation_KeepsOwnTransitionAnimations() {
    // Mirrors the Market overlay: a root `div-state` without transitions is
    // rebound inside the host's animation block, and the switch of a nested
    // `div-state` happens while that rebind builds the tree. The nested switch
    // must not inherit the ambient animation, but its own change_bounds flights
    // must still run (a `performWithoutAnimation` wrapper would swallow them:
    // the flights would complete instantly and the header would snap).
    let initialRoot = StateBlock(
      child: StateBlock(child: makeTransitioningItem(size: 40), ids: ["item"]),
      ids: []
    )
    let nextRoot = StateBlock(
      child: StateBlock(
        child: try! ContainerBlock(
          layoutDirection: .vertical,
          widthTrait: .fixed(100),
          heightTrait: .fixed(100),
          children: [makeTransitioningItem(size: 60)]
        ),
        ids: ["item"]
      ),
      ids: []
    )
    let view = initialRoot.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    initialRoot.configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 200))
    window.addSubview(view)
    window.makeKeyAndVisible()
    window.layoutIfNeeded()
    defer { window.isHidden = true }

    inAmbientAnimation {
      nextRoot.configureBlockView(
        view,
        observer: nil,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    }

    assertOnlyOwnTransitionAnimations(in: window)
  }

  func test_ReusedStateReboundInsideAmbientAnimation_DoesNotLayOutWithStaleBounds() {
    // Mirrors a scaffold card rebound inside the host's animation block: the
    // state view is reconfigured while it still has its old bounds, and the host
    // applies the new size a moment later, still inside the animation. The switch
    // must not lay the subtree out with the stale bounds: the children would take
    // a transient wrong geometry (a match_parent button sized by the old bounds
    // minus the new paddings) and animate from it once the host resizes the view.
    // Nothing may be laid out before the host applies the new size.
    func makeRow(stepperSize: CGFloat) -> StateBlock {
      StateBlock(
        child: try! ContainerBlock(
          layoutDirection: .horizontal,
          widthTrait: .fixed(100),
          heightTrait: .intrinsic,
          children: [
            ContainerBlock.Child(
              content: EmptyBlock(widthTrait: .fixed(50), heightTrait: .resizable)
            ),
            ContainerBlock.Child(
              content: EmptyBlock(widthTrait: .fixed(50), heightTrait: .fixed(stepperSize))
            ),
          ]
        ),
        ids: []
      )
    }
    let initialState = makeRow(stepperSize: 94)
    let nextState = makeRow(stepperSize: 60)
    let view = initialState.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 94)
    initialState.configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 200))
    window.addSubview(view)
    window.makeKeyAndVisible()
    window.layoutIfNeeded()
    defer { window.isHidden = true }

    guard let rowView = view.subviews.first,
          rowView.subviews.count == 2 else {
      XCTFail("state view has no row or the row has no children")
      return
    }
    let resizableChild = rowView.subviews[0]
    let fixedChild = rowView.subviews[1]
    XCTAssertEqual(resizableChild.frame.height, 94)
    XCTAssertEqual(fixedChild.frame.height, 94)

    inAmbientAnimation {
      nextState.configureBlockView(
        view,
        observer: nil,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
      XCTAssertEqual(
        fixedChild.frame.height,
        94,
        "the switch laid the subtree out with the stale bounds; the host has not resized the view yet"
      )
      // The host resizes the card after the rebind, inside the same animation.
      view.frame = CGRect(x: 0, y: 0, width: 100, height: 60)
      view.layoutIfNeeded()
    }

    XCTAssertEqual(rowView.frame.height, 60)
    XCTAssertEqual(resizableChild.frame.height, 60)
    XCTAssertEqual(fixedChild.frame.height, 60)
  }

  // The ambient block deliberately uses a non-linear curve, so inheriting it is
  // distinguishable from the transitions' own linear timing function.
  private func inAmbientAnimation(_ block: @escaping () -> Void) {
    UIView.animate(
      withDuration: ambientDuration,
      delay: 0,
      options: [.curveEaseIn],
      animations: block
    )
  }

  private func makeHostedView(
    animationIn: [TransitioningAnimation]? = nil,
    animationOut: [TransitioningAnimation]? = nil,
    animationChange: ChangeBoundsTransition? = nil,
    performInitialLayout: Bool = true
  ) -> (DetachableAnimationBlockView, UIView, UIWindow) {
    let block = DetachableAnimationBlock(
      child: EmptyBlock(widthTrait: .fixed(40), heightTrait: .fixed(40)),
      id: "test",
      animationIn: animationIn,
      animationOut: animationOut,
      animationChange: animationChange
    )
    let view = block.makeBlockView() as! DetachableAnimationBlockView
    view.frame = CGRect(x: 50, y: 100, width: 40, height: 40)
    block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    let parent = LayoutingParentView(frame: CGRect(x: 0, y: 0, width: 100, height: 200))
    parent.addSubview(view)
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 200))
    window.addSubview(parent)
    window.makeKeyAndVisible()
    if performInitialLayout {
      window.layoutIfNeeded()
    }
    return (view, parent, window)
  }

  private func makeContent(child: Block) -> DetachableAnimationBlock {
    DetachableAnimationBlock(
      child: try! ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: .fixed(100),
        heightTrait: .fixed(100),
        children: [child]
      ),
      id: "content",
      animationIn: nil,
      animationOut: nil,
      animationChange: ChangeBoundsTransition(
        duration: transitionDuration,
        delay: 0,
        timingFunction: .linear
      )
    )
  }

  private func makeTransitioningItem(size: CGFloat) -> DetachableAnimationBlock {
    DetachableAnimationBlock(
      child: EmptyBlock(widthTrait: .fixed(size), heightTrait: .fixed(size)),
      id: "item",
      animationIn: nil,
      animationOut: nil,
      animationChange: ChangeBoundsTransition(
        duration: transitionDuration,
        delay: 0,
        timingFunction: .linear
      )
    )
  }

  private func fade(start: Double, end: Double) -> TransitioningAnimation {
    TransitioningAnimation(
      kind: .fade,
      start: start,
      end: end,
      duration: transitionDuration,
      delay: 0,
      timingFunction: .linear
    )
  }

  private func allAnimations(in view: UIView) -> [AnimationInfo] {
    var result: [AnimationInfo] = []
    var stack: [UIView] = [view]
    while let current = stack.popLast() {
      for key in current.layer.animationKeys() ?? [] {
        if let animation = current.layer.animation(forKey: key) {
          result.append(AnimationInfo(
            duration: animation.duration,
            timingFunction: animation.timingFunction
          ))
        }
      }
      stack += current.subviews
    }
    return result
  }

  // The seeding must produce explicit transition animations only: at least one
  // animation with the transition's own duration, none with the ambient
  // duration, and every transition animation keeping its own linear curve
  // instead of the inherited ambient ease-in one.
  private func assertOnlyOwnTransitionAnimations(in view: UIView) {
    let animations = allAnimations(in: view)
    let ownAnimations = animations.filter { abs($0.duration - transitionDuration) < 0.001 }
    XCTAssertFalse(
      ownAnimations.isEmpty,
      "expected an explicit transition animation with duration \(transitionDuration)"
    )
    let foreignDurations = animations
      .map(\.duration)
      .filter { abs($0 - transitionDuration) >= 0.001 }
    XCTAssertTrue(
      foreignDurations.isEmpty,
      "seeding inherited an ambient animation (durations: \(foreignDurations))"
    )
    for animation in ownAnimations {
      XCTAssertTrue(
        isLinear(animation.timingFunction),
        "transition animation lost its own curve: \(String(describing: animation.timingFunction))"
      )
    }
  }

  private func isLinear(_ timingFunction: CAMediaTimingFunction?) -> Bool {
    guard let timingFunction else { return false }
    var point1 = [Float](repeating: 0, count: 2)
    var point2 = [Float](repeating: 0, count: 2)
    timingFunction.getControlPoint(at: 1, values: &point1)
    timingFunction.getControlPoint(at: 2, values: &point2)
    return abs(point1[0] - point1[1]) < 0.001 && abs(point2[0] - point2[1]) < 0.001
  }
}

private final class LayoutingParentView: UIView {
  let sibling = UIView()
  var siblingFrame: CGRect = .init(x: 5, y: 150, width: 25, height: 25)

  override init(frame: CGRect) {
    super.init(frame: frame)
    addSubview(sibling)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    sibling.frame = siblingFrame
  }
}
