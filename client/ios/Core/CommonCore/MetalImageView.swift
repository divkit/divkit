// Copyright 2022 Yandex LLC. All rights reserved.

import MetalKit
import UIKit

import BaseUI

public final class MetalImageView: UIView, RemoteImageViewContentProtocol {
  public var appearanceAnimation: ImageViewAnimation?
  private(set) lazy var commandQueue: MTLCommandQueue? = metalView.device?.makeCommandQueue()
  private lazy var ciContext: CIContext? = metalView.device.map { CIContext(mtlDevice: $0) }
  private lazy var device = MTLCreateSystemDefaultDevice()
  private lazy var metalView: MTKView = {
    let mtkView = MTKView(frame: frame, device: device)
    mtkView.enableSetNeedsDisplay = true
    mtkView.framebufferOnly = false
    mtkView.delegate = self
    mtkView.backgroundColor = .clear
    addSubview(mtkView)
    return mtkView
  }()

  public override init(frame: CGRect) {
    super.init(frame: frame)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    metalView.frame = bounds
    image = redrawingImage(uiImage, bounds: bounds, imageRedrawingStyle: imageRedrawingStyle)
  }

  public func setImage(_ image: UIImage?, animated: Bool?) {
    uiImage = image
    setNeedsLayout()
    if let appearanceAnimation = appearanceAnimation, animated == true {
      self.alpha = appearanceAnimation.startAlpha
      UIView.animate(
        withDuration: appearanceAnimation.duration,
        delay: appearanceAnimation.delay,
        options: appearanceAnimation.options,
        animations: { self.alpha = appearanceAnimation.endAlpha },
        completion: nil
      )
    }
  }

  private var image: CIImage? {
    didSet {
      metalView.setNeedsDisplay()
    }
  }

  private var uiImage: UIImage?

  public var imageRedrawingStyle: ImageRedrawingStyle? {
    didSet {
      if oldValue != imageRedrawingStyle {
        setNeedsLayout()
      }
    }
  }

  public var imageContentMode = ImageContentMode.default {
    didSet {
      if oldValue != imageContentMode {
        metalView.setNeedsDisplay()
      }
    }
  }
}

fileprivate func redrawingImage(
  _ image: UIImage?,
  bounds: CGRect,
  imageRedrawingStyle: ImageRedrawingStyle?
) -> CIImage? {
  guard let image,
        let cgImage = image.cgImage else {
    return nil
  }
  let ciImage = CIImage(cgImage: cgImage)
  let extent = ciImage.extent

  let identityFilter: ImageFilter = { $0 }

  let tintModeFilter: ImageFilter

  if let tintColor = imageRedrawingStyle?.tintColor,
     let coloredImage = ImageGeneratorType.constantColor(color: tintColor.ciColor)
     .imageGenerator() {
    let mode = imageRedrawingStyle?.tintMode ?? .sourceAtop
    tintModeFilter = { mode.composerType.imageComposer($0)(coloredImage) }
  } else {
    tintModeFilter = identityFilter
  }

  let filter = imageRedrawingStyle?.effects.compactMap {
    switch $0 {
    case let .blur(radius: radius):
      let scaleX = bounds.width / extent.width
      let scaleY = bounds.height / extent.height
      let scale = max(scaleX, scaleY) * 2
      return ImageBlurType.gaussian(radius: radius / scale).imageFilter
    case let .tint(color: color, mode: mode):
      guard let mode,
            let coloredImage = ImageGeneratorType.constantColor(color: color.ciColor)
            .imageGenerator()
      else { return nil }
      return { mode.composerType.imageComposer($0)(coloredImage) }
    }
  }.reduce(identityFilter, combine) ?? identityFilter

  let contentRect = CIVector(
    x: 0,
    y: 0,
    z: extent.width,
    w: extent.height
  )

  return combine(tintModeFilter, filter, ImageCropType.crop(rect: contentRect).imageFilter)(ciImage)
}

extension TintMode {
  fileprivate var composerType: ImageComposerType {
    switch self {
    case .sourceIn:
      return .sourceIn
    case .sourceAtop:
      return .sourceAtop
    case .darken:
      return .darken
    case .lighten:
      return .lighten
    case .multiply:
      return .multiply
    case .screen:
      return .screen
    }
  }
}

