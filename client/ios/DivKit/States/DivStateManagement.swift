import Foundation

public protocol DivStateManagement: DivStateUpdater {
  func getStateManagerForCard(cardId: DivCardID) -> DivStateManager
  func reset()
}

public class DefaultDivStateManagement: DivStateManagement {
  private var stateManagersForCards: [DivCardID: DivStateManager] = [:]

  public init() {}

  public func set(
    path: DivStatePath,
    cardId: DivCardID,
    lifetime _: DivStateLifetime
  ) {
    let stateManager = getStateManagerForCard(cardId: cardId)
    let (parentPath, stateId) = path
      .split() ?? (DivData.rootPath, DivStateID(rawValue: path.rawValue.root))
    stateManager.setStateWithHistory(path: parentPath, stateID: stateId)
    stateManagersForCards[cardId] = stateManager
  }

  public func getStateManagerForCard(cardId: DivCardID) -> DivStateManager {
    if let stateManager = stateManagersForCards[cardId] {
      return stateManager
    }

    let stateManager = DivStateManager()
    stateManagersForCards[cardId] = stateManager
    return stateManager
  }

  public func reset() {
    stateManagersForCards.values.forEach {
      $0.reset()
    }
  }
}
