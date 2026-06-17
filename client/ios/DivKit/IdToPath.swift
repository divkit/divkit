import LayoutKit
import VGSL

final class IdToPath {
  private var idToPath = [UIElementPath: Set<UIElementPath>]()
  private let lock = AllocatedUnfairLock()

  subscript(id: UIElementPath) -> [UIElementPath] {
    lock.withLock {
      idToPath[id].map { Array($0) } ?? []
    }
  }

  func add(_ path: UIElementPath, forId id: UIElementPath) {
    lock.withLock {
      _ = idToPath[id, default: Set<UIElementPath>()].insert(path)
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
