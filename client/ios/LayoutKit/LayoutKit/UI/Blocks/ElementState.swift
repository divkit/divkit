import Foundation

import LayoutKitInterface

public protocol ElementState {}

public typealias BlocksState = [UIElementPath: ElementState]

public protocol ElementStateObserver: AnyObject {
  func elementStateChanged(_ state: ElementState, forPath path: UIElementPath)
  func focusedElementChanged(isFocused: Bool, forPath path: UIElementPath)
}

extension ElementStateObserver {
  public func focusedElementChanged(isFocused _: Bool, forPath _: UIElementPath) {}
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
