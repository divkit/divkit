import CoreGraphics

public final class SwipeContainerBlock: WrapperBlock {
  public enum State: ElementState {
    case normal
    case right
    case left

    public static let `default` = State.normal
  }

  public let child: Block

  let swipeOutActions: [UserInterfaceAction]
  let path: UIElementPath
  let state: State

  public init(
    child: Block,
    state: State,
    path: UIElementPath,
    swipeOutActions: [UserInterfaceAction]
  ) {
    self.child = child
    self.path = path
    self.state = state
    self.swipeOutActions = swipeOutActions
  }

  public static func ==(lhs: SwipeContainerBlock, rhs: SwipeContainerBlock) -> Bool {
    lhs.child == rhs.child
      && lhs.swipeOutActions == rhs.swipeOutActions
      && lhs.path == rhs.path
      && lhs.state == rhs.state
  }

  public func intrinsicContentHeight(forWidth: CGFloat) -> CGFloat {
    switch state {
    case .normal:
      child.intrinsicContentHeight(forWidth: forWidth)
    case .left, .right:
      0
    }
  }

  public func makeCopy(wrapping child: Block) -> SwipeContainerBlock {
    SwipeContainerBlock(
      child: child,
      state: state,
      path: path,
      swipeOutActions: swipeOutActions
    )
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? SwipeContainerBlock else {
      return false
    }

    return self == other
  }

}

extension SwipeContainerBlock: LayoutCaching {
  public func laidOut(for width: CGFloat) -> Block {
    makeCopy(wrapping: child.laidOut(for: width))
  }

  public func laidOut(for size: CGSize) -> Block {
    makeCopy(wrapping: child.laidOut(for: size))
  }
}

extension SwipeContainerBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> SwipeContainerBlock {
    guard let newState: State = states.getState(at: path), newState != state else {
      return self
    }

    return SwipeContainerBlock(
      child: child,
      state: newState,
      path: path,
      swipeOutActions: swipeOutActions
    )
  }
}

extension SwipeContainerBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> SwipeContainerBlock {
    let newChild = try child.updated(path: path, isFocused: isFocused)
    if newChild === child {
      return self
    }

    return SwipeContainerBlock(
      child: newChild,
      state: state,
      path: path,
      swipeOutActions: swipeOutActions
    )
  }
}
