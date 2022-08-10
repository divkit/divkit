import CoreGraphics
import Foundation

extension TextBlock: ImageRenderableBlock {
  public func drawInRect(_ rect: CGRect, context: CGContext) {
    text.draw(inContext: context, rect: rect)
  }
}
