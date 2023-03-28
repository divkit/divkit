// Copyright 2022 Yandex LLC. All rights reserved.

import MetalKit
import UIKit

import BaseUIPublic

public final class MetalImageView: UIView, RemoteImageViewContentProtocol {
  public var appearanceAnimation: ImageViewAnimation?
  private lazy var commandQueue = metalView.device?.makeCommandQueue()
  private lazy var ciContext = metalView.device.map { CIContext(mtlDevice: $0) }
  private lazy var textureLoader = metalView.device.map(MTKTextureLoader.init(device:))
  private lazy var device = MTLCreateSystemDefaultDevice()
  private lazy var metalView = {
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
    metalView.setNeedsDisplay()
  }

  public func setImage(_ image: UIImage?, animated: Bool?) {
    uiImage = image

    let tryAnimate = {
      if animated == true {
        self.animateAppearance()
      }
    }
    if let ciImage = image?.ciImage {
      self.ciImage = ciImage
      tryAnimate()
    } else {
      guard let cgImage = image?.cgImage else {
        ciImage = nil
        return
      }
      ciImage = CIImage(cgImage: cgImage)
      tryAnimate()
    }
  }

  private var uiImage: UIImage?

  private var ciImage: CIImage? {
    didSet {
      if oldValue !== ciImage {
        metalView.setNeedsDisplay()
      }
    }
  }

  public var imageRedrawingStyle: ImageRedrawingStyle? {
    didSet {
      if oldValue != imageRedrawingStyle {
        metalView.setNeedsDisplay()
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

  private func animateAppearance() {
    if let appearanceAnimation = self.appearanceAnimation {
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
}

extension MetalImageView: MTKViewDelegate {
  public func mtkView(_: MTKView, drawableSizeWillChange _: CGSize) {}

  public func draw(in view: MTKView) {
    guard !view.bounds.isEmpty,
          let drawable = view.currentDrawable,
          let commandQueue = commandQueue,
          let currentRenderPassDescriptor = view.currentRenderPassDescriptor
    else {
      return
    }
    let buffer = commandQueue.makeCommandBuffer()
    currentRenderPassDescriptor.colorAttachments[0].clearColor = MTLClearColor()
    let renderEncoder = buffer?.makeRenderCommandEncoder(descriptor: currentRenderPassDescriptor)
    renderEncoder?.endEncoding()

    guard let ciImage = ciImage else {
      buffer?.present(drawable)
      buffer?.commit()
      return
    }

    let ciImageFactoryParams = CIImageFactoryParams(
      image: ciImage,
      bounds: view.bounds,
      redrawingStyle: imageRedrawingStyle
    )

    guard let image = makeFilteredImage(params: ciImageFactoryParams) else { return }

    let drawableSize = view.drawableSize

    let layout = layout(
      contentMode: imageContentMode,
      contentSize: image.extent.size,
      boundsSize: view.bounds.size
    )

    let bounds = CGRect(origin: CGPoint.zero, size: drawableSize)

    let screenFactorX = drawableSize.width / view.bounds.width
    let screenFactorY = drawableSize.height / view.bounds.height
    let scaleX = (layout.width * screenFactorX).rounded(.up) / image.extent.width
    let scaleY = (layout.height * screenFactorX).rounded(.up) / image.extent.height

    let scaledImage = image
      .transformed(by: CGAffineTransform(scaleX: scaleX, y: scaleY))
      .transformed(by: CGAffineTransform(
        translationX: (layout.origin.x * screenFactorX).rounded(.down),
        y: (layout.origin.y * screenFactorY).rounded(.down)
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
        colorSpace: CGColorSpaceCreateDeviceRGB()
      )
    }

    buffer?.present(drawable)
    buffer?.commit()
  }
}

private struct CIImageFactoryParams: Equatable {
  let image: CIImage
  let bounds: CGRect
  let redrawingStyle: ImageRedrawingStyle?

  static func ==(lhs: CIImageFactoryParams, rhs: CIImageFactoryParams) -> Bool {
    lhs.image === rhs.image &&
      lhs.bounds == rhs.bounds &&
      lhs.redrawingStyle == rhs.redrawingStyle
  }
}

private func makeFilteredImage(
  params: CIImageFactoryParams
) -> CIImage? {
  let ciImage = params.image
  let extent = ciImage.extent

  let identityFilter: ImageFilter = { $0 }

  let tintModeFilter: ImageFilter

  if let tintColor = params.redrawingStyle?.tintColor,
     let coloredImage = ImageGeneratorType.constantColor(color: tintColor.ciColor)
     .imageGenerator() {
    let mode = params.redrawingStyle?.tintMode ?? .sourceAtop
    tintModeFilter = { mode.composerType.imageComposer($0)(coloredImage) }
  } else {
    tintModeFilter = identityFilter
  }

  let filter = params.redrawingStyle?.effects.compactMap {
    switch $0 {
    case let .blur(radius: radius):
      let scaleX = params.bounds.width / extent.width
      let scaleY = params.bounds.height / extent.height
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
