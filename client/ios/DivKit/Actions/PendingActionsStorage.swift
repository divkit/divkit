import LayoutKit
import VGSL

/// Stores actions that could not be performed immediately because their target
/// element was not modeled yet (e.g. it was inside a `gone` subtree and is being
/// revealed by a preceding action in the same batch).
///
/// Such actions are parked here and re-attempted after the next successful card
/// re-model, once `IdToPath` has been repopulated. See `DivActionHandler.applyPendingActions`.
final class PendingActionsStorage {
  struct PendingAction {
    let id: String
    let divTypes: Set<String>?
    let scopePath: UIElementPath?
    let cardId: DivCardID
    let sourcePath: UIElementPath
    let apply: (UIElementPath) -> Void
  }

  private var storage = [DivCardID: [PendingAction]]()
  private let lock = AllocatedUnfairLock()

  func enqueue(_ action: PendingAction) {
    lock.withLock {
      storage[action.cardId, default: []].append(action)
    }
  }

  func take(cardId: DivCardID) -> [PendingAction] {
    lock.withLock {
      let actions = storage[cardId] ?? []
      storage[cardId] = nil
      return actions
    }
  }

  func reset() {
    lock.withLock {
      storage.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      storage[cardId] = nil
    }
  }
}
