import Foundation
import LayoutKit
import VGSL

final class DivAnimatorController {
  private struct Entry {
    var animator: Animator
    var definition: DivAnimator
  }

  private var entries = [UIElementPath: Entry]()
  private let lock = AllocatedUnfairLock()

  deinit {
    lock.withLock {
      entries.values.forEach { $0.animator.stop() }
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
      entries[path + id]?.animator.start(
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
      entries[path + id]?.animator.stop()
    }
  }

  func definition(path: UIElementPath, id: String) -> DivAnimator? {
    lock.withLock { entries[path + id]?.definition }
  }

  func initializeIfNeeded(
    path: UIElementPath,
    id: String,
    definition: DivAnimator,
    animator: Variable<Animator?>
  ) {
    lock.withLock {
      let key = path + id
      if let existing = entries[key], existing.animator.isRunning {
        // Keep the running animator instance, but refresh the definition so
        // that the next start() picks up the latest resolved values.
        entries[key] = Entry(animator: existing.animator, definition: definition)
      } else {
        animator.value.map {
          entries[key] = Entry(animator: $0, definition: definition)
        }
      }
    }
  }

  func reset() {
    lock.withLock {
      entries.values.forEach { $0.animator.stop() }
      entries.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      entries = entries.filter {
        if $0.key.cardId == cardId {
          $0.value.animator.stop()
        }
        return $0.key.cardId != cardId
      }
    }
  }
}
