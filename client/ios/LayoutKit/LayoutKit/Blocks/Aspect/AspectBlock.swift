import CoreGraphics

public final class AspectBlock<Content: Block>: WrapperBlock {
  public let content: Content
  public let aspectRatio: CGFloat

  public var child: Block { content }

  public init(content: Content, aspectRatio: CGFloat) {
    assert(content.isVerticallyResizable)
    self.content = content
    self.aspectRatio = aspectRatio
  }

  public var isVerticallyResizable: Bool { false }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    width * aspectRatio
  }

  public func makeCopy(wrapping: Block) -> Self {
    guard let wrapping = wrapping as? Content else {
      assertionFailure()
      return self
    }
    return Self(content: wrapping, aspectRatio: aspectRatio)
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? Self else { return false }
    return child == other.child && aspectRatio == other.aspectRatio
  }

  public var debugDescription: String {
    """
    Aspect \(aspectRatio):
      \(child)
    """
  }

  public func laidOut(for width: CGFloat) -> Block {
    makeCopy(wrapping: child.laidOut(for: width))
  }

  public func laidOut(for size: CGSize) -> Block {
    makeCopy(wrapping: child.laidOut(for: size))
  }
}

extension Block {
  public func aspectRatio(_ ratio: CGFloat) -> Block {
    AspectBlock(content: self, aspectRatio: ratio)
  }
}
