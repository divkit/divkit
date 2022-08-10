// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics

// TODO: fix publicity of the internal struct
// and bring BaseUI import back in https://st.yandex-team.ru/IBRO-28090
public struct InternalImageDescriptor {
  public let drawingHandler: (CGContext) -> Void
  public let size: CGSize

  public init(drawingHandler: @escaping (CGContext) -> Void, size: CGSize) {
    self.drawingHandler = drawingHandler
    self.size = size
  }

  public func imageWithShadow(offset: CGSize, blur: CGFloat, color: Color) -> Image {
    let spaceExpansionX = abs(offset.width) + blur
    let spaceExpansionY = abs(offset.height) + blur
    let requiredSize = CGSize(
      width: size.width + 2 * spaceExpansionX,
      height: size.height + 2 * spaceExpansionY
    )
    let internalDrawingHandler: (CGContext) -> Void = { ctx in
      ctx.translateBy(x: spaceExpansionX, y: spaceExpansionY)
      ctx.setShadow(offset: offset, blur: blur, color: color.cgColor)
      ctx.beginTransparencyLayer(auxiliaryInfo: nil)
      drawingHandler(ctx)
      ctx.endTransparencyLayer()
    }
    #if os(iOS)
    return Image.imageOfSize(
      requiredSize,
      transformForUIKitCompatibility: false,
      drawingHandler: internalDrawingHandler
    )!
    #else
    return Image.imageOfSize(
      requiredSize,
      drawingHandler: internalDrawingHandler
    )!
    #endif
  }
}
