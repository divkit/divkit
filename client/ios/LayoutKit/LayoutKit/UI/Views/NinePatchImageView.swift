import UIKit

import BasePublic
import CommonCorePublic

public final class NinePatchImageView: UIView, RemoteImageViewContentProtocol {
  private var image: UIImage?

  public var capInsets: UIEdgeInsets = .zero {
    didSet {
      updateLayout()
    }
  }

  public var appearanceAnimation: ImageViewAnimation?
  public var imageRedrawingStyle: ImageRedrawingStyle?
  public var imageContentMode = ImageContentMode.default

  init() {
    super.init(frame: .zero)
    isOpaque = false
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func draw(_ rect: CGRect) {
    super.draw(rect)
    image?.draw(in: rect)
  }

  public func setImage(_ image: UIImage?, animated _: Bool?) {
    self.image = image
    updateLayout()
  }

  private func updateLayout() {
    let scale = image?.scale
    self.image = scale.flatMap {
      image?.resizableImage(
        withCapInsets: UIEdgeInsets(
          top: capInsets.top / $0,
          left: capInsets.left / $0,
          bottom: capInsets.bottom / $0,
          right: capInsets.right / $0
        ),
        resizingMode: .stretch
      )
    }
    setNeedsDisplay()
  }
}
