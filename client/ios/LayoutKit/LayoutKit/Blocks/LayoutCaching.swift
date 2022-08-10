import CoreGraphics

public protocol LayoutCaching {
  // for vertically non-resizable blocks
  func laidOut(for width: CGFloat) -> Block

  // for vertically resizable blocks
  func laidOut(for size: CGSize) -> Block
}

public protocol LayoutCachingDefaultImpl: Block {}
extension LayoutCachingDefaultImpl {
  public func laidOut(for _: CGFloat) -> Block { self }
  public func laidOut(for _: CGSize) -> Block { self }
}
