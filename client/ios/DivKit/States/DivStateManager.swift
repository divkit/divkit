import CommonCorePublic

public class DivStateManager {
  public struct Item: Equatable {
    public let currentStateID: DivStateID
    // Previous state is used for transition animations only.
    // It becomes .empty when transition is over.
    public let previousState: PreviousState

    public init(currentStateID: DivStateID, previousState: PreviousState) {
      self.currentStateID = currentStateID
      self.previousState = previousState
    }
  }

  public enum PreviousState: Equatable {
    case empty
    // Exact state id is unknown becuase it is initial state, it was not
    // stored in DivStateManager yet.
    case initial
    case withID(DivStateID)
  }

  private let rwLock = RWLock()

  public private(set) var items: [DivStatePath: Item]
  public private(set) var blockIds: [DivStatePath: Set<String>] = [:]
  public private(set) var blockVisibility: [DivBlockPath: Bool] = [:]

  public init() {
    self.items = [:]
  }

  public init(items: [DivStatePath: Item]) {
    self.items = items
  }

  func get(stateBlockPath: DivStatePath) -> Item? {
    rwLock.read {
      items[stateBlockPath]
    }
  }

  public func setState(stateBlockPath: DivStatePath, stateID: DivStateID) {
    rwLock.write {
      items[stateBlockPath] = Item(currentStateID: stateID, previousState: .empty)
    }
  }

  public func setStateWithHistory(path: DivStatePath, stateID: DivStateID) {
    let previousState: PreviousState
    if let previousStateID = get(stateBlockPath: path)?.currentStateID {
      previousState = .withID(previousStateID)
    } else {
      previousState = .initial
    }
    rwLock.write {
      items[path] = Item(currentStateID: stateID, previousState: previousState)
    }
  }

  public func removeState(path: DivStatePath) {
    rwLock.write {
      _ = items.removeValue(forKey: path)
    }
  }

  public func isBlockAdded(_ id: String, stateBlockPath: DivStatePath) -> Bool {
    guard let item = get(stateBlockPath: stateBlockPath),
          let currentBlockIds = blockIds[stateBlockPath + item.currentStateID] else {
      return false
    }

    switch item.previousState {
    case .empty:
      return false
    case .initial:
      if currentBlockIds.contains(id) {
        return true
      }
    case let .withID(previousStateId):
      if currentBlockIds.contains(id),
         let previousBlockIds = blockIds[stateBlockPath + previousStateId],
         !previousBlockIds.contains(id) {
        return true
      }
    }

    return false
  }

  public func updateBlockIdsWithStateChangeTransition(statePath: DivStatePath, div: Div) {
    blockIds[statePath] = div.idsWithStateChangeTransitionInCurrentState
  }

  public func getVisibleIds(statePath: DivStatePath) -> Set<String> {
    var ids = blockIds[statePath] ?? Set<String>()
    blockVisibility.forEach { blockPath, isVisible in
      if blockPath.statePath == statePath {
        if isVisible {
          ids.insert(blockPath.blockId)
        } else {
          ids.remove(blockPath.blockId)
        }
      }
    }
    return ids
  }

  public func shouldBlockAppearWithTransition(path: DivBlockPath) -> Bool {
    blockVisibility[path] == false
  }

  public func setBlockVisibility(statePath: DivStatePath, div: DivBase, isVisible: Bool) {
    if case .value = div.visibility {
      // visibility is constant
      return
    }

    if let id = div.id, div.shouldApplyTransition(.visibilityChange) {
      blockVisibility[statePath + id] = isVisible
    }
  }

  public func reset() {
    items = [:]
    blockIds = [:]
    blockVisibility = [:]
  }
}

extension DivStateManager: Equatable {
  public static func ==(lhs: DivStateManager, rhs: DivStateManager) -> Bool {
    lhs.items == rhs.items
  }
}

extension Div {
  fileprivate var idsWithStateChangeTransitionInCurrentState: Set<String> {
    var items: [String] = []
    items.appendIdWithStateChangeTransition(div: self)
    if case .divState = self {
      return Set(items)
    }
    children.forEach {
      items.append(contentsOf: $0.idsWithStateChangeTransitionInCurrentState)
    }
    return Set(items)
  }
}

extension DivBase {
  fileprivate func shouldApplyTransition(_ trigger: DivTransitionTrigger) -> Bool {
    if transitionIn == nil, transitionOut == nil, transitionChange == nil {
      return false
    }
    guard let triggers = transitionTriggers else {
      return trigger == .stateChange || trigger == .visibilityChange
    }
    return triggers.contains(trigger)
  }
}

extension Array where Element == String {
  fileprivate mutating func appendIdWithStateChangeTransition(div: Div) {
    if let id = div.id, div.value.shouldApplyTransition(.stateChange) {
      append(id)
    }
  }
}
