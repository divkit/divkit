import Foundation

import LayoutKit
import BaseTinyPublic

public final class DivBlockStateStorage {
  public private(set) var states: BlocksState
  private var statesById: [IdAndCardId: ElementState] = [:]

  public init(states: BlocksState = [:]) {
    self.states = states
  }

  @inlinable
  public func getState<T: ElementState>(_ path: UIElementPath) -> T? {
    getStateUntyped(path) as? T
  }

  public func getStateUntyped(_ path: UIElementPath) -> ElementState? {
    statesById[IdAndCardId(path: path)] ?? states[path]
  }

  @inlinable
  public func getState<T: ElementState>(_ id: String, cardId: DivCardID) -> T? {
    getStateUntyped(id, cardId: cardId) as? T
  }

  public func getStateUntyped(_ id: String, cardId: DivCardID) -> ElementState? {
    let idKey = IdAndCardId(id: id, cardId: cardId)
    return statesById[idKey] ?? states.first { IdAndCardId(path: $0.key) == idKey }?.value
  }

  public func setState(path: UIElementPath, state: ElementState) {
    statesById[IdAndCardId(path: path)] = nil
    states[path] = state
  }

  public func setState(id: String, cardId: DivCardID, state: ElementState) {
    statesById[IdAndCardId(id: id, cardId: cardId)] = state
  }

  public func reset() {
    states = [:]
    statesById = [:]
  }
}

extension DivBlockStateStorage: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    setState(path: path, state: state)
  }

  func state(for path: UIElementPath) -> ElementState? {
    getStateUntyped(path)
  }
}

private struct IdAndCardId: Hashable {
  let id: String
  let cardId: DivCardID

  init(id: String, cardId: DivCardID) {
    self.id = id
    self.cardId = cardId
  }

  init(path: UIElementPath) {
    id = path.leaf
    cardId = DivCardID(rawValue: path.root)
  }
}
