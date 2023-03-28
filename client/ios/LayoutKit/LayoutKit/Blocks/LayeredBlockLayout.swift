import CoreGraphics

import CommonCorePublic

extension LayeredBlock {
  func makeChildrenFrames(size: CGSize) -> [CGRect] {
    children.map { child in
      let block = child.content
      let blockSize = block.size(forResizableBlockSize: size)
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
