import UIKit

import CommonCorePublic

extension GenericViewBlock {
  public static func makeBlockView() -> BlockView {
    GenericView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is GenericView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    (view as! GenericView).content = content.value
  }
}

private final class GenericView: UIStackView, BlockViewProtocol, VisibleBoundsTrackingLeaf {
  var content: GenericViewBlock.Content? {
    didSet {
      guard oldValue !== content else {
        return
      }

      oldValue?.remove(from: self)
      content?.add(to: self)
      setNeedsLayout()
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override func layoutSubviews() {
    super.layoutSubviews()
    content?.setFrame(bounds)
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    if result === self {
      return nil
    } else {
      return result
    }
  }
}

extension GenericViewBlock.Content {
  fileprivate func remove(from: UIView) {
    switch self {
    case let .view(view):
      if view.superview === from {
        view.removeFromSuperview()
      }
    case let .layer(layer):
      if layer.superlayer === from.layer {
        layer.removeFromSuperlayer()
      }
    }
  }

  fileprivate func setFrame(_ frame: CGRect) {
    switch self {
    case let .view(view): view.frame = frame
    case let .layer(layer): layer.frame = frame
    }
  }

  fileprivate func add(to view: UIView) {
    switch self {
    case let .view(content): view.addSubview(content)
    case let .layer(content): view.layer.addSublayer(content)
    }
  }
}
