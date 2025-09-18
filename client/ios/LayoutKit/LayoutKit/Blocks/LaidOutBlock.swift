import CoreGraphics
import VGSL

final class LaidOutBlock<T: BlockWithLayout>: Block {
  let block: T
  let layout: T.Layout
  let isVerticallyResizable = false
  let isHorizontallyResizable = false

  private let size: CGSize

  var isVerticallyConstrained: Bool { block.isVerticallyConstrained }
  var isHorizontallyConstrained: Bool { block.isHorizontallyConstrained }

  var intrinsicContentWidth: CGFloat {
    size.width
  }

  var widthOfHorizontallyNonResizableBlock: CGFloat { size.width }
  var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  var debugDescription: String {
    "L\(size.width)x\(size.height) \(block.debugDescription)"
  }

  convenience init(block: T, width: CGFloat) {
    let height = block.heightOfVerticallyNonResizableBlock(forWidth: width)
    self.init(block: block, size: CGSize(width: width, height: height))
  }

  init(block: T, size: CGSize) {
    self.size = size

    (self.block, layout) = block.laidOutHierarchy(for: size)
  }

  func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    size.height
  }

  func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat {
    size.height
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? LaidOutBlock<T> else {
      return false
    }

    return block == other.block && size == other.size
  }
}

extension LaidOutBlock: ImageContaining {
  func getImageHolders() -> [ImageHolder] {
    block.getImageHolders()
  }
}

extension LaidOutBlock {
  func updated(withStates states: BlocksState) throws -> LaidOutBlock {
    let newBlock = try block.updated(withStates: states)
    return newBlock === block ? self : LaidOutBlock(block: newBlock, width: size.width)
  }
}

extension LaidOutBlock: ElementFocusUpdating {
  func updated(path: UIElementPath, isFocused: Bool) throws -> LaidOutBlock {
    let newBlock = try block.updated(path: path, isFocused: isFocused)
    return newBlock === block ? self : LaidOutBlock(block: newBlock, width: size.width)
  }
}

extension LaidOutBlock: LayoutCachingDefaultImpl {}
