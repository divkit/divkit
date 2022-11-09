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

  public var lastTexture: MTLTexture?

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
  }

  public func setImage(_ image: UIImage?, animated: Bool?) {
    self.image = redrawingImage(image, imageRedrawingStyle: imageRedrawingStyle)
    uiImage = image
    metalView.setNeedsDisplay()
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
      if oldValue != imageRedrawingStyle, let uiImage {
        image = redrawingImage(uiImage, imageRedrawingStyle: imageRedrawingStyle)
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

extension UIImage.Orientation {
  fileprivate var layerTransform: CGAffineTransform {
    switch self {
    case .up:
      return .identity
    case .upMirrored:
      return CGAffineTransform(scaleX: -1, y: 1)
    case .down:
      return CGAffineTransform(rotationAngle: .pi)
    case .downMirrored:
      return CGAffineTransform(rotationAngle: .pi).scaledBy(x: -1, y: 1)
    case .left:
      return CGAffineTransform(rotationAngle: -.pi / 2)
    case .leftMirrored:
      return CGAffineTransform(rotationAngle: -.pi / 2).scaledBy(x: -1, y: 1)
    case .right:
      return CGAffineTransform(rotationAngle: .pi / 2)
    case .rightMirrored:
      return CGAffineTransform(rotationAngle: .pi / 2).scaledBy(x: -1, y: 1)
    @unknown default:
      return .identity
    }
  }
}

extension CALayer {
  fileprivate func setContents(_ source: Image?) {
    contents = source?.cgImage
    contentsScale = source?.scale ?? 1
    setAffineTransform(source?.imageOrientation.layerTransform ?? .identity)
  }
}

fileprivate func redrawingImage(
  _ image: UIImage?,
  imageRedrawingStyle: ImageRedrawingStyle?
) -> CIImage? {
  guard let image,
        let cgImage = image.cgImage else {
    return nil
  }
  let baseImage = CIImage(cgImage: cgImage)

  guard let tintColor = imageRedrawingStyle?.tintColor.ciColor,
        let coloredImage = ImageGeneratorType.constantColor(color: tintColor).imageGenerator(),
        let ciImage = ImageComposerType.sourceAtop.imageComposer(baseImage)(coloredImage) else {
    return baseImage
  }

  return ciImage.cropped(to: baseImage.extent)
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

    let boundsSize: CGSize
    if imageContentMode.scale == .noScale {
      boundsSize = drawableSize
    } else {
      boundsSize = view.bounds.size
    }

    let layout = layout(
      contentMode: imageContentMode,
      contentSize: image.extent.size,
      boundsSize: boundsSize
    )

    let ciContext = CIContext()
    let colorSpace = CGColorSpaceCreateDeviceRGB()

    let bounds = CGRect(origin: CGPoint.zero, size: drawableSize)

    let screenFactorX = drawableSize.width / boundsSize.width
    let screenFactorY = drawableSize.height / boundsSize.height
    let scaleX = layout.width / image.extent.width * screenFactorX
    let scaleY = layout.height / image.extent.height * screenFactorY

    let scaledImage = image
    #if targetEnvironment(simulator)
      // Transform to unflip
      .transformed(by: CGAffineTransform(scaleX: 1, y: -1))
      .transformed(by: CGAffineTransform(translationX: 0, y: image.extent.height))
    #endif
    .transformed(by: CGAffineTransform(scaleX: scaleX, y: scaleY))
      .transformed(by: CGAffineTransform(
        translationX: layout.origin.x * screenFactorX,
        y: layout.origin.y * screenFactorY
      ))

    ciContext.render(
      scaledImage,
      to: drawable.texture,
      commandBuffer: buffer,
      bounds: bounds,
      colorSpace: colorSpace
    )

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
    y = 0
  case .center:
    y = diff.height / 2
  case .bottom:
    y = diff.height
  }
  return CGPoint(x: x, y: y)
}
