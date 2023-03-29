import UIKit

import BasePublic
import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

#if INTERNAL_BUILD
extension AccessibilityBlock {
  public static func makeBlockView() -> BlockView { AccessibilityBlockView() }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    guard let view = view as? AccessibilityBlockView else { return false }
    return view.childView.map(child.canConfigureBlockView) ?? false
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! AccessibilityBlockView

    let model = AccessibilityBlockView.Model(
      child: child,
      accessibilityID: accessibilityID,
      traits: traits,
      source: weakify(self)
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class AccessibilityBlockView: UIView, BlockViewProtocol,
  VisibleBoundsTrackingContainer {
  struct Model: ReferenceEquatable {
    let child: UIViewRenderable & AnyObject
    let accessibilityID: String
    let traits: AccessibilityElement.Traits
    let source: Variable<AnyObject?>
  }

  private var model: Model!
  private weak var observer: ElementStateObserver?
  private(set) var childView: BlockView?

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { childView.asArray() }
  var effectiveBackgroundColor: UIColor? { childView?.backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard model != self.model || observer !== self.observer else {
      return
    }

    let shouldUpdateChildView = model.child !== self.model?.child || self.observer !== observer
    self.observer = observer

    self.model = model
    self.observer = observer

    accessibilityIdentifier = model.accessibilityID
    accessibilityTraits = model.traits.uiTraits

    if shouldUpdateChildView {
      childView = model.child.reuse(
        childView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate,
        superview: self
      )
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    childView?.frame = bounds
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }

  func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    passVisibleBoundsChanged(from: from, to: to)
  }
}
#endif
