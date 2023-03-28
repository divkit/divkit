import CoreGraphics
import Foundation

import CommonCorePublic

public final class TransitioningBlock: BlockWithLayout, WrapperBlock {
  public typealias Layout = CGRect
  public let from: Block?
  public let to: Block
  public let animationOut: [TransitioningAnimation]?
  public let animationIn: [TransitioningAnimation]?

  public var child: Block { to }

  public init(
    from: Block?,
    to: Block,
    animationOut: [TransitioningAnimation]?,
    animationIn: [TransitioningAnimation]?
  ) {
    self.from = from
    self.to = to
    self.animationOut = animationOut
    self.animationIn = animationIn
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? Self else {
      return false
    }

    return self == other
  }

  public func makeCopy(wrapping block: Block) -> Self {
    Self(
      from: from,
      to: block,
      animationOut: animationOut,
      animationIn: animationIn
    )
  }

  public static func ==(lhs: TransitioningBlock, rhs: TransitioningBlock) -> Bool {
    lhs.from == rhs.from &&
      lhs.to == rhs.to &&
      lhs.animationOut == rhs.animationOut &&
      lhs.animationIn == rhs.animationIn
  }

  func laidOutHierarchy(for size: CGSize) -> (TransitioningBlock, Layout) {
    let block = Self(
      from: from?.laidOut(for: size),
      to: to.laidOut(for: size),
      animationOut: animationOut,
      animationIn: animationIn
    )
    return (block, CGRect(origin: .zero, size: size))
  }
}
