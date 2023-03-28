import CoreGraphics

import CommonCorePublic

extension LayeredBlock: ImageRenderableBlock {
  public func drawInRect(_ rect: CGRect, context: CGContext) {
    let frames = makeChildrenFrames(size: rect.size)

    let blocks = children.map { $0.content as! ImageRenderableBlock }

    context.inSeparateGState {
      blocks.draw(in: context, offset: rect.origin, frames: frames)
    }
  }
}
