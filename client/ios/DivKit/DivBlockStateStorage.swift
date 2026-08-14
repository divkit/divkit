import Foundation
import LayoutKit
import VGSL

public final class DivBlockStateStorage {
  struct ChangeEvent {
    let path: UIElementPath
    let state: ElementState
  }

  private var _states: BlocksState
  private var _pendingStates: BlocksState = [:]
  private var _isInputFocused = false

  private var _focusedElement: UIElementPath? {
    didSet {
      _isInputFocused = false
    }
  }

  private let lock = AllocatedUnfairLock()
  private let stateUpdatesPipe = SignalPipe<ChangeEvent>()

  public var states: BlocksState {
    lock.withLock {
      _states
    }
  }

  var focusedElement: UIElementPath? {
    lock.withLock {
      _focusedElement
    }
  }

  var isInputFocused: Bool {
    lock.withLock {
      _isInputFocused
    }
  }

  var stateUpdates: Signal<ChangeEvent> {
    stateUpdatesPipe.signal
  }

  public init(states: BlocksState = [:]) {
    _states = states
  }

  @inlinable
  public func getState<T: ElementState>(_ path: UIElementPath) -> T? {
    getStateUntyped(path) as? T
  }

  public func getStateUntyped(_ path: UIElementPath) -> ElementState? {
    lock.withLock {
      _states[path]
    }
  }

  public func setState(path: UIElementPath, state: ElementState) {
    var shouldUpdatePipe = true

    lock.withLock {
      if let existingState = _states.updateValue(state, forKey: path),
         !state.isDifferent(from: existingState) {
        shouldUpdatePipe = false
      }
    }

    if shouldUpdatePipe {
      stateUpdatesPipe.send(
        ChangeEvent(path: path, state: state)
      )
    }
  }

  public func setFocused(
    isFocused: Bool,
    path: UIElementPath
  ) {
    lock.withLock {
      if isFocused {
        _focusedElement = path
      } else if _focusedElement == path {
        _focusedElement = nil
      }
    }
  }

  public func clearFocus() {
    lock.withLock {
      _focusedElement = nil
    }
  }

  public func isFocused(path: UIElementPath) -> Bool {
    lock.withLock {
      _focusedElement == path
    }
  }

  public func reset() {
    lock.withLock {
      _states = [:]
      _pendingStates = [:]
      _focusedElement = nil
    }
  }

  public func reset(cardId: DivCardID) {
    lock.withLock {
      _states = _states.filter { $0.key.cardId != cardId }
      _pendingStates = _pendingStates.filter { $0.key.cardId != cardId }
      if _focusedElement?.cardId == cardId {
        _focusedElement = nil
      }
    }
  }

  func setPendingState(_ path: UIElementPath, state: ElementState) {
    lock.withLock {
      _pendingStates[path] = state
    }
    setState(path: path, state: state)
  }

  func takePendingState(_ path: UIElementPath) -> ElementState? {
    lock.withLock {
      _pendingStates.removeValue(forKey: path)
    }
  }

  func peekPendingState(_ path: UIElementPath) -> ElementState? {
    lock.withLock {
      _pendingStates[path]
    }
  }

  func setInputFocused() {
    lock.withLock {
      _isInputFocused = true
    }
  }
}

extension DivBlockStateStorage: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    setState(path: path, state: state)
  }

  public func focusedElementChanged(
    isFocused: Bool,
    forPath path: UIElementPath
  ) {
    setFocused(isFocused: isFocused, path: path)
  }
}
