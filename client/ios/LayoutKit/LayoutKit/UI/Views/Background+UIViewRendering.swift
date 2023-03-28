import Foundation
import UIKit

import CommonCorePublic

extension Background: UIViewRenderable {
  public static func makeBlockView() -> BlockView {
    BackgroundView()
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! BackgroundView
    view.configure(
      model: self,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    (view as? BackgroundView)?.canConfigureView(withModel: self) ?? false
  }
}

private final class BackgroundView: BlockView, VisibleBoundsTrackingLeaf {
  private var innerView: UIView? {
    didSet {
      guard oldValue !== innerView else { return }
      oldValue?.removeFromSuperview()
      if let innerView = innerView {
        addSubview(innerView)
      }
    }
  }

  var effectiveBackgroundColor: UIColor? { innerView?.backgroundColor }

  override func layoutSubviews() {
    super.layoutSubviews()

    innerView?.frame = bounds
  }

  private var model: Background!
  private weak var observer: ElementStateObserver?

  func configure(
    model: Background,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.model = model
    self.observer = observer

    let innerView = self.innerView ?? makeInnerView(
      observer: observer,
      overscrollDelegate: overscrollDelegate
    )

    configureInnerView(
      innerView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )

    innerView.isUserInteractionEnabled = model.isUserInteractionEnabled
    isUserInteractionEnabled = innerView.isUserInteractionEnabled

    self.innerView = innerView
  }
}

extension BackgroundView {
  private func makeInnerView(
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?
  ) -> UIView {
    let view: UIView
    switch model! {
    case .solidColor, .tiledImage:
      view = ColoredView()

    case let .image(backgroundImage):
      if backgroundImage.metalImageRenderingEnabled {
        view = RemoteImageViewContainer(contentView: MetalImageView())
      } else {
        view = RemoteImageViewContainer(contentView: RemoteImageView())
      }

    case .ninePatchImage:
      view = RemoteImageViewContainer(contentView: NinePatchImageView())

    case let .gradient(type):
      switch type {
      case let .box(color):
        view = BoxShadowView(shadowColor: color)
      case let .radial(gradient):
        view = RadialGradientView(gradient)
      case let .linear(gradient):
        view = LinearGradientView(gradient)
      }

    case let .transparentAction(action):
      view = ClickableView(action: action)

    case let .block(block):
      view = block.makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate
      )

    case let .composite(background1, background2, _):
      let view1 = background1.makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate
      )
      let view2 = background2.makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate
      )
      view = CompositeView(backView: view1, frontView: view2)

    case let .withInsets(background, insets):
      let innerView = background.makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate
      )
      view = ViewWithContentInsets(innerView: innerView, contentInsets: insets)
    }
    return view
  }

  private func configureInnerView(
    _ innerView: UIView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    switch model! {
    case let .solidColor(color):
      let coloredView = innerView as! ColoredView
      coloredView.backgroundColor = color.systemColor

    case let .tiledImage(image):
      let coloredView = innerView as! ColoredView
      coloredView.backgroundColor = UIColor(patternImage: image)

    case let .image(image):
      let imageViewContainer = innerView as! RemoteImageViewContainer
      imageViewContainer.contentView.clipsToBounds = true
      imageViewContainer.contentView.alpha = CGFloat(image.alpha)
      imageViewContainer.contentView.imageContentMode = image.contentMode
      imageViewContainer.contentView.imageRedrawingStyle = ImageRedrawingStyle(
        tintColor: nil,
        effects: image.effects
      )
      if imageViewContainer.imageHolder != image.imageHolder {
        imageViewContainer.imageHolder = image.imageHolder
      }

    case let .gradient(type):
      switch type {
      case let .box(color):
        let boxView = innerView as! BoxShadowView
        boxView.shadowColor = color
      case let .radial(gradient):
        let radialView = innerView as! RadialGradientView
        radialView.gradient = gradient
      case let .linear(gradient):
        let linearView = innerView as! LinearGradientView
        linearView.set(gradient: gradient)
      }

    case let .transparentAction(action):
      let clickableView = innerView as! ClickableView
      clickableView.action = action

    case let .block(block):
      let blockView = innerView as! BlockView
      block.configureBlockView(
        blockView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )

    case let .composite(background1, background2, blendingCoefficient):
      let compositeView = innerView as! CompositeView
      background1.configureBlockView(
        compositeView.backView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
      background2.configureBlockView(
        compositeView.frontView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
      compositeView.blendingCoefficient = blendingCoefficient

    case let .withInsets(background, contentInsets):
      let viewWithContentInsets = innerView as! ViewWithContentInsets
      background.configureBlockView(
        viewWithContentInsets.innerView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
      viewWithContentInsets.contentInsets = contentInsets
    case let .ninePatchImage(image):
      let imageViewContainer = innerView as! RemoteImageViewContainer
      let imageView = imageViewContainer.contentView as! NinePatchImageView
      imageView.capInsets = image.insets
      if imageViewContainer.imageHolder != image.imageHolder {
        imageViewContainer.imageHolder = image.imageHolder
      }
    }
  }

  func canConfigureView(withModel model: Background) -> Bool {
    guard let currentModel = self.model else { return true }
    switch (currentModel, model) {
    case (.solidColor, .solidColor),
         (.tiledImage, .tiledImage),
         (.image, .image),
         (.transparentAction, .transparentAction),
         (.ninePatchImage, .ninePatchImage),
         (.block, .block):
      return true

    case let (.gradient(type1), .gradient(type2)):
      switch (type1, type2) {
      case (.box, .box),
           (.radial, .radial):
        return true
      case let (.linear(gradient1), .linear(gradient2)):
        return gradient1.direction == gradient2.direction
      case (.box, _),
           (.radial, _),
           (.linear, _):
        return false
      }

    case let (
      .composite(_, _, _),
      .composite(background1, background2, _)
    ):
      guard let compositeView = innerView as? CompositeView else {
        return false
      }

      return background1.canConfigureBlockView(compositeView.backView) &&
        background2.canConfigureBlockView(compositeView.frontView)

    case let (.withInsets(_, _), .withInsets(background, _)):
      guard let viewWithContentInsets = innerView as? ViewWithContentInsets else {
        return false
      }
      return background.canConfigureBlockView(viewWithContentInsets.innerView)

    case (.solidColor, _),
         (.tiledImage, _),
         (.image, _),
         (.transparentAction, _),
         (.gradient, _),
         (.composite, _),
         (.withInsets, _),
         (.block, _),
         (.ninePatchImage, _):
      return false
    }
  }
}

private final class ColoredView: UIView {}

extension Background {
  fileprivate var isUserInteractionEnabled: Bool {
    switch self {
    case .solidColor,
         .tiledImage,
         .image,
         .gradient,
         .ninePatchImage,
         .block:
      return false
    case .transparentAction:
      return true
    case let .composite(lhs, rhs, _):
      return lhs.isUserInteractionEnabled || rhs.isUserInteractionEnabled
    case let .withInsets(background, _):
      return background.isUserInteractionEnabled
    }
  }
}
