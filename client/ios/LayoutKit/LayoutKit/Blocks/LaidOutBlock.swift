import CoreGraphics

import CommonCorePublic

final class LaidOutBlock<T: BlockWithLayout>: Block {
  public let block: T
  public let layout: T.Layout
  private let size: CGSize

  public convenience init(block: T, width: CGFloat) {
    let height = block.heightOfVerticallyNonResizableBlock(forWidth: width)
    self.init(block: block, size: CGSize(width: width, height: height))
  }

  public init(block: T, size: CGSize) {
    self.size = size

    (self.block, layout) = block.laidOutHierarchy(for: size)
  }

  public let isVerticallyResizable = false
  public let isHorizontallyResizable = false

  public var isVerticallyConstrained: Bool { block.isVerticallyConstrained }
  public var isHorizontallyConstrained: Bool { block.isHorizontallyConstrained }

  public var intrinsicContentWidth: CGFloat {
    size.width
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    size.height
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat { size.width }
  public func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat {
    size.height
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }
  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? LaidOutBlock<T> else {
      return false
    }

    return block == other.block && size == other.size
  }

  public var debugDescription: String {
    "L\(size.width)x\(size.height) \(block.debugDescription)"
  }
}

extension LaidOutBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    block.getImageHolders()
  }
}

extension LaidOutBlock {
  public func updated(withStates states: BlocksState) throws -> LaidOutBlock {
    let newBlock = try block.updated(withStates: states)
    return newBlock === block ? self : LaidOutBlock(block: newBlock, width: size.width)
  }
}

extension LaidOutBlock: LayoutCachingDefaultImpl {}
