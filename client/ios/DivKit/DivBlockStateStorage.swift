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
  private var videoPagerPaths: [UIElementPath: UIElementPath] = [:]
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
    let updates = lock.withLock {
      updateState(path: path, state: state)
    }

    for update in updates {
      stateUpdatesPipe.send(update)
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
      videoPagerPaths = [:]
      _focusedElement = nil
    }
  }

  public func reset(cardId: DivCardID) {
    lock.withLock {
      _states = _states.filter { $0.key.cardId != cardId }
      _pendingStates = _pendingStates.filter { $0.key.cardId != cardId }
      videoPagerPaths = videoPagerPaths.filter { $0.key.cardId != cardId }
      if _focusedElement?.cardId == cardId {
        _focusedElement = nil
      }
    }
  }

  func registerVideo(
    path: UIElementPath,
    pagerPath: UIElementPath?,
    initialState: VideoBlockViewState
  ) {
    let updates: [ChangeEvent] = lock.withLock {
      videoPagerPaths[path] = pagerPath
      guard pagerPath != nil, initialState.state == .playing, _states[path] == nil else {
        return []
      }
      return updateState(path: path, state: initialState)
    }
    for update in updates {
      stateUpdatesPipe.send(update)
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

  private func updateState(path: UIElementPath, state: ElementState) -> [ChangeEvent] {
    lock.precondition(.owner)
    var updates: [ChangeEvent] = []
    if let videoState = state as? VideoBlockViewState,
       videoState.state == .playing,
       let pagerPath = videoPagerPaths[path] {
      let otherPlayingVideos = _states.compactMap { videoPath, storedState -> UIElementPath? in
        guard videoPath != path,
              videoPagerPaths[videoPath] == pagerPath,
              let storedVideoState = storedState as? VideoBlockViewState,
              storedVideoState.state == .playing else {
          return nil
        }
        return videoPath
      }
      for videoPath in otherPlayingVideos {
        let paused = VideoBlockViewState(state: .paused)
        _states[videoPath] = paused
        updates.append(ChangeEvent(path: videoPath, state: paused))
      }
    }
    let existingState = _states.updateValue(state, forKey: path)
    let shouldNotify = existingState.map { state.isDifferent(from: $0) } ?? true
    if shouldNotify {
      updates.append(ChangeEvent(path: path, state: state))
    }
    return updates
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
