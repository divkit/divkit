import CoreGraphics

import CommonCorePublic

public protocol WrapperBlock: SizeForwardingBlock {
  var child: Block { get }

  // make copy of self with substituted child
  func makeCopy(wrapping: Block) -> Self
}

extension WrapperBlock {
  public var sizeProvider: Block { child }

  public func getImageHolders() -> [ImageHolder] {
    child.getImageHolders()
  }

  public func updated(withStates states: BlocksState) throws -> Self {
    let newChild = try child.updated(withStates: states)
    return makeCopyIfChanged(wrapping: newChild)
  }

  private func makeCopyIfChanged(wrapping child: Block) -> Self {
    child === self.child ? self : makeCopy(wrapping: child)
  }
}
