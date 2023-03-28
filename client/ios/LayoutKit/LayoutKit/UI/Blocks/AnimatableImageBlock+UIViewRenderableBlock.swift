import Foundation
import UIKit

import CommonCorePublic

extension AnimatableImageBlock {
  public static func makeBlockView() -> BlockView { AnimatableImageContainer(frame: .zero) }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let animatableImageView = view as! AnimatableImageContainer
    if animatableImageView.imageHolder !== imageHolder {
      animatableImageView.imageHolder = imageHolder
    }
    animatableImageView.imageContentMode = contentMode
    animatableImageView.isUserInteractionEnabled = false
    animatableImageView.applyAccessibility(accessibilityElement)
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

  public var imageHolder: ImageHolder? {
    didSet {
      imageRequest?.cancel()
      self.imageView.image = nil
      backgroundModel = imageHolder?.placeholder.flatMap(ImageViewBackgroundModel.init)

      let newValue = imageHolder
      imageRequest = imageHolder?.requestImageWithCompletion { [weak self] image in
        guard let self = self,
              newValue === self.imageHolder else {
          return
        }
        self.imageView.image = image
        self.backgroundModel = nil
      }
    }
  }

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
}

private class AnimatableImageView: UIImageView {
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
    didSet {
      updateGravity()
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
