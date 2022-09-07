import Foundation

import LayoutKitInterface

public protocol ElementState {
  static var prefersAnimatedChanges: Bool { get }
  static var shouldUpdateUi: Bool { get }
}

extension ElementState {
  public static var prefersAnimatedChanges: Bool { false }
  public static var shouldUpdateUi: Bool { true }
}

public typealias BlocksState = [UIElementPath: ElementState]

public protocol ElementStateObserver: AnyObject {
  func elementStateChanged(_ state: ElementState, forPath path: UIElementPath)
}

extension Dictionary where Key == UIElementPath, Value == ElementState {
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
