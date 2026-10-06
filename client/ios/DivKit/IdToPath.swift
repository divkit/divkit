import LayoutKit
import VGSL

/// Maps element ids to their paths across a whole card - the host `DivView` and every one of
/// its tooltips, each of which gets a `DivView` of its own.
///
/// Two identifiers meet here, and they are not interchangeable:
///
/// - **write and invalidate by `DivViewId`** (`add`, `reset(viewId:)`), so that each `DivView`
///   can refresh its own registrations without touching a sibling's; a whole card, tooltips
///   included, is dropped by `DivCardID` (`reset(cardId:)`);
/// - **read by `DivCardID`** (`paths(forId:cardId:)`), because addressing is card-wide: an
///   action inside a tooltip may name an element of the host card, and vice versa, exactly as
///   it could before tooltips got a `DivView` of their own.
///
/// The partition is about who may drop a registration, not about who may see it. There is
/// deliberately no view-scoped lookup: narrowing addressing to a single view is not a thing a
/// caller should be able to ask for by accident.
final class IdToPath {
  private var idToPath = [DivViewId: [String: [UIElementPath: String?]]]()
  private let lock = AllocatedUnfairLock()

  /// Ids colliding across views resolve to several paths and are reported as ambiguous by the
  /// caller, rather than silently resolved in favour of the nearest one - picking for the
  /// author is what `scope_id` exists to avoid.
  func paths(
    forId id: String,
    cardId: DivCardID,
    divTypes: Set<String>? = nil
  ) -> [UIElementPath] {
    lock.withLock {
      idToPath.reduce(into: [UIElementPath]()) { result, entry in
        guard entry.key.cardId == cardId else {
          return
        }
        result.append(contentsOf: paths(in: entry.value[id], divTypes: divTypes))
      }
    }
  }

  func add(_ path: UIElementPath, forId id: String, viewId: DivViewId, divType: String? = nil) {
    lock.withLock {
      _ = idToPath[viewId, default: [:]][id, default: [:]].updateValue(divType, forKey: path)
    }
  }

  func reset() {
    lock.withLock {
      idToPath.removeAll()
    }
  }

  /// Removes everything registered for the card: the host and all of its tooltips.
  func reset(cardId: DivCardID) {
    lock.withLock {
      idToPath = idToPath.filter { $0.key.cardId != cardId }
    }
  }

  /// Removes only the entries registered under `viewId`: a provider refreshing its own
  /// registrations on remodel, or a tooltip closing, must not wipe out a sibling view's.
  func reset(viewId: DivViewId) {
    lock.withLock {
      idToPath[viewId] = nil
    }
  }

  private func paths(
    in pathsWithTypes: [UIElementPath: String?]?,
    divTypes: Set<String>?
  ) -> [UIElementPath] {
    guard let pathsWithTypes else {
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
