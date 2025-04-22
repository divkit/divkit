import UIKit
import VGSL

extension DetachableAnimationBlock {
  public static func makeBlockView() -> BlockView {
    DetachableAnimationBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is DetachableAnimationBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    renderingDelegate?.mapView(view, to: BlockViewID(rawValue: id))

    (view as! DetachableAnimationBlockView).configure(
      child: child,
      animationIn: animationIn,
      animationOut: animationOut,
      animationChange: animationChange,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

final class DetachableAnimationBlockView: BlockView, DelayedVisibilityActionView {
  private var childView: BlockView? {
    didSet {
      guard childView !== oldValue else { return }

      isFirstChildLayout = true

      switch (oldValue, childView) {
      case (.none, .none):
        break
      case let (.some(oldValue), .some(childView)):
        addSubview(childView)
        animatedView = oldValue
        oldValue.removeFromSuperview()
      case let (.some(oldValue), .none):
        animatedView = oldValue
        oldValue.removeFromSuperview()
      case let (.none, .some(childView)):
        addSubview(childView)
        applyVisibilityAction()
      }
    }
  }

  var visibilityAction: Action? {
    didSet {
      if childView != nil, visibilityAction != nil {
        applyVisibilityAction()
      }
    }
  }

  private var animatedView: BlockView?

  private var animationIn: [TransitioningAnimation]?
  private var animationOut: [TransitioningAnimation]?
  private var animationChange: ChangeBoundsTransition?
  private var queuedAnimation: DispatchWorkItem?
  private var child: Block?
  private var isFirstChildLayout: Bool = true

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  func configure(
    child: Block,
    animationIn: [TransitioningAnimation]?,
    animationOut: [TransitioningAnimation]?,
    animationChange: ChangeBoundsTransition?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.child = child
    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    self.animationIn = animationIn
    self.animationOut = animationOut
    self.animationChange = animationChange

    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    guard isFirstChildLayout || animationChange == nil else {
      return
    }

    childView?.frame = bounds
    if frame != .zero {
      isFirstChildLayout = false
    }
  }

  public var hasAnimationIn: Bool {
    animationIn != nil
  }

  public func convertFrame(to container: UIView) -> CGRect {
    convert(bounds, to: container)
  }

  public func changeBoundsWithAnimation(
    in container: UIView,
    startFrame: CGRect
  ) {
    guard let childView,
          let animationChange else {
      return
    }

    let finishFrame = convertFrame(to: container)

    guard finishFrame != startFrame else { return }

    self.childView = nil

    let animationContainer = UIView()
    animationContainer.frame = startFrame
    animationContainer.clipsToBounds = false
    animationContainer.addSubview(childView)

    container.addSubview(animationContainer)
    container.layoutIfNeeded()

    UIView.animate(
      withDuration: animationChange.duration,
      delay: animationChange.delay,
      options: [animationChange.timingFunction.cast()],
      animations: {
        animationContainer.frame = finishFrame
        childView.frame.size = animationContainer.bounds.size
        childView.layoutIfNeeded()
        container.layoutIfNeeded()
      },
      completion: { [weak self] _ in
        self?.animationChange = nil
        if animationContainer.superview == container {
          animationContainer.removeFromSuperview()
          self?.childView = childView
        }
      }
    )
  }

  public func removeWithAnimation(in container: UIView) {
    guard let childView else {
      return
    }

    childView.frame = convertFrame(to: container)
    self.childView = nil
    container.addSubview(childView)
    childView.perform(animationOut, animated: true, completion: {
      childView.removeFromSuperview()
    })
  }

  public func addWithAnimation() {
    guard let childView,
          let animationIn else {
      return
    }

    let minDelay = animationIn.sortedChronologically().first?.delay ?? 0

    let item = DispatchWorkItem { [weak self] in
      childView.setInitialParamsAndAnimate(
        animations: animationIn.withDelay(-minDelay),
        completion: { [weak self] in
          self?.queuedAnimation = nil
          self?.animationIn = nil
        }
      )
    }

    if minDelay > 0 {
      queuedAnimation = item
      DispatchQueue.main.asyncAfter(
        deadline: .now() + minDelay,
        execute: item
      )
    } else {
      item.perform()
    }
  }

  func cancelAnimations() {
    queuedAnimation?.cancel()
    queuedAnimation = nil
  }
}

extension DetachableAnimationBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    (childView ?? animatedView).asArray()
  }
}