extension MetalImageView: MTKViewDelegate {
  public func mtkView(_: MTKView, drawableSizeWillChange _: CGSize) {}

  public func draw(in view: MTKView) {
    guard let drawable = view.currentDrawable,
          let commandQueue = commandQueue,
          let image = image else {
      return
    }
    let buffer = commandQueue.makeCommandBuffer()
    let drawableSize = view.drawableSize

    let layout = layout(
      contentMode: imageContentMode,
      contentSize: image.extent.size,
      boundsSize: view.bounds.size
    )

    let colorSpace = CGColorSpaceCreateDeviceRGB()

    let bounds = CGRect(origin: CGPoint.zero, size: drawableSize)

    let screenFactorX = drawableSize.width / view.bounds.width
    let screenFactorY = drawableSize.height / view.bounds.height
    let scaleX = layout.width / image.extent.width * screenFactorX
    let scaleY = layout.height / image.extent.height * screenFactorY

    let scaledImage = image
      .transformed(by: CGAffineTransform(scaleX: scaleX, y: scaleY))
      .transformed(by: CGAffineTransform(
        translationX: layout.origin.x * screenFactorX,
        y: layout.origin.y * screenFactorY
      ))

    let ciContext: CIContext?

    #if DEBUG
    ciContext = isSnapshotTest ? CIContext() : self.ciContext
    #else
    ciContext = self.ciContext
    #endif

    if #available(iOS 11, *) {
      let destination = CIRenderDestination(
        width: Int(drawableSize.width),
        height: Int(drawableSize.height),
        pixelFormat: view.colorPixelFormat,
        commandBuffer: buffer,
        mtlTextureProvider: { () -> MTLTexture in
          drawable.texture
        }
      )

      _ = try? ciContext?.startTask(toRender: scaledImage, to: destination)
    } else {
      ciContext?.render(
        scaledImage,
        to: drawable.texture,
        commandBuffer: buffer,
        bounds: bounds,
        colorSpace: colorSpace
      )
    }

    buffer?.present(drawable)
    buffer?.commit()
  }
}

private func layout(
  contentMode: ImageContentMode,
  contentSize: CGSize,
  boundsSize: CGSize
) -> CGRect {
  switch contentMode.scale {
  case .aspectFit:
    let size = contentSize.sizeToFit(size: boundsSize) ?? boundsSize
    let origin = makeOrigin(contentMode: contentMode, contentSize: size, boundsSize: boundsSize)
    return CGRect(origin: origin, size: size)
  case .aspectFill:
    let size = contentSize.sizeToFill(size: boundsSize) ?? boundsSize
    let origin = makeOrigin(contentMode: contentMode, contentSize: size, boundsSize: boundsSize)
    return CGRect(origin: origin, size: size)
  case .resize:
    return CGRect(origin: .zero, size: boundsSize)
  case .noScale:
    let origin = makeOrigin(
      contentMode: contentMode,
      contentSize: contentSize,
      boundsSize: boundsSize
    )
    return CGRect(origin: origin, size: contentSize)
  case .aspectWidth:
    let size = contentSize.sizeToFit(size: boundsSize) ?? boundsSize
    return CGRect(origin: .zero, size: size)
  }
}

private func makeOrigin(
  contentMode: ImageContentMode,
  contentSize: CGSize,
  boundsSize: CGSize
) -> CGPoint {
  let x: CGFloat
  let y: CGFloat
  let diff = boundsSize - contentSize
  switch contentMode.horizontalAlignment {
  case .left:
    x = 0
  case .center:
    x = diff.width / 2
  case .right:
    x = diff.width
  }
  switch contentMode.verticalAlignment {
  case .top:
    y = diff.height
  case .center:
    y = diff.height / 2
  case .bottom:
    y = 0
  }
  return CGPoint(x: x, y: y)
}

#if DEBUG
private let isSnapshotTest = ProcessInfo.processInfo
  .environment["XCTestConfigurationFilePath"] != nil
#endif
