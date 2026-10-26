#if os(iOS)
import UIKit
import VGSL

/// Owns a single programmatic scroll. Offset updates use UIScrollView's normal delegate path.
final class ScrollOffsetAnimator {
  private(set) var isRunning = false

  private weak var scrollView: UIScrollView?
  private let clock: ScrollAnimationClock
  private var completion: (() -> Void)?

  private var generation = 0
  private var targetOffset: CGPoint?
  private var endTime: TimeInterval = 0

  init(scrollView: UIScrollView, clock: ScrollAnimationClock = DisplayLinkScrollAnimationClock()) {
    self.scrollView = scrollView
    self.clock = clock
  }

  deinit {
    clock.stop()
  }

  func cancel() {
    generation += 1
    clock.stop()
    completion = nil
    targetOffset = nil
    isRunning = false
  }

  func retarget(to target: CGPoint) {
    guard isRunning, targetOffset != target, scrollView != nil, let completion else {
      return
    }
    let remainingDuration = max(0, endTime - clock.time)
    scroll(to: target, animated: true, duration: remainingDuration, completion: completion)
  }

  func scroll(
    to target: CGPoint,
    animated: Bool,
    duration: TimeInterval?,
    completion: @escaping () -> Void
  ) {
    cancel()
    guard let scrollView else {
      return
    }

    let start = scrollView.contentOffset
    // Also stop a preceding UIKit animation at its current offset.
    scrollView.setContentOffset(start, animated: false)
    let shouldAnimate = animated && UIView.areAnimationsEnabled && duration != 0 && start != target
    guard shouldAnimate else {
      scrollView.setContentOffset(target, animated: false)
      completion()
      return
    }
    guard let duration else {
      scrollView.setContentOffset(target, animated: true)
      return
    }

    animate(from: start, to: target, duration: duration, completion: completion)
  }

  private func animate(
    from start: CGPoint,
    to target: CGPoint,
    duration: TimeInterval,
    completion: @escaping () -> Void
  ) {
    self.completion = completion
    targetOffset = target
    endTime = clock.time + duration
    isRunning = true
    let generation = self.generation
    let startTime = clock.time
    clock.start { [weak self] time in
      guard let self, let scrollView = self.scrollView else {
        return
      }
      let progress = CGFloat(min(1, max(0, (time - startTime) / duration)))
      let easedProgress = progress * progress * (3 - 2 * progress)
      scrollView.setContentOffset(
        CGPoint(
          x: start.x + (target.x - start.x) * easedProgress,
          y: start.y + (target.y - start.y) * easedProgress
        ),
        animated: false
      )
      guard self.generation == generation else {
        return
      }
      if progress == 1 {
        let completion = self.completion
        cancel()
        completion?()
      }
    }
  }
}

extension ScrollOffsetAnimator: ScrollDelegate {
  func onWillBeginDragging(_: ScrollView) {
    cancel()
  }
}

protocol ScrollAnimationClock: AnyObject {
  var time: TimeInterval { get }
  func start(_ tick: @escaping (TimeInterval) -> Void)
  func stop()
}

final class DisplayLinkScrollAnimationClock: ScrollAnimationClock {
  private var displayLink: CADisplayLink?

  var time: TimeInterval { CACurrentMediaTime() }

  func start(_ tick: @escaping (TimeInterval) -> Void) {
    stop()
    let target = ScrollAnimationFrameTarget(tick: tick)
    let displayLink = CADisplayLink(target: target, selector: #selector(target.update(_:)))
    self.displayLink = displayLink
    displayLink.add(to: .main, forMode: .common)
  }

  func stop() {
    displayLink?.invalidate()
    displayLink = nil
  }
}

private final class ScrollAnimationFrameTarget: NSObject {
  private let tick: (TimeInterval) -> Void

  init(tick: @escaping (TimeInterval) -> Void) {
    self.tick = tick
  }

  @objc func update(_ displayLink: CADisplayLink) {
    tick(displayLink.timestamp)
  }
}
#endif
