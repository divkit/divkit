import CoreGraphics
import LayoutKit
import VGSL

final class DivLastVisibleBoundsCache {
  private let lock = AllocatedUnfairLock()

  private var storage: [UIElementPath: Int] = [:]
  private var timers: [UIElementPath: PendingVisibilityTimer] = [:]
  private var invisibleElements = Set<UIElementPath>()

  init() {}

  func lastVisibleArea(for path: UIElementPath) -> Int {
    lock.withLock {
      storage[path] ?? .zero
    }
  }

  func updateLastVisibleArea(for path: UIElementPath, area: Int) {
    lock.withLock {
      storage[path] = area
    }
  }

  func visibilityTimer(for path: UIElementPath) -> PendingVisibilityTimer? {
    lock.withLock {
      timers[path]
    }
  }

  func updateVisibilityTimer(for path: UIElementPath, timer: PendingVisibilityTimer?) {
    lock.withLock {
      timers[path] = timer
    }
  }

  func dropVisibleBounds(prefix: UIElementPath) {
    lock.withLock {
      _dropVisibleBounds(prefix: prefix)
    }
  }

  func onBecomeVisible(_ path: UIElementPath) {
    lock.withLock {
      _ = invisibleElements.remove(path)
    }
  }

  func onBecomeInvisible(_ path: UIElementPath) {
    lock.withLock {
      if !invisibleElements.contains(path) {
        _dropVisibleBounds(prefix: path)
        invisibleElements.insert(path)
      }
    }
  }

  func reset() {
    lock.withLock {
      storage.removeAll()
      timers.removeAll()
    }
  }

  private func _dropVisibleBounds(prefix: UIElementPath) {
    for (path, _) in storage {
      if path.starts(with: prefix) {
        storage[path] = nil
      }
    }
    for (path, _) in timers {
      if path.starts(with: prefix) {
        timers[path] = nil
      }
    }
  }
}
