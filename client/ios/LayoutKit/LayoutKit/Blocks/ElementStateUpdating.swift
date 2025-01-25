import Foundation

public protocol ElementStateUpdating {
  func updated(withStates states: BlocksState) throws -> Self
}

public protocol ElementStateUpdatingDefaultImpl: Block {}

extension ElementStateUpdatingDefaultImpl {
  public func updated(withStates _: BlocksState) -> Self { self }
}

public protocol ElementFocusUpdating {
  func updated(path: UIElementPath, isFocused: Bool) throws -> Self
}

extension ElementFocusUpdating {
  public func updated(
    path _: UIElementPath,
    isFocused _: Bool
  ) throws -> Self { self }
}
