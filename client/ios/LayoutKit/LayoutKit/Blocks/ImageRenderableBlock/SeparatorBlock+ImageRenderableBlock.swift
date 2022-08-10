import CoreGraphics
import Foundation

extension SeparatorBlock: ImageRenderableBlock {
  public func drawInRect(_ rect: CGRect, context: CGContext) {
    context.setFillColor(color.cgColor)
    context.fill(rect)
  }
}
