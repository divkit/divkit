import CoreGraphics
import Foundation

import CommonCore

extension ContainerBlock: ImageRenderableBlock {
  public func drawInRect(_ rect: CGRect, context: CGContext) {
    let frames = ContainerBlockLayout(
      children: children,
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      size: rect.size
    ).blockFrames

    let blocks = children.map { $0.content as! ImageRenderableBlock }
    context.inSeparateGState {
      blocks.draw(in: context, offset: rect.origin, frames: frames)
    }
  }
}
