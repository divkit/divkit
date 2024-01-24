import CoreGraphics

import CommonCorePublic
import LayoutKit

public final class DivLastVisibleBoundsCache {
  private let rwLock = RWLock()

  private var storage: [UIElementPath: Int] = [:]

  init() {}

  func lastVisibleArea(for path: UIElementPath) -> Int {
    rwLock.read {
      storage[path] ?? .zero
    }
  }

  func updateLastVisibleArea(for path: UIElementPath, area: Int) {
    rwLock.write {
      storage[path] = area
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
