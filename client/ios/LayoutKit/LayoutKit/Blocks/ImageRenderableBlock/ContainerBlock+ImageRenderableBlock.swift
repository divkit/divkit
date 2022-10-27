import CoreGraphics
import Foundation

import CommonCore

extension ContainerBlock: ImageRenderableBlock {
  public func drawInRect(_ rect: CGRect, context: CGContext) {
    let layout = ContainerBlockLayout(
      children: children,
      separator: separator,
      lineSeparator: lineSeparator,
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      size: rect.size
    )

    let frames = layout.blockFrames
    let blocks = layout.childrenWithSeparators.map { $0.content as! ImageRenderableBlock }
    context.inSeparateGState {
      blocks.draw(in: context, offset: rect.origin, frames: frames)
    }
  }
}
