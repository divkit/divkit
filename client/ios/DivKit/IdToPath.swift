import LayoutKit
import VGSL

final class IdToPath {
  private var idToPath = [UIElementPath: [UIElementPath: String?]]()
  private let lock = AllocatedUnfairLock()

  subscript(id: UIElementPath) -> [UIElementPath] {
    paths(forId: id)
  }

  func paths(
    forId id: UIElementPath,
    divTypes: Set<String>? = nil
  ) -> [UIElementPath] {
    lock.withLock {
      guard let pathsWithTypes = idToPath[id] else {
        return []
      }
      guard let divTypes else {
        return Array(pathsWithTypes.keys)
      }
      return pathsWithTypes.compactMap { path, divType -> UIElementPath? in
        guard let divType else {
          return path
        }
        return divTypes.contains(divType) ? path : nil
      }
    }
  }

  func add(_ path: UIElementPath, forId id: UIElementPath, divType: String? = nil) {
    lock.withLock {
      _ = idToPath[id, default: [:]].updateValue(divType, forKey: path)
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
