// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

#if os(iOS) || os(tvOS)
import UIKit

extension UIImage.Orientation {
  public var sizeDimensionsAreFlippedInCGImage: Bool {
    switch self {
    case .left, .leftMirrored, .right, .rightMirrored:
      return true
    case .up, .upMirrored, .down, .downMirrored:
      return false
    @unknown default:
      return false
    }
  }
}
#endif

extension Image {
  public class func imageWithSolidColor(_ color: Color, size: CGSize) -> Image? {
    makeImage(color, size)
  }

  public class func transparentImageWithSolidColor(_ color: Color, size: CGSize) -> Image? {
    makeTransparentImage(color, size)
  }

  public class func resizableImageWithColor(_ color: Color, opaque: Bool = true) -> Image? {
    let pointSize = CGSize(width: 1, height: 1)
    return imageOfSize(pointSize, opaque: opaque) { ctx in
      ctx.setFillColor(color.cgColor)
      ctx.fill(CGRect(origin: .zero, size: pointSize))
    }?.resizableImage(withCapInsets: .zero)
  }

  fileprivate func convertSizeForCGContext(_ size: CGSize) -> CGSize {
    let contextSize: CGSize
    #if os(iOS) || os(tvOS)
    contextSize = imageOrientation.fixDimensions(in: size)
    #else
    contextSize = size
    #endif

    return contextSize
  }

  public func resizedImageWithSize(_ newSize: CGSize, opaque: Bool = true) -> Image? {
    let contextSize = convertSizeForCGContext(newSize)

    let cgImage = cgImg!
    let drawAction: (CGContext) -> Void = { ctx in
      ctx.draw(cgImage, in: CGRect(origin: .zero, size: contextSize))
    }

    #if os(iOS) || os(tvOS)
    return type(of: self).imageOfSize(
      contextSize,
      opaque: opaque,
      scale: scale,
      orientation: imageOrientation,
      transformForUIKitCompatibility: false,
      drawingHandler: drawAction
    )
    #else
    return type(of: self).imageOfSize(
      contextSize,
      opaque: opaque,
      scale: scale,
      drawingHandler: drawAction
    )
    #endif
  }

  public func imageWithMaxSize(_ fitSize: CGSize) -> Image? {
    if size.width < fitSize.width, size.height < fitSize.height {
      return self
    }
    guard let targetSize = size.sizeToFit(size: fitSize) else {
      return nil
    }
    return resizedImageWithSize(targetSize)
  }

  public func addingShadow(offset: CGSize, blur: CGFloat, color: Color) -> Image {
    InternalImageDescriptor(image: self)
      .imageWithShadow(offset: offset, blur: blur, color: color)
  }

  public func redrawn(
    withTintColor color: Color,
    size: CGSize? = nil
  ) -> Image {
    redrawn(withSize: size, overlayDrawingHandler: { ctx, rect in
      ctx.setBlendMode(.sourceIn)
      ctx.setFillColor(color.cgColor)
      ctx.fill(rect)
    })
  }

  public func redrawn(
    withLinearGradientTint gradient: Gradient.Linear,
    size: CGSize? = nil
  ) -> Image {
    redrawn(withSize: size, overlayDrawingHandler: { ctx, rect in
      guard let cgGradient = CGGradient(
        colorsSpace: CGColorSpaceCreateDeviceRGB(),
        colors: gradient.colors.map { $0.cgColor } as CFArray,
        locations: gradient.locations
      ) else {
        return
      }
      guard let img = ctx.makeImage() else { return }
      ctx.saveGState()
      ctx.clip(to: rect, mask: img)
      ctx.translateBy(x: 0, y: rect.height)
      ctx.scaleBy(x: 1, y: -1)
      ctx.drawLinearGradient(
        cgGradient,
        start: gradient.direction.from.absolutePosition(in: rect),
        end: gradient.direction.to.absolutePosition(in: rect),
        options: [.drawsBeforeStartLocation, .drawsAfterEndLocation]
      )
      ctx.restoreGState()
    })
  }

  public func redrawn(
    withOverlayedText text: NSAttributedString,
    size: CGSize? = nil
  ) -> Image {
    redrawn(withSize: size, overlayDrawingHandler: { ctx, rect in
      ctx.translateBy(x: 0, y: rect.height)
      ctx.scaleBy(x: 1, y: -1)
      text.draw(inContext: ctx, rect: rect)
    })
  }

  public func redrawn(
    withSize size: CGSize? = nil,
    overlayDrawingHandler drawHandler: (CGContext, CGRect) -> Void
  ) -> Image {
    let contextSize = convertSizeForCGContext(size ?? self.size)

    #if os(iOS) || os(tvOS)
    return Image.imageOfSize(
      contextSize,
      scale: scale,
      orientation: imageOrientation,
      transformForUIKitCompatibility: false,
      drawingHandler: { ctx in
        let rect = CGRect(origin: .zero, size: contextSize)
        ctx.draw(cgImg!, in: rect)
        drawHandler(ctx, rect)
      }
    )!
    #else
    return Image.imageOfSize(
      contextSize,
      drawingHandler: { ctx in
        let rect = CGRect(origin: .zero, size: contextSize)
        ctx.draw(cgImg!, in: rect)
        drawHandler(ctx, rect)
      }
    )!
    #endif
  }

