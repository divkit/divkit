public final class GestureBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public let child: Block
  public let upActions: [UserInterfaceAction]?
  public let downActions: [UserInterfaceAction]?
  public let leftActions: [UserInterfaceAction]?
  public let rightActions: [UserInterfaceAction]?

  public init(
    child: Block,
    upActions: [UserInterfaceAction]? = nil,
    downActions: [UserInterfaceAction]? = nil,
    leftActions: [UserInterfaceAction]? = nil,
    rightActions: [UserInterfaceAction]? = nil
  ) {
    self.child = child
    self.upActions = upActions
    self.downActions = downActions
    self.leftActions = leftActions
    self.rightActions = rightActions
  }

  public func makeCopy(wrapping block: Block) -> GestureBlock {
    GestureBlock(
      child: block,
      upActions: upActions,
      downActions: downActions,
      leftActions: leftActions,
      rightActions: rightActions
    )
  }
  
  public func equals(_ other: Block) -> Bool {
    guard let other = other as? GestureBlock else {
      return false
    }
    return self == other
  }
}

extension GestureBlock: Equatable {
  public static func ==(lhs: GestureBlock, rhs: GestureBlock) -> Bool {
    lhs.child == rhs.child &&
      lhs.upActions == rhs.upActions &&
      lhs.downActions == rhs.downActions &&
      lhs.leftActions == rhs.leftActions &&
      lhs.rightActions == rhs.rightActions
  }
}

extension GestureBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    "GestureBlock child: \(child), " +
      "up actions: \(upActions ?? [])," +
      "down actions: \(downActions ?? [])," +
      "left actions: \(leftActions ?? [])," +
      "right actions: \(rightActions ?? [])"
  }
}
