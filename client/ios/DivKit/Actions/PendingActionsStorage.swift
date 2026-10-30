import Foundation
import LayoutKit
import VGSL

/// Stores actions that could not be performed immediately because their target
/// element was not modeled yet (e.g. it was inside a `gone` subtree and is being
/// revealed by a preceding action in the same batch).
///
/// Such actions are parked here and processed in the next card-update tick, after all
/// synchronously subscribed views have been re-modeled and `IdToPath` has been repopulated.
/// See `DivActionHandler.processCardUpdates`.
final class PendingActionsStorage {
  struct PendingAction {
    let token = UUID()
    let id: String
    let divTypes: Set<String>?
    let extensionId: String?
    let scopePath: UIElementPath?
    let cardId: DivCardID
    let sourcePath: UIElementPath
    var isDeferred = false

    let apply: (IdToPath.Target) -> Void

    var retainsUntilResolved: Bool { extensionId != nil }

    func addressesSameTarget(as other: PendingAction) -> Bool {
      retainsUntilResolved && other.retainsUntilResolved
        && cardId == other.cardId && id == other.id
        && extensionId == other.extensionId && scopePath == other.scopePath
    }

    func addressesExtension(
      id: String, extensionId: String, scopePath: UIElementPath?, cardId: DivCardID
    ) -> Bool {
      self.id == id && self.extensionId == extensionId
        && self.scopePath == scopePath && self.cardId == cardId
    }
  }

  private var storage: [PendingAction] = []
  private var inFlight: [PendingAction] = []
  private var superseded: Set<UUID> = []
  private let lock = AllocatedUnfairLock()

  func enqueue(_ action: PendingAction) {
    lock.withLock {
      storage.removeAll { $0.addressesSameTarget(as: action) }
      storage.append(action)
    }
  }

  func retain(_ action: PendingAction) {
    lock.withLock {
      // Do not overwrite a newer command enqueued while this one was being resolved.
      guard !superseded.contains(action.token),
            !storage.contains(where: { $0.addressesSameTarget(as: action) }) else {
        return
      }
      storage.append(action)
    }
  }

  func takeRetained(cardId: DivCardID) -> [PendingAction] {
    lock.withLock {
      let actions = storage.filter { $0.retainsUntilResolved && $0.cardId == cardId }
      storage.removeAll { $0.retainsUntilResolved && $0.cardId == cardId }
      inFlight.append(contentsOf: actions)
      return actions
    }
  }

  func take() -> [PendingAction] {
    lock.withLock {
      let actions = storage
      storage = []
      inFlight.append(contentsOf: actions.filter(\.retainsUntilResolved))
      return actions
    }
  }

  /// Also invalidates commands taken before modeling, when a trigger can send a newer one.
  func supersedeExtensionAction(
    id: String, extensionId: String, scopePath: UIElementPath?, cardId: DivCardID
  ) {
    lock.withLock {
      storage.removeAll {
        $0.addressesExtension(
          id: id,
          extensionId: extensionId,
          scopePath: scopePath,
          cardId: cardId
        )
      }
      for action in inFlight where action.addressesExtension(
        id: id, extensionId: extensionId, scopePath: scopePath, cardId: cardId
      ) {
        superseded.insert(action.token)
      }
    }
  }

  func isSuperseded(_ action: PendingAction) -> Bool {
    lock.withLock {
      superseded.contains(action.token)
    }
  }

  func finish(_ action: PendingAction) {
    lock.withLock {
      inFlight.removeAll { $0.token == action.token }
      superseded.remove(action.token)
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
