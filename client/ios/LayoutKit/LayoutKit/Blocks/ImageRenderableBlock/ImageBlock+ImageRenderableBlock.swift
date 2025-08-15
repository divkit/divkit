import CoreGraphics
import Foundation
import VGSL

extension ImageBlock: ImageRenderableBlock {
  public func drawInRect(_ rect: CGRect, context: CGContext) {
    switch imageHolder.placeholder {
    case let .image(image)?:
      if let img = image.cgImg {
        context.draw(img, in: rect)
      }
    case let .color(color)?:
      context.setFillColor(color.cgColor)
      context.fill(rect)
    case .none, .view?, .imageData?:
      break
    }
  }
}
