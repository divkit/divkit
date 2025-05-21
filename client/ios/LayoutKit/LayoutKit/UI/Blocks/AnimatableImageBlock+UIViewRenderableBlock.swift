import Foundation
import UIKit
import VGSL

extension AnimatableImageBlock {
  public static func makeBlockView() -> BlockView { AnimatableImageContainer(frame: .zero) }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let animatableImageView = view as! AnimatableImageContainer
    animatableImageView.setImageHolder(imageHolder) { [weak self] in
      self?.updateStateIfNeeded(observer: observer)
    }
    animatableImageView.imageContentMode = contentMode
    animatableImageView.isUserInteractionEnabled = false
    animatableImageView.applyAccessibilityFromScratch(accessibilityElement)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is AnimatableImageContainer
  }
}

private class AnimatableImageContainer: UIStackView, BlockViewProtocol, VisibleBoundsTrackingLeaf {
  var imageRequest: Cancellable?
  var effectiveBackgroundColor: UIColor? { backgroundColor }

  private var backgroundModel: ImageViewBackgroundModel? {
    didSet {
      backgroundModel.applyTo(self, oldValue: oldValue)
    }
  }

  private var imageHolder: ImageHolder?

  var imageContentMode = ImageContentMode.default {
    didSet {
      if imageView.imageContentMode != imageContentMode {
        imageView.imageContentMode = imageContentMode
        setNeedsLayout()
      }
    }
  }

  private let imageView = AnimatableImageView(frame: .zero)

  public override init(frame: CGRect) {
    super.init(frame: frame)
    isUserInteractionEnabled = false
    layer.masksToBounds = true
    addArrangedSubview(imageView)
  }

  @available(*, unavailable)
  required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public func setImageHolder(_ imageHolder: ImageHolder, completion: @escaping () -> Void) {
    guard imageHolder !== self.imageHolder else { return }
    self.imageHolder = imageHolder

    imageRequest?.cancel()
    self.imageView.image = nil
    backgroundModel = imageHolder.placeholder.flatMap(ImageViewBackgroundModel.init)

    let newValue = imageHolder
    imageRequest = imageHolder.requestImageWithCompletion { [weak self] image in
      guard let self,
            newValue === self.imageHolder else {
        return
      }
      self.imageView.image = image
      self.backgroundModel = nil

      completion()
    }
  }
}

private class AnimatableImageView: UIImageView {
  override class var layerClass: AnyClass { AnimatableImageLayer.self }

  var animatableImageLayer: AnimatableImageLayer? {
    layer as? AnimatableImageLayer
  }

  public override init(frame: CGRect) {
    super.init(frame: frame)
    layer.masksToBounds = true
    backgroundColor = .clear
    isUserInteractionEnabled = false
    layer.contentsGravity = .center
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  var imageContentMode = ImageContentMode.default {
    didSet {
      updateGravity()
    }
  }

  override var image: UIImage? {
    willSet {
      animatableImageLayer?.updateAllowed = true
    }
    didSet {
      updateGravity()
      animatableImageLayer?.updateAllowed = false
    }
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    updateFramePosition()
  }

  private func updateGravity() {
    let gravity = imageContentMode
      .contentsGravity(isGeometryFlipped: layer.contentsAreFlipped())
    if gravity == layer.contentsGravity {
      return
    }
    CATransaction.performWithoutAnimations {
      layer.contentsGravity = gravity
    }
    setNeedsLayout()
  }

  private func updateFramePosition() {
    CATransaction.performWithoutAnimations {
      let layout = ImageLayerLayout(
        contentMode: imageContentMode,
        contentSize: image?.size ?? .zero,
        boundsSize: bounds.size,
        capInsets: image?.capInsets ?? .zero
      )
      layer.frame = layout.frame
      layer.contentsRect = layout.contentRect
      layer.contentsCenter = layout.contentCenter
    }
  }
}

private class AnimatableImageLayer: CALayer {
  var updateAllowed = false

  override func removeAllAnimations() {
    if updateAllowed {
      super.removeAllAnimations()
    }
  }
}
