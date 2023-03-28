import CommonCorePublic
import LayoutKit

public protocol DivVisibilityCounting {
  func visibilityCount(for path: UIElementPath) -> UInt
  func incrementCount(for path: UIElementPath)
}

public final class DivVisibilityCounter: DivVisibilityCounting, Resetting {
  private var storage: [UIElementPath: UInt] = [:]

  public init() {}

  public func visibilityCount(for path: UIElementPath) -> UInt {
    storage[path] ?? 0
  }

  public func incrementCount(for path: UIElementPath) {
    storage[path] = visibilityCount(for: path) + 1
  }

  public func reset() {
    storage.removeAll()
  }
}
