import CoreGraphics

extension [ImageRenderableBlock] {
  func draw(
    in context: CGContext,
    offset: CGPoint,
    frames: [CGRect]
  ) {
    assert(count == frames.count)
    for (block, frame) in zip(self, frames) {
      let blockRect = frame.offset(by: offset)
      block.drawInRect(blockRect, context: context)
    }
  }
}
