import Foundation
import LayoutKit
import VGSL

/// Stores animator registrations.
///
/// The lock protects only `entries`. Animator methods are always called after releasing it:
/// `stop()` may synchronously execute actions that enter this controller again.
final class DivAnimatorController {
  enum ActionResult {
    case success
    case notFound
    case ambiguousId
  }

  private struct Entry {
    var animator: Animator
    var definition: DivAnimator
  }

  private var entries = [UIElementPath: Entry]()
  private let lock = AllocatedUnfairLock()

  deinit {
    let dropped = lock.withLock {
      let dropped = Array(entries.values)
      entries.removeAll()
      return dropped
    }
    dropped.filter(\.animator.isRunning).forEach { $0.animator.stop() }
  }

  @discardableResult
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
  ) -> ActionResult {
    let resolution = lock.withLock { () -> (result: ActionResult, animator: Animator?) in
      let matchedEntries = getEntries(path: path, id: id)
      guard matchedEntries.count == 1, let entry = matchedEntries.first else {
        return (matchedEntries.isEmpty ? .notFound : .ambiguousId, nil)
      }
      return (.success, entry.animator)
    }
    resolution.animator?.start(
      startValue: startValue,
      endValue: endValue,
      duration: duration,
      startDelay: startDelay,
      direction: direction,
      progressInterpolator: progressInterpolator,
      repeatCount: repeatCount
    )
    return resolution.result
  }

  @discardableResult
  func stopAnimator(path: UIElementPath, id: String) -> ActionResult {
    let resolution = lock.withLock { () -> (result: ActionResult, animator: Animator?) in
      let matchedEntries = getEntries(path: path, id: id)
      guard matchedEntries.count == 1, let entry = matchedEntries.first else {
        return (matchedEntries.isEmpty ? .notFound : .ambiguousId, nil)
      }
      return (.success, entry.animator)
    }
    resolution.animator?.stop()
    return resolution.result
  }

  func definition(path: UIElementPath, id: String) -> DivAnimator? {
    lock.withLock {
      let matchedEntries = getEntries(path: path, id: id)
      guard matchedEntries.count == 1, let entry = matchedEntries.first else {
        return nil
      }
      return entry.definition
    }
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
    let dropped = lock.withLock {
      let dropped = Array(entries.values)
      entries.removeAll()
      return dropped
    }
    dropped.filter(\.animator.isRunning).forEach { $0.animator.stop() }
  }

  /// Stops and removes every entry of the card: the host's and all of its tooltips'.
  func reset(cardId: DivCardID) {
    reset { $0.cardId == cardId }
  }

  /// Stops and removes only the entries defined by `viewId`'s own elements.
  func reset(viewId: DivViewId) {
    reset { $0.viewId == viewId }
  }

  /// `matches` receives the path of the element that defines the animator, not the entry key:
  /// the key ends with the animator's `id`, which is free-form and may even start with the
  /// reserved `tooltip#` prefix.
  private func reset(where matches: (UIElementPath) -> Bool) {
    let dropped = lock.withLock {
      let partitioned = entries.reduce(
        into: (dropped: [Entry](), kept: [UIElementPath: Entry]())
      ) { result, item in
        if let elementPath = item.key.parent, matches(elementPath) {
          result.dropped.append(item.value)
        } else {
          result.kept[item.key] = item.value
        }
      }
      entries = partitioned.kept
      return partitioned.dropped
    }
    dropped.filter(\.animator.isRunning).forEach { $0.animator.stop() }
  }

  private func getEntries(path: UIElementPath, id: String) -> [Entry] {
    entries.keys
      .filter { $0.starts(with: path) && $0.leaf == id }
      .reduce(into: [Entry]()) { result, key in
        if let entry = entries[key] {
          result.append(entry)
        }
      }
  }
}
