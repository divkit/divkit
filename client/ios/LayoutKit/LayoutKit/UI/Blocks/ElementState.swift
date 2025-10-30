import Foundation

public protocol ElementState {}

extension ElementState {
  public func isDifferent(from newState: ElementState) -> Bool {
    guard let newState = newState as? (any Equatable),
          let base = self as? (any Equatable)
    else { return false }

    return !isEqual(base, newState)
  }

  private func isEqual<T: Equatable>(_ lhs: T, _ rhs: Any) -> Bool {
    guard let rhs = rhs as? T
    else { return false }

    return lhs == rhs
  }
}

public typealias BlocksState = [UIElementPath: ElementState]

public protocol ElementStateObserver: AnyObject {
  func elementStateChanged(_ state: ElementState, forPath path: UIElementPath)
  func focusedElementChanged(isFocused: Bool, forPath path: UIElementPath)
  func clearFocus()
  func setFocusTrackingEnabled(_ isEnabled: Bool)
}

extension ElementStateObserver {
  public func focusedElementChanged(isFocused _: Bool, forPath _: UIElementPath) {}
  public func clearFocus() {}
  public func setFocusTrackingEnabled(_: Bool) {}
}

extension [UIElementPath: ElementState] {
  @inlinable
  public func getState<T: ElementState>(at path: Key) -> T? {
    guard let value = self[path] else {
      return nil
    }

    guard let state = value as? T else {
      assertionFailure("Unexpected state type \(value)")
      return nil
    }

    return state
  }
}
