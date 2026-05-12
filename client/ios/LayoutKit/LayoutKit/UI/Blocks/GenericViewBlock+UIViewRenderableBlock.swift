#if os(iOS)
import UIKit
import VGSL

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
    let genericView = view as! GenericView
    genericView.content = content.value
    genericView.exclusiveTouchEnabled = isExclusiveTouch
  }
}

private final class GenericView: UIView, BlockViewProtocol, VisibleBoundsTrackingLeaf {
  var content: GenericViewBlock.Content? {
    didSet {
      guard oldValue !== content || content?.isChild(of: self) != true else {
        return
      }

      oldValue?.remove(from: self)
      content?.add(to: self)
      setNeedsLayout()
    }
  }

  var exclusiveTouchEnabled = false {
    didSet {
      guard oldValue != exclusiveTouchEnabled else { return }
      tapAbsorber.isEnabled = exclusiveTouchEnabled
    }
  }

  private let tapAbsorber = UITapGestureRecognizer()

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)
    tapAbsorber.addTarget(self, action: #selector(absorbTap))
    tapAbsorber.delegate = self
    tapAbsorber.cancelsTouchesInView = false
    tapAbsorber.isEnabled = false
    addGestureRecognizer(tapAbsorber)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

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

  @objc private func absorbTap() {}
}

extension GenericView: UIGestureRecognizerDelegate {
  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    guard gestureRecognizer === tapAbsorber else { return false }
    guard let otherTap = otherGestureRecognizer as? UITapGestureRecognizer,
          otherTap.numberOfTapsRequired == 1
    else { return false }
    guard let otherView = otherTap.view,
          otherView !== self,
          isDescendant(of: otherView)
    else { return false }
    return true
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

  fileprivate func isChild(of parent: UIView) -> Bool {
    switch self {
    case let .view(view):
      view.superview === parent
    case let .layer(layer):
      layer.superlayer === parent.layer
    }
  }
}
#endif
