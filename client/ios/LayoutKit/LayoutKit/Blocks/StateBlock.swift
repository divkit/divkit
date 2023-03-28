import CommonCorePublic

public final class StateBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public let child: Block
  public let ids: Set<String>

  public init(
    child: Block,
    ids: Set<String>
  ) {
    self.child = child
    self.ids = ids
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? StateBlock else {
      return false
    }
    return self == other
  }

  public func makeCopy(wrapping child: Block) -> StateBlock {
    StateBlock(child: child, ids: ids)
  }
}

extension StateBlock: Equatable {
  public static func ==(lhs: StateBlock, rhs: StateBlock) -> Bool {
    lhs.child == rhs.child
      && lhs.ids == rhs.ids
  }
}

extension StateBlock: CustomDebugStringConvertible {
  public var debugDescription: String { "StateBlock" }
}

extension Block {
  public func addingStateBlock(ids: Set<String>) -> Block {
    StateBlock(child: self, ids: ids)
  }
}
