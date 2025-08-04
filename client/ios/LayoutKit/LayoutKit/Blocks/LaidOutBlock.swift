import CoreGraphics
import VGSL

final class LaidOutBlock<T: BlockWithLayout>: Block {
  public let block: T
  public let layout: T.Layout
  public let isVerticallyResizable = false
  public let isHorizontallyResizable = false

  private let size: CGSize

  public var isVerticallyConstrained: Bool { block.isVerticallyConstrained }
  public var isHorizontallyConstrained: Bool { block.isHorizontallyConstrained }

  public var intrinsicContentWidth: CGFloat {
    size.width
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat { size.width }
  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  public var debugDescription: String {
    "L\(size.width)x\(size.height) \(block.debugDescription)"
  }

  public convenience init(block: T, width: CGFloat) {
    let height = block.heightOfVerticallyNonResizableBlock(forWidth: width)
    self.init(block: block, size: CGSize(width: width, height: height))
  }

  public init(block: T, size: CGSize) {
    self.size = size

    (self.block, layout) = block.laidOutHierarchy(for: size)
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    size.height
  }

  public func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat {
    size.height
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? LaidOutBlock<T> else {
      return false
    }

    return block == other.block && size == other.size
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

extension LaidOutBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> LaidOutBlock {
    let newBlock = try block.updated(path: path, isFocused: isFocused)
    return newBlock === block ? self : LaidOutBlock(block: newBlock, width: size.width)
  }
}

extension LaidOutBlock: LayoutCachingDefaultImpl {}
