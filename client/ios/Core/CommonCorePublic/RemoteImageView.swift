// Copyright 2022 Yandex LLC. All rights reserved.

import UIKit

public final class RemoteImageView: UIView, RemoteImageViewContentProtocol {
  private let contentsLayer = CALayer()
  private lazy var clipMask: CALayer = {
    let mask = CALayer()
    mask.backgroundColor = UIColor.white.cgColor
    return mask
  }()

  public var appearanceAnimation: ImageViewAnimation?

  private var templateLayer: CALayer?

  private func updateContent() {
    let content = Content(
      image: image,
      imageRedrawingColor: imageRedrawingStyle?.tintColor
    )

    let gravity = imageContentMode
      .contentsGravity(isGeometryFlipped: contentsLayer.contentsAreFlipped())

    CATransaction.performWithoutAnimations {
      switch content {
      case let .plain(image):
        contentsLayer.setContents(image)
        contentsLayer.contentsGravity = gravity
        contentsLayer.backgroundColor = nil
        contentsLayer.mask = nil
        templateLayer = nil
      case let .template(image, color):
        contentsLayer.setContents(nil)
        contentsLayer.contentsGravity = .resize
        contentsLayer.backgroundColor = color.cgColor
        let template = templateLayer ?? CALayer()
        template.setContents(image)
        template.contentsGravity = gravity
        contentsLayer.mask = template
        templateLayer = template
      }
    }
    setNeedsLayout()
    layoutIfNeeded()
  }

  public func setImage(_ image: UIImage?, animated: Bool?) {
    self.image = image
    if let appearanceAnimation = appearanceAnimation, animated == true {
      self.alpha = appearanceAnimation.startAlpha
      updateContent()
      UIView.animate(
        withDuration: appearanceAnimation.duration,
        delay: appearanceAnimation.delay,
        options: appearanceAnimation.options,
        animations: { self.alpha = appearanceAnimation.endAlpha },
        completion: nil
      )
    } else {
      updateContent()
    }
    setNeedsLayout()
    layoutIfNeeded()
  }

  private var image: UIImage?

  public var imageRedrawingStyle: ImageRedrawingStyle? {
    didSet { updateContent() }
  }

  public var imageContentMode = ImageContentMode.default {
    didSet { updateContent() }
  }

  public override init(frame: CGRect) {
    super.init(frame: frame)
    layer.addSublayer(contentsLayer)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    super.layoutSubviews()

    CATransaction.performWithoutAnimations {
      let layout = ImageLayerLayout(
        contentMode: imageContentMode,
        contentSize: image?.size ?? .zero,
        boundsSize: bounds.size,
        capInsets: image?.capInsets ?? .zero
      )

      contentsLayer.frame = layout.frame
      contentsLayer.contentsRect = layout.contentRect
      contentsLayer.contentsCenter = layout.contentCenter

      templateLayer?.frame = contentsLayer.bounds
      templateLayer?.contentsRect = layout.contentRect
      templateLayer?.contentsCenter = layout.contentCenter

      updateMask()
    }
  }

  private func updateMask() {
    let contentsHeight = templateLayer?.contentsRect.height ?? contentsLayer.contentsRect.height

    // MOBYANDEXIOS-376: for now, coded the layout only for top-directed content collapsing
    if contentsHeight > 1.0 {
      clipMask.frame = CGRect(
        x: 0,
        y: 0,
        width: bounds.width,
        height: layer.bounds.height / contentsHeight
      )

      layer.mask = clipMask
    } else if layer.mask != nil {
      layer.mask = nil
    }
    layer.masksToBounds = true
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

private enum Content {
  case plain(Image?)
  case template(Image, color: Color)

  init(image: Image?, imageRedrawingColor: Color?) {
    if let image = image, let color = imageRedrawingColor {
      self = .template(image, color: color)
    } else {
      self = .plain(image)
    }
  }
}

extension URLRequestResult.Source {
  var shouldAnimate: Bool {
    switch self {
    case .network: return true
    case .cache: return false
    }
  }
}
