import LayoutKit

public protocol DivVisibilityCounting {
  func visibilityCount(for path: UIElementPath) -> UInt
  func incrementCount(for path: UIElementPath)
}

final class DivVisibilityCounter: DivVisibilityCounting {
  private var storage: [UIElementPath: UInt] = [:]

  init() {}

  func visibilityCount(for path: UIElementPath) -> UInt {
    storage[path] ?? 0
  }

  func incrementCount(for path: UIElementPath) {
    storage[path] = visibilityCount(for: path) + 1
  }

  func reset() {
    storage.removeAll()
  }

  func reset(cardId: DivCardID) {
    storage = storage.filter { $0.key.root != cardId.rawValue }
  }
}
