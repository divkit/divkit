import CoreGraphics
import LayoutKit
import CommonCorePublic

public final class DivLastVisibleBoundsCache {
  private let rwLock = RWLock()

  private var storage: [UIElementPath: CGRect] = [:]

  public init() {}

  public func lastVisibleBounds(for path: UIElementPath) -> CGRect {
    rwLock.read {
      storage[path] ?? .zero
    }
  }

  public func updateLastVisibleBounds(for path: UIElementPath, bounds: CGRect) {
    rwLock.write {
      storage[path] = bounds
    }
  }

  public func dropVisibleBounds(forMatchingPrefix prefix: UIElementPath) {
    let prefix = prefix.description + "/"
    rwLock.write {
      storage.forEach {
        if ($0.key.description + "/").starts(with: prefix) {
          storage[$0.key] = nil
        }
      }
    }
  }

  public func reset() {
    rwLock.write {
      storage.removeAll()
    }
  }
}
