#if os(iOS)
@testable import LayoutKit
import UIKit
import XCTest

@MainActor
final class ScrollOffsetAnimatorTests: XCTestCase {
  func test_HorizontalScroll_ReachesTargetAtRequestedTime() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)
    var completions = 0

    animator.scroll(to: CGPoint(x: 400, y: 0), animated: true, duration: 2) { completions += 1 }
    XCTAssertEqual(view.contentOffset, .zero)
    clock.advance(to: 1)
    XCTAssertEqual(view.contentOffset, CGPoint(x: 200, y: 0))
    XCTAssertEqual(completions, 0)
    clock.advance(to: 2)
    clock.advance(to: 3)
    XCTAssertEqual(view.contentOffset, CGPoint(x: 400, y: 0))
    XCTAssertEqual(completions, 1)
  }

  func test_VerticalScroll_ReachesTargetAtRequestedTime() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)

    animator.scroll(to: CGPoint(x: 0, y: 400), animated: true, duration: 2) {}
    clock.advance(to: 1)
    XCTAssertEqual(view.contentOffset, CGPoint(x: 0, y: 200))
    clock.advance(to: 2)
    XCTAssertEqual(view.contentOffset, CGPoint(x: 0, y: 400))
  }

  func test_NewAction_StartsAtCurrentOffsetAndCancelsPreviousCompletion() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)
    var cancelledCompletions = 0
    animator
      .scroll(to: CGPoint(x: 400, y: 0), animated: true, duration: 2) { cancelledCompletions += 1 }
    clock.advance(to: 1)

    animator.scroll(to: .zero, animated: true, duration: 2) {}
    clock.advance(to: 2)

    XCTAssertEqual(view.contentOffset, CGPoint(x: 100, y: 0))
    clock.advance(to: 3)
    XCTAssertEqual(view.contentOffset, .zero)
    XCTAssertEqual(cancelledCompletions, 0)
  }

  func test_Drag_CancelsProgrammaticScroll() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)
    animator.scroll(to: CGPoint(x: 400, y: 0), animated: true, duration: 2) {}
    clock.advance(to: 1)

    animator.onWillBeginDragging(view)
    clock.advance(to: 2)

    XCTAssertEqual(view.contentOffset, CGPoint(x: 200, y: 0))
  }

  func test_ZeroDuration_ScrollsInstantly() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)
    var completions = 0

    animator.scroll(to: CGPoint(x: 400, y: 0), animated: true, duration: 0) { completions += 1 }

    XCTAssertEqual(view.contentOffset, CGPoint(x: 400, y: 0))
    XCTAssertEqual(completions, 1)
    XCTAssertFalse(clock.isScheduled)
  }

  func test_NonAnimatedAction_IgnoresDuration() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)

    animator.scroll(to: CGPoint(x: 400, y: 0), animated: false, duration: 2) {}

    XCTAssertEqual(view.contentOffset, CGPoint(x: 400, y: 0))
    XCTAssertFalse(clock.isScheduled)
  }

  func test_GloballyDisabledAnimations_ScrollInstantly() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    let animator = ScrollOffsetAnimator(scrollView: view, clock: clock)
    let wereAnimationsEnabled = UIView.areAnimationsEnabled
    UIView.setAnimationsEnabled(false)
    defer { UIView.setAnimationsEnabled(wereAnimationsEnabled) }

    animator.scroll(to: CGPoint(x: 400, y: 0), animated: true, duration: 2) {}

    XCTAssertEqual(view.contentOffset, CGPoint(x: 400, y: 0))
    XCTAssertFalse(clock.isScheduled)
  }

  func test_ReleasingAnimator_CancelsScheduledFrames() {
    let view = UIScrollView()
    let clock = TestScrollAnimationClock()
    var animator: ScrollOffsetAnimator? = ScrollOffsetAnimator(scrollView: view, clock: clock)
    animator?.scroll(to: CGPoint(x: 400, y: 0), animated: true, duration: 2) {}

    animator = nil
    clock.advance(to: 2)

    XCTAssertEqual(view.contentOffset, .zero)
    XCTAssertFalse(clock.isScheduled)
  }
}

final class TestScrollAnimationClock: ScrollAnimationClock {
  private(set) var time: TimeInterval = 0

  private var tick: ((TimeInterval) -> Void)?

  var isScheduled: Bool { tick != nil }

  func start(_ tick: @escaping (TimeInterval) -> Void) {
    self.tick = tick
  }

  func stop() {
    tick = nil
  }

  func advance(to time: TimeInterval) {
    self.time = time
    tick?(time)
  }
}
#endif
