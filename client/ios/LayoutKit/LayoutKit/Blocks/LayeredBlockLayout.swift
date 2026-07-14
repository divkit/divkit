import CoreGraphics
import VGSL

extension LayeredBlock {
  func makeChildrenFrames(size: CGSize) -> [CGRect] {
    children.map { child in
      let block = child.content
      var blockSize = block.size(forResizableBlockSize: size)
      // A match_parent child (modeled as constrained wrap_content) stretches to fill the resolved
      // container size on its match_parent axis, clamped by its own min/max.
      if child.fillsWidth {
        blockSize.width = clamp(
          max(blockSize.width, size.width),
          min: block.minWidth,
          max: block.maxWidth
        )
      }
      if child.fillsHeight {
        blockSize.height = clamp(
          max(blockSize.height, size.height),
          min: block.minHeight,
          max: block.maxHeight
        )
      }
      let availableSize = size - blockSize
      let frame = CGRect(
        origin: child.alignment.offset(forAvailableSpace: availableSize),
        size: blockSize
      ).roundedToScreenScale
      assert(frame.isValidAndFinite)
      return frame
    }
  }
}
