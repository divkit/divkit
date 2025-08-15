import Darwin
import DivKit
import Foundation
#if os(iOS)
import UIKit
#endif
import VGSL

#if os(iOS)
@objc
private class CGSVGDocument: NSObject {}

private var CGSVGDocumentCreateFromData: (
  @convention(c) (CFData?, CFDictionary?)
    -> Unmanaged<CGSVGDocument>?
) = load("CGSVGDocumentCreateFromData")

private var CGSVGDocumentGetCanvasSize: (@convention(c) (CGSVGDocument?) -> CGSize) =
  load("CGSVGDocumentGetCanvasSize")

private var CGContextDrawSVGDocument: (@convention(c) (CGContext?, CGSVGDocument?) -> Void) =
  load("CGContextDrawSVGDocument")

private var CGSVGDocumentRelease: (@convention(c) (CGSVGDocument?) -> Void) =
  load("CGSVGDocumentRelease")

private let CoreSVG = dlopen(
  "/System/Library/PrivateFrameworks/CoreSVG.framework/CoreSVG",
  RTLD_NOW
)

private func load<T>(_ name: String) -> T {
  unsafeBitCast(dlsym(CoreSVG, name), to: T.self)
}

public final class SVGDecoder {
  public init() {}

  public func decode(data: Data) -> Image? {
    guard let document = CGSVGDocumentCreateFromData(data as CFData, nil)?.takeUnretainedValue()
    else {
      return nil
    }
    defer {
      CGSVGDocumentRelease(document)
    }
    let originSize = CGSVGDocumentGetCanvasSize(document)
    guard originSize != .zero else {
      return nil
    }
    let scale = PlatformDescription.screenScale()
    let size = CGSize(width: originSize.width * scale, height: originSize.height * scale)
    let render = UIGraphicsImageRenderer(size: size)
    let image = render.image { context in
      let cgContext = context.cgContext
      cgContext.translateBy(x: 0, y: size.height)
      cgContext.scaleBy(x: scale, y: -scale)
      CGContextDrawSVGDocument(cgContext, document)
    }
    return image
  }
}
#else
public final class SVGDecoder {
  public init() {}
  public func decode(data _: Data) -> Image? { nil }
}
#endif
