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

  public private(set) var states: BlocksState
  private var statesById: [IdAndCardId: ElementState] = [:]

  private var focusedElement: FocusedElement = .none {
    didSet {
      isInputFocused = false
    }
  }

  private let lock = AllocatedUnfairLock()
  private let stateUpdatesPipe = SignalPipe<ChangeEvent>()

  private(set) var isInputFocused = false

  var stateUpdates: Signal<ChangeEvent> {
    stateUpdatesPipe.signal
  }

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
    let id = IdAndCardId(path: path)
    lock.withLock {
      statesById[id] = nil
      states[path] = state
    }
    stateUpdatesPipe.send(ChangeEvent(id: id, state: state))
  }

  public func setState(id: String, cardId: DivCardID, state: ElementState) {
    let id = IdAndCardId(id: id, cardId: cardId)
    lock.withLock {
      statesById[id] = state
    }
    stateUpdatesPipe.send(ChangeEvent(id: id, state: state))
  }

  func setFocused(isFocused: Bool, element: IdAndCardId) {
    lock.withLock {
      focusedElement = isFocused ? .idFocused(element) : removeFocus(from: element)
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

  func isFocused(element: IdAndCardId) -> Bool {
    lock.withLock {
      isFocusedInternal(checkedElement: .idFocused(element))
    }
  }

  public func isFocused(path: UIElementPath) -> Bool {
    lock.withLock {
      isFocusedInternal(checkedElement: .pathFocused(path))
    }
  }

  func setInputFocused() {
    isInputFocused = true
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

  public func reset() {
    lock.withLock {
      states = [:]
      statesById = [:]
      focusedElement = .none
    }
  }

  public func reset(cardId: DivCardID) {
    lock.withLock {
      states = states.filter { $0.key.root != cardId.rawValue }
      statesById = statesById.filter { $0.key.cardId != cardId }
      if getFocusedElement()?.cardId == cardId {
        focusedElement = .none
      }
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
