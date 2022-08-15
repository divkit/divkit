import CoreGraphics

extension Array where Element == ImageRenderableBlock {
  func draw(
    in context: CGContext,
    offset: CGPoint,
    frames: [CGRect]
  ) {
    assert(count == frames.count)
    zip(self, frames).forEach { block, frame in
      let blockRect = frame.offset(by: offset)
      block.drawInRect(blockRect, context: context)
    }
  }
}
