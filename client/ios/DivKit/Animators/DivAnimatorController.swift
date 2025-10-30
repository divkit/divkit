import Foundation
import LayoutKit
import VGSL

final class DivAnimatorController {
  private var animators = [UIElementPath: Animator]()
  private let lock = AllocatedUnfairLock()

  deinit {
    lock.withLock {
      animators.values.forEach { $0.stop() }
    }
  }

  func startAnimator(
    path: UIElementPath,
    id: String,
    startValue: DivVariableValue?,
    endValue: DivVariableValue?,
    duration: Int?,
    startDelay: Int?,
    direction: AnimationDirection?,
    progressInterpolator: ProgressInterpolator?,
    repeatCount: RepeatCount?
  ) {
    lock.withLock {
      animators[path + id]?.start(
        startValue: startValue,
        endValue: endValue,
        duration: duration,
        startDelay: startDelay,
        direction: direction,
        progressInterpolator: progressInterpolator,
        repeatCount: repeatCount
      )
    }
  }

  func stopAnimator(path: UIElementPath, id: String) {
    lock.withLock {
      animators[path + id]?.stop()
    }
  }

  func initializeIfNeeded(path: UIElementPath, id: String, animator: Variable<Animator?>) {
    lock.withLock {
      guard let animator = animator.value else { return }
      animators[path + id] = animator
    }
  }

  func reset() {
    lock.withLock {
      animators.values.forEach { $0.stop() }
      animators.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      animators = animators.filter {
        if $0.key.cardId == cardId {
          $0.value.stop()
        }
        return $0.key.cardId != cardId
      }
    }
  }
}
