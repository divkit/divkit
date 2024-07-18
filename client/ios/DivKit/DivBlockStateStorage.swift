import Foundation

import LayoutKit
import VGSL

public struct IdAndCardId: Hashable {
  let id: String
  let cardId: DivCardID

  public init(id: String, cardId: DivCardID) {
    self.id = id
    self.cardId = cardId
  }

  public init(path: UIElementPath) {
    id = path.leaf
    cardId = path.cardId
  }
}

public final class DivBlockStateStorage {
  public private(set) var states: BlocksState
  private var statesById: [IdAndCardId: ElementState] = [:]
  private var focusedElement: UIElementPath?
  private var focusedElementById: IdAndCardId?
  private let lock = AllocatedUnfairLock()

  public init(states: BlocksState = [:]) {
    self.states = states
  }

  @inlinable
  public func getState<T: ElementState>(_ path: UIElementPath) -> T? {
    getStateUntyped(path) as? T
  }

  public func getStateUntyped(_ path: UIElementPath) -> ElementState? {
    lock.withLock {
      statesById[IdAndCardId(path: path)] ?? states[path]
    }
  }

  @inlinable
  public func getState<T: ElementState>(_ id: String, cardId: DivCardID) -> T? {
    getStateUntyped(id, cardId: cardId) as? T
  }

  public func getStateUntyped(_ id: String, cardId: DivCardID) -> ElementState? {
    let idKey = IdAndCardId(id: id, cardId: cardId)
    return lock.withLock {
      statesById[idKey] ?? states.first { IdAndCardId(path: $0.key) == idKey }?.value
    }
  }

  public func setState(path: UIElementPath, state: ElementState) {
    lock.withLock {
      statesById[IdAndCardId(path: path)] = nil
      states[path] = state
    }
  }

  public func setState(id: String, cardId: DivCardID, state: ElementState) {
    lock.withLock {
      statesById[IdAndCardId(id: id, cardId: cardId)] = state
    }
  }

  public func setFocused(isFocused: Bool, element: IdAndCardId) {
    lock.withLock {
      if isFocused {
        focusedElement = nil
        focusedElementById = element
      } else if self.isFocusedInternal(element: element) {
        focusedElement = nil
        focusedElementById = nil
      }
    }
  }

  public func setFocused(isFocused: Bool, path: UIElementPath) {
    lock.withLock {
      if isFocused {
        focusedElement = path
        focusedElementById = nil
      } else if self.isFocusedInternal(path: path) {
        focusedElement = nil
        focusedElementById = nil
      }
    }
  }

  public func clearFocus() {
    lock.withLock {
      focusedElement = nil
      focusedElementById = nil
    }
  }

  public func isFocused(element: IdAndCardId) -> Bool {
    lock.withLock {
      isFocusedInternal(element: element)
    }
  }

  public func isFocused(path: UIElementPath) -> Bool {
    lock.withLock {
      isFocusedInternal(path: path)
    }
  }

  private func isFocusedInternal(element: IdAndCardId) -> Bool {
    getFocusedElement() == element
  }

  private func isFocusedInternal(path: UIElementPath) -> Bool {
    focusedElement == path || focusedElementById == IdAndCardId(path: path)
  }

  public func getFocusedElement() -> IdAndCardId? {
    focusedElementById ?? focusedElement.map(IdAndCardId.init)
  }

  public func reset() {
    lock.withLock {
      states = [:]
      statesById = [:]
      focusedElement = nil
      focusedElementById = nil
    }
  }

  public func reset(cardId: DivCardID) {
    lock.withLock {
      states = states.filter { $0.key.root != cardId.rawValue }
      statesById = statesById.filter { $0.key.cardId != cardId }
      if getFocusedElement()?.cardId == cardId {
        focusedElement = nil
        focusedElementById = nil
      }
    }
  }
}

extension DivBlockStateStorage: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    setState(path: path, state: state)
  }

  public func focusedElementChanged(isFocused: Bool, forPath path: UIElementPath) {
    setFocused(isFocused: isFocused, path: path)
  }
}
