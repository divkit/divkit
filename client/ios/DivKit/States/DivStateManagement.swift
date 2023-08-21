import BasePublic
import CommonCorePublic
import Foundation
import BaseTinyPublic

/// The `DivStateManagement` protocol provides essential functionality for managing card states.
///
/// Conforming to this protocol allows your class to handle state management for cards. It requires
/// the implementation of three methods:
/// - `getStateManagerForCard(cardId:)`: This method should return a ``DivStateManager`` object
/// associated with the given `cardId`.
/// - `reset()`: This method resets states for all cards.
/// - `reset(cardId:)`: This method resets the state for the card identified by the provided
/// `cardId`.
public protocol DivStateManagement: DivStateUpdater {
  /// Retrieves the `DivStateManager` object associated with the given `cardId`.
  ///
  /// - Parameter cardId: The identifier of the card.
  /// - Returns: A `DivStateManager` object for the specified card.
  func getStateManagerForCard(cardId: DivCardID) -> DivStateManager

  /// Resets states for all cards.
  func reset()

  /// Resets the state for the card identified by the provided `cardId`.
  ///
  /// - Parameter cardId: The identifier of the card to reset.
  func reset(cardId: DivCardID)
}

public class DefaultDivStateManagement: DivStateManagement {
  static let storageFileName = "divkit.states_storage"

  private let timestampProvider: Variable<Milliseconds>

  private var persistentStorage = Property<StoredStates>(
    fileName: storageFileName,
    initialValue: StoredStates(items: [:]),
    onError: { error in
      DivKitLogger.error("Failed to create storage: \(error)")
    }
  )

  private var stateManagersForCards: [DivCardID: DivStateManager] = [:]

  public init(
    timestampProvider: Variable<Milliseconds> = Variable {
      Date().timeIntervalSince1970.milliseconds
    }
  ) {
    self.timestampProvider = timestampProvider

    removeOutdatedStoredStates()
  }

  public func set(
    path: DivStatePath,
    cardId: DivCardID,
    lifetime: DivStateLifetime
  ) {
    let stateManager = getStateManagerForCard(cardId: cardId)
    let (parentPath, stateId) = path
      .split() ?? (DivData.rootPath, DivStateID(rawValue: path.rawValue.root))
    stateManager.setStateWithHistory(path: parentPath, stateID: stateId)
    stateManagersForCards[cardId] = stateManager

    if lifetime == .long {
      var storedStates = persistentStorage.value
      var storedStatesForCard = storedStates.getStates(cardId: cardId)
      storedStatesForCard[parentPath] = StoredState(
        stateId: stateId,
        timestamp: timestampProvider.value
      )
      storedStates.items[cardId] = storedStatesForCard
      persistentStorage.value = storedStates
    }
  }

  public func getStateManagerForCard(cardId: DivCardID) -> DivStateManager {
    if let stateManager = stateManagersForCards[cardId] {
      return stateManager
    }

    let storedStates = persistentStorage.value.getStates(cardId: cardId)
    let stateManager = DivStateManager(
      items: storedStates.mapValues {
        DivStateManager.Item(
          currentStateID: $0.stateId,
          previousState: .initial
        )
      }
    )
    stateManagersForCards[cardId] = stateManager
    return stateManager
  }

  public func reset() {
    stateManagersForCards.values.forEach {
      $0.reset()
    }
  }

  public func reset(cardId: DivCardID) {
    stateManagersForCards[cardId]?.reset()
  }

  private func removeOutdatedStoredStates() {
    let currentTimestamp = timestampProvider.value
    let items = persistentStorage.value.items
    let newItems = items.compactMapValues { states in
      let newStates = states.filter { _, state in
        currentTimestamp - state.timestamp < storagePeriod
      }
      return (newStates.count > 0) ? newStates : nil
    }

    if newItems != items {
      persistentStorage.value = StoredStates(items: newItems)
    }
  }
}

struct StoredStates: Equatable, Codable {
  var items: [DivCardID: [DivStatePath: StoredState]]

  func getStates(cardId: DivCardID) -> [DivStatePath: StoredState] {
    items[cardId] ?? [:]
  }
}

struct StoredState: Equatable, Codable {
  let stateId: DivStateID
  let timestamp: Milliseconds
}

private let storagePeriod = 2 * 24 * 60 * 60 * 1000
