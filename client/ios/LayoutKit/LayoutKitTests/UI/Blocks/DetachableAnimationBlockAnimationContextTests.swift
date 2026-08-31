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
    animationChange: ChangeBoundsTransition? = nil
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
    window.layoutIfNeeded()
    return (view, parent, window)
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
