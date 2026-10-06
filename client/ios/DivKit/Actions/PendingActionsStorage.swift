import LayoutKit
import VGSL

/// Stores actions that could not be performed immediately because their target
/// element was not modeled yet (e.g. it was inside a `gone` subtree and is being
/// revealed by a preceding action in the same batch).
///
/// Such actions are parked here and processed in the next card-update tick, after all
/// synchronously subscribed views have been re-modeled and `IdToPath` has been repopulated.
/// See `DivActionHandler.processPendingActions`.
final class PendingActionsStorage {
  struct PendingAction {
    let id: String
    let divTypes: Set<String>?
    let scopePath: UIElementPath?
    let cardId: DivCardID
    let sourcePath: UIElementPath
    let apply: (UIElementPath) -> Void
  }

  private var storage: [PendingAction] = []
  private let lock = AllocatedUnfairLock()

  func enqueue(_ action: PendingAction) {
    lock.withLock {
      storage.append(action)
    }
  }

  func take() -> [PendingAction] {
    lock.withLock {
      let actions = storage
      storage = []
      return actions
    }
  }

  func contains(cardId: DivCardID) -> Bool {
    lock.withLock {
      storage.contains { $0.cardId == cardId }
    }
  }

  func reset() {
    lock.withLock {
      storage.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      storage.removeAll { $0.cardId == cardId }
    }
  }
}
