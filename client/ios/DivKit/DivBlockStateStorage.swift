import Foundation
import LayoutKit
import VGSL

struct IdAndCardId: Hashable {
  let id: String
  let cardId: DivCardID

  init(id: String, cardId: DivCardID) {
    self.id = id
    self.cardId = cardId
  }

  init(path: UIElementPath) {
    id = path.leaf
    cardId = path.cardId
  }
}

public final class DivBlockStateStorage {
  struct ChangeEvent {
    public let id: IdAndCardId
    public let state: ElementState
  }

  private enum FocusedElement: Equatable {
    case none
    case pathFocused(UIElementPath)
    case idFocused(IdAndCardId)
  }

  private(set) var isInputFocused = false

  private var shouldRefreshCachedStates = false
  private var _cachedStates: BlocksState?

  private var _states: [StateKey: ElementState] {
    didSet {
      shouldRefreshCachedStates = true
    }
  }

  private var focusedElement: FocusedElement = .none {
    didSet {
      isInputFocused = false
    }
  }

  private let lock = AllocatedUnfairLock()
  private let stateUpdatesPipe = SignalPipe<ChangeEvent>()

  public var states: BlocksState {
    if let cached = _cachedStates,
       !shouldRefreshCachedStates {
      return cached
    }

    return lock.withLock {
      let dict = Dictionary(
        _states.compactMap { key, value in
          if let path = key.path {
            (path, value)
          } else {
            nil
          }
        },
        uniquingKeysWith: { _, new in new }
      )

      _cachedStates = dict
      shouldRefreshCachedStates = false
      return dict
    }
  }

  var stateUpdates: Signal<ChangeEvent> {
    stateUpdatesPipe.signal
  }

  public init(states: BlocksState = [:]) {
    self._states = Dictionary(
      states.map { key, value in
        (.path(key), value)
      },
      uniquingKeysWith: { _, new in new }
    )
  }

  @inlinable
  public func getState<T: ElementState>(_ path: UIElementPath) -> T? {
    getStateUntyped(path) as? T
  }

  public func getStateUntyped(_ path: UIElementPath) -> ElementState? {
    lock.withLock {
      _states[.path(path)]
    }
  }

  @inlinable
  public func getState<T: ElementState>(_ id: String, cardId: DivCardID) -> T? {
    getStateUntyped(id, cardId: cardId) as? T
  }

  public func getStateUntyped(_ id: String, cardId: DivCardID) -> ElementState? {
    lock.withLock {
      _states[.id(IdAndCardId(id: id, cardId: cardId))]
    }
  }

  public func setState(path: UIElementPath, state: ElementState) {
    let key = StateKey.path(path)
    var shouldUpdatePipe = true

    lock.withLock {
      if let existingState = _states.removeValue(forKey: key),
         !state.isDifferent(from: existingState) {
        shouldUpdatePipe = false
      }

      _states[key] = state
    }

    if shouldUpdatePipe {
      stateUpdatesPipe.send(
        ChangeEvent(id: IdAndCardId(path: path), state: state)
      )
    }
  }

  public func setState(id: String, cardId: DivCardID, state: ElementState) {
    let id = IdAndCardId(id: id, cardId: cardId)
    let key = StateKey.id(id)
    var shouldUpdatePipe = true

    lock.withLock {
      if let existingState = _states[key],
         !state.isDifferent(from: existingState) {
        shouldUpdatePipe = false
      } else {
        _states[key] = state
      }
    }

    if shouldUpdatePipe {
      stateUpdatesPipe.send(ChangeEvent(id: id, state: state))
    }
  }

  public func setFocused(
    isFocused: Bool,
    path: UIElementPath
  ) {
    lock.withLock {
      focusedElement = isFocused ? .pathFocused(path) : .none
    }
  }

  public func clearFocus() {
    lock.withLock {
      focusedElement = .none
    }
  }

  public func isFocused(path: UIElementPath) -> Bool {
    lock.withLock {
      isFocusedInternal(checkedElement: .pathFocused(path))
    }
  }

  public func reset() {
    lock.withLock {
      _states = [:]
      focusedElement = .none
    }
  }

  public func reset(cardId: DivCardID) {
    lock.withLock {
      _states = _states.filter { $0.key.cardID != cardId }
      if getFocusedElement()?.cardId == cardId {
        focusedElement = .none
      }
    }
  }

  func setFocused(isFocused: Bool, element: IdAndCardId) {
    lock.withLock {
      focusedElement = isFocused ? .idFocused(element) : removeFocus(from: element)
    }
  }

  func isFocused(element: IdAndCardId) -> Bool {
    lock.withLock {
      isFocusedInternal(checkedElement: .idFocused(element))
    }
  }

  func setInputFocused() {
    isInputFocused = true
  }

  func getFocusedElement() -> IdAndCardId? {
    switch focusedElement {
    case .none:
      nil
    case let .pathFocused(focusedPath):
      IdAndCardId(path: focusedPath)
    case let .idFocused(focusedId):
      focusedId
    }
  }

  private func isFocusedInternal(checkedElement: FocusedElement) -> Bool {
    switch (focusedElement, checkedElement) {
    case (.none, _), (_, .none):
      false
    case let (.pathFocused(focusedPath), .pathFocused(checkedPath)):
      focusedPath == checkedPath
    case let (.idFocused(focusedId), .idFocused(checkedId)):
      focusedId == checkedId
    case let (.pathFocused(focusedPath), .idFocused(checkedId)):
      IdAndCardId(path: focusedPath) == checkedId
    case let (.idFocused(focusedId), .pathFocused(checkedPath)):
      focusedId == IdAndCardId(path: checkedPath)
    }
  }

  private func removeFocus(from element: IdAndCardId) -> FocusedElement {
    isFocusedInternal(checkedElement: FocusedElement.idFocused(element)) ? .none : focusedElement
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

private enum StateKey {
  case path(UIElementPath)
  case id(IdAndCardId)

  var path: UIElementPath? {
    switch self {
    case let .path(path):
      path
    case .id:
      nil
    }
  }

  var cardID: DivCardID {
    id.cardId
  }

  private var id: IdAndCardId {
    switch self {
    case let .path(path):
      IdAndCardId(path: path)
    case let .id(id):
      id
    }
  }
}

extension StateKey: Equatable, Hashable {
  func hash(into hasher: inout Hasher) {
    hasher.combine(id)
  }

  static func ==(lhs: StateKey, rhs: StateKey) -> Bool {
    switch (lhs, rhs) {
    case let (.path(lhsPath), .path(rhsPath)):
      lhsPath == rhsPath
    default:
      lhs.id == rhs.id
    }
  }
}
