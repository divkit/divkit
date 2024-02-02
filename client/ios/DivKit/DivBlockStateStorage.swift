import Foundation

import BaseTinyPublic
import LayoutKit

public struct IdAndCardId: Hashable {
  let id: String
  let cardId: DivCardID

  public init(id: String, cardId: DivCardID) {
    self.id = id
    self.cardId = cardId
  }

  public init(path: UIElementPath) {
    id = path.leaf
    cardId = DivCardID(rawValue: path.root)
  }
}

public final class DivBlockStateStorage {
  public private(set) var states: BlocksState
  var focusedElement: IdAndCardId?
  private var statesById: [IdAndCardId: ElementState] = [:]
  private let lock = RWLock()

  public init(states: BlocksState = [:]) {
    self.states = states
  }

  @inlinable
  public func getState<T: ElementState>(_ path: UIElementPath) -> T? {
    getStateUntyped(path) as? T
  }

  public func getStateUntyped(_ path: UIElementPath) -> ElementState? {
    lock.read {
      statesById[IdAndCardId(path: path)] ?? states[path]
    }
  }

  @inlinable
  public func getState<T: ElementState>(_ id: String, cardId: DivCardID) -> T? {
    getStateUntyped(id, cardId: cardId) as? T
  }

  public func getStateUntyped(_ id: String, cardId: DivCardID) -> ElementState? {
    let idKey = IdAndCardId(id: id, cardId: cardId)
    return lock.read {
      statesById[idKey] ?? states.first { IdAndCardId(path: $0.key) == idKey }?.value
    }
  }

  public func setState(path: UIElementPath, state: ElementState) {
    lock.write {
      statesById[IdAndCardId(path: path)] = nil
      states[path] = state
    }
  }

  public func setState(id: String, cardId: DivCardID, state: ElementState) {
    lock.write {
      statesById[IdAndCardId(id: id, cardId: cardId)] = state
    }
  }

  public func setFocused(isFocused: Bool, element: IdAndCardId) {
    lock.write {
      if isFocused {
        focusedElement = element
      } else {
        if focusedElement == element {
          focusedElement = nil
        }
      }
    }
  }

  public func isFocused(element: IdAndCardId) -> Bool {
    lock.read {
      focusedElement == element
    }
  }

  public func reset() {
    lock.write {
      states = [:]
      statesById = [:]
      focusedElement = nil
    }
  }

  public func reset(cardId: DivCardID) {
    lock.write {
      states = states.filter { $0.key.root != cardId.rawValue }
      statesById = statesById.filter { $0.key.cardId != cardId }
      if focusedElement?.cardId == cardId {
        focusedElement = nil
      }
    }
  }
}

extension DivBlockStateStorage: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    setState(path: path, state: state)
  }

  public func focusedElementChanged(isFocused: Bool, forPath path: UIElementPath) {
    setFocused(isFocused: isFocused, element: IdAndCardId(path: path))
  }

  func state(for path: UIElementPath) -> ElementState? {
    getStateUntyped(path)
  }
}
