import LayoutKit
import VGSL

@_spi(Internal)
public final class DivVisibilityCounter {
  private let storage = AllocatedUnfairLock<[UIElementPath: UInt]>(uncheckedState: [:])

  init() {}

  public func reset() {
    storage.withLock { $0.removeAll() }
  }

  public func reset(cardId: DivCardID) {
    storage.withLock { $0 = $0.filter { $0.key.root != cardId.rawValue } }
  }

  func visibilityCount(for path: UIElementPath) -> UInt {
    storage.withLock { $0[path] ?? 0 }
  }

  func incrementCount(for path: UIElementPath) {
    storage.withLock { $0[path] = ($0[path] ?? 0) + 1 }
  }
}
