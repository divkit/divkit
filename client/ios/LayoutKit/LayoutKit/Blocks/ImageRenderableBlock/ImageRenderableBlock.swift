// Copyright 2016 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import CommonCore

public protocol ImageRenderableBlock: Block {
  func drawInRect(_ rect: CGRect, context: CGContext)
}

extension ImageRenderableBlock {
  public func makeUIImage(withSize size: CGSize) -> Image {
    Image.imageOfSize(size) { context in
      let rect = CGRect(origin: .zero, size: size)
      self.drawInRect(rect, context: context)
    }!
  }
}
