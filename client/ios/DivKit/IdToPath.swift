import LayoutKit
import VGSL

/// Maps element ids to their paths across a whole card - the host `DivView` and every one of
/// its tooltips, each of which gets a `DivView` of its own.
///
/// Two identifiers meet here, and they are not interchangeable:
///
/// - **write and invalidate by `DivViewId`** (`add`, `beginUpdate`/`endUpdate`,
///   `reset(viewId:)`), so that each `DivView` can refresh its own registrations without
///   touching a sibling's; a whole card, tooltips included, is dropped by `DivCardID`
///   (`reset(cardId:)`);
/// - **read by `DivCardID`** (`paths(forId:cardId:)`), because addressing is card-wide: an
///   action inside a tooltip may name an element of the host card, and vice versa, exactly as
///   it could before tooltips got a `DivView` of their own.
///
/// The partition is about who may drop a registration, not about who may see it. There is
/// deliberately no view-scoped lookup: narrowing addressing to a single view is not a thing a
/// caller should be able to ask for by accident.
final class IdToPath {
  struct Target {
    let path: UIElementPath
    let extensionHandler: DivExtensionHandler?
  }

  private struct Registration {
    let divType: String?
    let generation: Int
    let extensionIds: Set<String>
    let extensionHandlers: [String: DivExtensionHandler]
  }

  private var idToPath = [DivViewId: [String: [UIElementPath: Registration]]]()
  private var generations = [DivViewId: Int]()
  private let lock = AllocatedUnfairLock()

  /// Ids colliding across views resolve to several paths and are reported as ambiguous by the
  /// caller, rather than silently resolved in favour of the nearest one - picking for the
  /// author is what `scope_id` exists to avoid.
  ///
  /// Within a view only the latest registration among the paths matching `scopePath` and
  /// `divTypes` counts. During an update the id resolves to the matching paths registered by the
  /// update so far, or to the previous ones while the update has not reached any of them.
  func paths(
    forId id: String,
    cardId: DivCardID,
    scopePath: UIElementPath? = nil,
    divTypes: Set<String>? = nil
  ) -> [UIElementPath] {
    targets(
      forId: id, cardId: cardId, scopePath: scopePath,
      divTypes: divTypes
    ).map(\.path)
  }

  /// Captures paths and their extensions together so delivery cannot mix registrations
  /// from different model updates. Host-provided properties must be accessed outside the lock.
  func targets(
    forId id: String,
    cardId: DivCardID,
    scopePath: UIElementPath? = nil,
    divTypes: Set<String>? = nil,
    extensionId: String? = nil
  ) -> [Target] {
    lock.withLock {
      idToPath.reduce(into: [Target]()) { result, entry in
        guard entry.key.cardId == cardId else {
          return
        }
        result.append(contentsOf: selectLatestTargets(
          in: entry.value[id],
          scopePath: scopePath,
          divTypes: divTypes,
          extensionId: extensionId
        ))
      }
    }
  }

  func add(
    _ path: UIElementPath,
    forId id: String,
    viewId: DivViewId,
    divType: String? = nil,
    extensionIds: Set<String> = [],
    extensionHandlers: [String: DivExtensionHandler] = [:]
  ) {
    lock.withLock {
      idToPath[viewId, default: [:]][id, default: [:]][path] = Registration(
        divType: divType,
        generation: generations[viewId, default: 0],
        extensionIds: extensionIds,
        extensionHandlers: extensionHandlers
      )
    }
  }

  /// Starts re-registering the ids of the view. Its previous registrations stay visible until
  /// they are re-registered or `endUpdate(viewId:)` drops them, so concurrent lookups do not
  /// lose ids the update has not reached yet. Updates of one view are expected to be serial.
  func beginUpdate(viewId: DivViewId) {
    lock.withLock {
      generations[viewId, default: 0] += 1
    }
  }

  /// Drops the ids of the view that were not re-registered since `beginUpdate(viewId:)`.
  func endUpdate(viewId: DivViewId) {
    lock.withLock {
      let generation = generations[viewId, default: 0]
      let staleIds = (idToPath[viewId] ?? [:]).compactMap { id, registrations in
        registrations.values.contains { $0.generation < generation } ? id : nil
      }
      for id in staleIds {
        let actual = (idToPath[viewId]?[id] ?? [:]).filter { $0.value.generation >= generation }
        idToPath[viewId]?[id] = actual.isEmpty ? nil : actual
      }
      if idToPath[viewId]?.isEmpty ?? true {
        idToPath[viewId] = nil
        generations[viewId] = nil
      }
    }
  }

  func reset() {
    lock.withLock {
      idToPath.removeAll()
      generations.removeAll()
    }
  }

  /// Removes everything registered for the card: the host and all of its tooltips.
  func reset(cardId: DivCardID) {
    lock.withLock {
      idToPath = idToPath.filter { $0.key.cardId != cardId }
      generations = generations.filter { $0.key.cardId != cardId }
    }
  }

  /// Removes only the entries registered under `viewId`: a tooltip closing must not wipe out
  /// a sibling view's.
  func reset(viewId: DivViewId) {
    lock.withLock {
      idToPath[viewId] = nil
      generations[viewId] = nil
    }
  }

  private func selectLatestTargets(
    in registrations: [UIElementPath: Registration]?,
    scopePath: UIElementPath?,
    divTypes: Set<String>?,
    extensionId: String?
  ) -> [Target] {
    guard let registrations else {
      return []
    }
    let matching = registrations.lazy.filter { path, registration in
      if let scopePath, !path.starts(with: scopePath) {
        return false
      }
      if let extensionId, !registration.extensionIds.contains(extensionId) {
        return false
      }
      if let divTypes, let divType = registration.divType {
        return divTypes.contains(divType)
      }
      return true
    }
    guard let generation = matching.map(\.value.generation).max() else {
      return []
    }
    return matching.compactMap { path, registration in
      guard registration.generation == generation else {
        return nil
      }
      return Target(
        path: path,
        extensionHandler: extensionId.flatMap { registration.extensionHandlers[$0] }
      )
    }
  }
}
