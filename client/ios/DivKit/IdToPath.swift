import LayoutKit
import VGSL

final class IdToPath {
  private var idToPath = [UIElementPath: UIElementPath]()
  private let lock = AllocatedUnfairLock()

  subscript(id: UIElementPath) -> UIElementPath? {
    get {
      lock.withLock {
        idToPath[id]
      }
    }
    set {
      lock.withLock {
        idToPath[id] = newValue
      }
    }
  }

  func reset() {
    lock.withLock {
      idToPath.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      idToPath = idToPath.filter { $0.key.cardId != cardId }
    }
  }
}
