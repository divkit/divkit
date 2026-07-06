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

  var transitionChangeAnimationContainer: UIView? {
    didSet {
      oldValue?.removeFromSuperview()
    }
  }

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
  private var isAnimatingIn: Bool = false

  var hasAnimationIn: Bool {
    animationIn != nil
  }

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  deinit {
    // The flight container lives in a structural ancestor, not in this view, so
    // StateBlockView's leftover-animation sweep can't reach it. If this view is
    // discarded mid-flight (e.g. recreated on a fast state switch), remove the
    // orphaned container here so it doesn't linger in the hierarchy.
    transitionChangeAnimationContainer?.removeFromSuperview()
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
          let animationChange,
          let (anchorView, animationParent) = resolveAnimationHost() else {
      return
    }

    let finishFrame = convertFrame(to: container)

    guard finishFrame != startFrame else { return }

    self.childView = nil

    let startFrameInParent = CGRect(
      origin: animationParent.convert(startFrame.origin, from: container),
      size: startFrame.size
    )
    let finishFrameInParent = CGRect(
      origin: animationParent.convert(finishFrame.origin, from: container),
      size: finishFrame.size
    )

    let transitionChangeAnimationContainer = UIView()
    self.transitionChangeAnimationContainer = transitionChangeAnimationContainer

    transitionChangeAnimationContainer.frame = startFrameInParent
    transitionChangeAnimationContainer.clipsToBounds = false
    transitionChangeAnimationContainer.addSubview(childView)
    // Seed the content with the source size. A layout pass before the animation
    // may already have sized childView to the destination, which would make the
    // in-animation size assignment a no-op (position would animate, size would
    // snap). Starting from the source size gives its bounds a real delta to
    // interpolate.
    childView.frame = CGRect(origin: .zero, size: transitionChangeAnimationContainer.bounds.size)
    self.isHidden = true

    // Host the flight in the real structural parent at the element's own
    // z-position, so decorations (action, border, ...) don't clip it while its
    // sibling z-order is preserved.
    animationParent.insertSubview(transitionChangeAnimationContainer, aboveSubview: anchorView)
    animationParent.layoutIfNeeded()

    UIView.animate(
      withDuration: animationChange.duration,
      delay: animationChange.delay,
      options: [animationChange.timingFunction.cast()],
      animations: {
        transitionChangeAnimationContainer.frame = finishFrameInParent
        childView.frame.size = transitionChangeAnimationContainer.bounds.size
        childView.layoutIfNeeded()
        animationParent.layoutIfNeeded()
      },
      completion: { [weak self] _ in
        self?.isHidden = false
        self?.animationChange = nil
        if transitionChangeAnimationContainer.superview == animationParent {
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

    // Don't restart an appearance animation that is already running. A block can be reconfigured
    // several times in quick succession (e.g. variable-driven rebuilds during initial load); without
    // this guard the animation gets re-triggered mid-flight, producing a half-appear/hide/appear jank.
    guard !isAnimatingIn else { return }

    // Only start once the view is laid out, so the transition is actually visible. addWithAnimation
    // can be called before layout settles; defer to a later call instead of consuming the animation
    // invisibly.
    guard bounds.width > 0, bounds.height > 0 else { return }

    isAnimatingIn = true
    childView.isHidden = true
    let minDelay = animationIn.sortedChronologically().first?.delay ?? 0

    let item = DispatchWorkItem { [weak self] in
      childView.isHidden = false
      childView.setInitialParamsAndAnimate(
        animations: animationIn.withDelay(-minDelay),
        completion: { [weak self] in
          self?.queuedAnimation = nil
          self?.animationIn = nil
          self?.isAnimatingIn = false
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
    // Preserve a pending appearance animation across reconfigurations. During the first on-screen
    // load the block can be reconfigured several times in quick succession; a reconfigure with
    // `animationIn == nil` would otherwise overwrite the freshly-attached appearance animation before
    // it plays, swallowing the first-appearance transition. The play completion clears `animationIn`,
    // so the animation still runs exactly once.
    if animationIn != nil {
      self.animationIn = animationIn
    }
    self.animationOut = animationOut
    self.animationChange = animationChange

    setNeedsLayout()
  }

  func cancelAnimations() {
    queuedAnimation?.cancel()
    queuedAnimation = nil
    transitionChangeAnimationContainer = nil
    isAnimatingIn = false
  }

  private func resolveAnimationHost() -> (anchor: UIView, parent: UIView)? {
    var anchor: UIView = self
    while let superview = anchor.superview, superview is DecoratingViewProtocol {
      anchor = superview
    }
    guard let parent = anchor.superview else { return nil }
    return (anchor, parent)
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
