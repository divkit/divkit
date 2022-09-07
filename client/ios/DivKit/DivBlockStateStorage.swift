import Foundation

import LayoutKit

public final class DivBlockStateStorage {
  public private(set) var states: BlocksState
  private var statesById: [String: ElementState] = [:]

  public init(states: BlocksState = [:]) {
    self.states = states
  }

  @inlinable
  public func getState<T: ElementState>(_ path: UIElementPath) -> T? {
    getStateUntyped(path) as? T
  }

  public func getStateUntyped(_ path: UIElementPath) -> ElementState? {
    statesById[path.leaf] ?? states[path]
  }

  @inlinable
  public func getState<T: ElementState>(_ id: String) -> T? {
    getStateUntyped(id) as? T
  }

  public func getStateUntyped(_ id: String) -> ElementState? {
    statesById[id] ?? states.first { $0.key.leaf == id }?.value
  }

  public func setState(path: UIElementPath, state: ElementState) {
    statesById[path.leaf] = nil
    states[path] = state
  }

  public func setState(id: String, state: ElementState) {
    statesById[id] = state
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
