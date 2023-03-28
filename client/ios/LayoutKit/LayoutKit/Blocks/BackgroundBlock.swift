import CoreGraphics
import Foundation

import CommonCorePublic

public final class BackgroundBlock: BlockWithLayout, WrapperBlock {
  typealias Layout = CGRect

  public let background: Background
  public let child: Block
  public let cornerRadius: CGFloat

  public init(
    background: Background,
    child: Block,
    cornerRadius: CGFloat = 0.0
  ) {
    self.background = background
    self.child = child
    self.cornerRadius = cornerRadius
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? BackgroundBlock else {
      return false
    }

    return self == other
  }

  public func makeCopy(wrapping block: Block) -> BackgroundBlock {
    BackgroundBlock(
      background: background,
      child: block,
      cornerRadius: cornerRadius
    )
  }

  public static func ==(lhs: BackgroundBlock, rhs: BackgroundBlock) -> Bool {
    lhs.background == rhs.background &&
      lhs.child == rhs.child &&
      lhs.cornerRadius == rhs.cornerRadius
  }

  func laidOutHierarchy(for size: CGSize) -> (BackgroundBlock, Layout) {
    let laidOutChild = child.laidOut(for: size)
    let block = BackgroundBlock(
      background: background,
      child: laidOutChild,
      cornerRadius: cornerRadius
    )
    return (block, CGRect(origin: .zero, size: size))
  }
}

extension BackgroundBlock {
  public func getImageHolders() -> [ImageHolder] {
    child.getImageHolders() + background.getImageHolders()
  }
}

extension Block {
  public func with(background: Background) -> BackgroundBlock {
    BackgroundBlock(background: background, child: self)
  }
}