  fileprivate func hasDataEqualTo(_ image: Image) -> Bool {
    guard self !== image else {
      return true
    }

    let selfData = binaryRepresentation()
    let anotherData = image.binaryRepresentation()
    return (selfData == anotherData)
  }

  public func imageThatFills(_ newSize: CGSize) -> Image? {
    guard size != .zero, size.hasValidDimensions,
          newSize != .zero, newSize.hasValidDimensions else { return nil }
    guard newSize.width != size.width || newSize.height != size.height else { return self }

    let scaleRatio = max(newSize.width / size.width, newSize.height / size.height)
    let scaledSize = size * scaleRatio

    let contextSize = convertSizeForCGContext(newSize)
    let convertedScaleSize = convertSizeForCGContext(scaledSize)
    let drawRect = CGRect(
      x: (contextSize.width - convertedScaleSize.width) / 2,
      y: (contextSize.height - convertedScaleSize.height) / 2,
      width: convertedScaleSize.width, height: convertedScaleSize.height
    )

    let render: (Image) -> Image = {
      let cgImage = $0.cgImg!
      let drawAction: (CGContext) -> Void = {
        $0.draw(cgImage, in: drawRect)
      }

      #if os(iOS) || os(tvOS)
      return Image.imageOfSize(
        contextSize,
        opaque: cgImage.isOpaque,
        scale: $0.scale,
        orientation: $0.imageOrientation,
        transformForUIKitCompatibility: false,
        drawingHandler: drawAction
      ) ?? $0
      #else
      return Image.imageOfSize(contextSize, drawingHandler: drawAction) ?? $0
      #endif
    }

    #if os(iOS) || os(tvOS)
    if let frames = images?.map(render) {
      return Image.animatedImage(with: frames, duration: duration)
    }
    #endif

    return render(self)
  }

  public var verticallyFlipped: Image {
    #if os(iOS) || os(tvOS)
    Image(cgImage: cgImg!, scale: scale, orientation: .upMirrored)
    #else
    // orientation not supported for NSImage
    return self
    #endif
  }
}

extension Image {
  public var sizeDimensionsAreFlippedInCGImage: Bool {
    #if os(iOS) || os(tvOS)
    return imageOrientation.sizeDimensionsAreFlippedInCGImage
    #else
    // orientation not supported for NSImage
    return false
    #endif
  }
}

private let makeImage = memoize { (color: Color, size: CGSize) -> Image? in
  Image.imageOfSize(size, opaque: true) { ctx in
    ctx.setFillColor(color.cgColor)
    ctx.fill(CGRect(origin: .zero, size: size))
  }
}

private let makeTransparentImage = memoize { (color: Color, size: CGSize) -> Image? in
  Image.imageOfSize(size) { ctx in
    ctx.setFillColor(color.cgColor)
    ctx.fill(CGRect(origin: .zero, size: size))
  }
}

public func imagesDataAreEqual(_ lhs: Image?, _ rhs: Image?) -> Bool {
  switch (lhs, rhs) {
  case (.none, .none):
    return true
  case let (.some(image1), .some(image2)):
    if image1 === image2 {
      return true
    }
    if image1.size != image2.size {
      return false
    }
    return image1.isEqual(image2) || image1.hasDataEqualTo(image2)
  default:
    return false
  }
}

extension Image: ImageHolder {
  public var image: Image? { self }
  public var placeholder: ImagePlaceholder? { .image(self) }

  public func requestImageWithCompletion(_ completion: @escaping ((Image?) -> Void))
    -> Cancellable? {
    onMainThread {
      completion(self)
    }
    return nil
  }

  public func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    (placeholder === .image(self) && remoteImageURL == nil) ? self : nil
  }

  public func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? Image else {
      return false
    }

    return imagesDataAreEqual(self, other)
  }
}

extension Image {
  public func calculateFastNonPreciseAverageColor() -> Color? {
    var colorComponents: UInt32 = 0
    guard
      let cgIm = cgImg,
      let ctx = CGContext(
        data: &colorComponents,
        width: 1,
        height: 1,
        bitsPerComponent: 8,
        bytesPerRow: 4,
        space: CGColorSpaceCreateDeviceRGB(),
        bitmapInfo: CGImageAlphaInfo.noneSkipFirst.rawValue | CGBitmapInfo.byteOrder32Little
          .rawValue
      )
    else {
      return nil
    }
    ctx.interpolationQuality = .high
    ctx.draw(cgIm, in: CGRect(origin: .zero, size: CGSize(squareDimension: 1)))

    return Color.colorWithARGBHexCode(colorComponents)
  }
}

extension CGImage {
  fileprivate var isOpaque: Bool {
    switch alphaInfo {
    case .none, .noneSkipLast, .noneSkipFirst: return true
    case .alphaOnly, .first, .last, .premultipliedFirst, .premultipliedLast: return false
    @unknown default: return false
    }
  }
}

extension InternalImageDescriptor {
  fileprivate init(image: Image) {
    let size = image.convertSizeForCGContext(image.size)
    let drawingHandler: (CGContext) -> Void = { [size] in
      $0.draw(image.cgImg!, in: CGRect(origin: .zero, size: size))
    }
    self.init(drawingHandler: drawingHandler, size: size)
  }
}
