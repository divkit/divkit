import CoreGraphics

import CommonCorePublic
import LayoutKit

public final class DivLastVisibleBoundsCache {
  private let rwLock = RWLock()

  private var storage: [UIElementPath: CGRect] = [:]

  init() {}

  func lastVisibleBounds(for path: UIElementPath) -> CGRect {
    rwLock.read {
      storage[path] ?? .zero
    }
  }

  func updateLastVisibleBounds(for path: UIElementPath, bounds: CGRect) {
    rwLock.write {
      storage[path] = bounds
    }
  }

  func dropVisibleBounds(forMatchingPrefix prefix: UIElementPath) {
    let prefix = prefix.description + "/"
    rwLock.write {
      storage.forEach {
        if ($0.key.description + "/").starts(with: prefix) {
          storage[$0.key] = nil
        }
      }
    }
  }

  func reset() {
    rwLock.write {
      storage.removeAll()
    }
  }
}
