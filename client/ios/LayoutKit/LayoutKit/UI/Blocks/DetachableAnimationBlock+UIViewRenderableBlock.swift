#if os(iOS)
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
  var visibilityAction: Action? {
    didSet {
      if childView != nil, visibilityAction != nil {
        applyVisibilityAction()
      }
    }
  }

  var transitionChangeAnimationContainer: UIView?

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

  private var animatedView: BlockView?

  private var animationIn: [TransitioningAnimation]?
  private var animationOut: [TransitioningAnimation]?
  private var animationChange: ChangeBoundsTransition?
  private var queuedAnimation: DispatchWorkItem?
  private var child: Block?
  private var isFirstChildLayout: Bool = true

  var hasAnimationIn: Bool {
    animationIn != nil
  }

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

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

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }

  func convertFrame(to container: UIView) -> CGRect {
    convert(bounds, to: container)
  }

  func changeBoundsWithAnimation(
    in container: UIView,
    startFrame: CGRect
  ) {
    guard let childView,
          let animationChange else {
      return
    }

    let finishFrame = convertFrame(to: container)

    guard finishFrame != startFrame else { return }

    removeChildView()

    transitionChangeAnimationContainer = UIView()
    guard let transitionChangeAnimationContainer else { return }
    transitionChangeAnimationContainer.frame = startFrame
    transitionChangeAnimationContainer.clipsToBounds = false
    transitionChangeAnimationContainer.addSubview(childView)

    container.addSubview(transitionChangeAnimationContainer)
    container.layoutIfNeeded()

    UIView.animate(
      withDuration: animationChange.duration,
      delay: animationChange.delay,
      options: [animationChange.timingFunction.cast()],
      animations: {
        transitionChangeAnimationContainer.frame = finishFrame
        childView.frame.size = transitionChangeAnimationContainer.bounds.size
        childView.layoutIfNeeded()
        container.layoutIfNeeded()
      },
      completion: { [weak self] _ in
        self?.animationChange = nil
        if transitionChangeAnimationContainer.superview == container {
          transitionChangeAnimationContainer.removeFromSuperview()
          self?.childView = childView
          self?.transitionChangeAnimationContainer = nil
        }
      }
    )
  }

  func removeWithAnimation(in container: UIView) {
    guard let childView else {
      return
    }

    childView.frame = convertFrame(to: container)
    self.childView = nil
    container.addSubview(childView)
    childView.setInitialParamsAndAnimate(
      animations: animationOut?.map { animation in
        if animation.kind == .fade {
          return animation.modifying(start: alpha)
        }
        return animation
      },
      completion: {
        childView.removeFromSuperview()
      }
    )
  }

  func addWithAnimation() {
    guard let childView,
          let animationIn else {
      return
    }

    childView.isHidden = true
    let minDelay = animationIn.sortedChronologically().first?.delay ?? 0

    let item = DispatchWorkItem { [weak self] in
      childView.isHidden = false
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

  func cancelAnimations() {
    queuedAnimation?.cancel()
    queuedAnimation = nil
  }

  func removeChildView() {
    self.childView = nil
  }
}

extension DetachableAnimationBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    (childView ?? animatedView).asArray()
  }
}

extension TransitioningAnimation {
  fileprivate func modifying(start: Double?) -> TransitioningAnimation {
    TransitioningAnimation(
      kind: self.kind,
      start: start ?? self.start,
      end: self.end,
      duration: self.duration,
      delay: self.delay,
      timingFunction: self.timingFunction
    )
  }
}
#endif
