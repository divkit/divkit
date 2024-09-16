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

final class DetachableAnimationBlockView: BlockView {
  private var childView: BlockView? {
    didSet {
      guard childView !== oldValue else { return }

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
      }
    }
  }

  private var animatedView: BlockView?

  private var animationIn: [TransitioningAnimation]?
  private var animationOut: [TransitioningAnimation]?
  private var animationChange: ChangeBoundsTransition?
  private var queuedAnimation: DispatchWorkItem?
  private var child: Block?

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

    childView?.frame = bounds
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

    let blockSize = CGSize(
      width: startFrame.width,
      height: child?.intrinsicContentHeight(forWidth: startFrame.width) ?? .zero
    )
    childView.frame.size = blockSize
    childView.layoutIfNeeded()

    let animationContainer = UIView()
    animationContainer.frame = startFrame
    animationContainer.clipsToBounds = true
    animationContainer.addSubview(childView)

    container.addSubview(animationContainer)
    container.layoutIfNeeded()

    UIView.animate(
      withDuration: animationChange.duration.value,
      delay: animationChange.delay.value,
      options: [animationChange.timingFunction.cast()],
      animations: {
        animationContainer.frame = finishFrame
        childView.frame.size = CGSize(
          width: finishFrame.width,
          height: self.child?.intrinsicContentHeight(forWidth: finishFrame.width) ?? .zero
        )
        childView.layoutIfNeeded()
        container.layoutIfNeeded()
      },
      completion: { [weak self] _ in
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

  public func addWithAnimation(in container: UIView) {
    guard let childView,
          let animationIn else {
      return
    }

    let originalFrame = childView.frame
    childView.frame = convertFrame(to: container)

    self.childView = nil

    let minDelay = animationIn.sortedChronologically().first?.delay.value ?? 0

    let item = DispatchWorkItem { [weak self] in
      container.addSubview(childView)
      childView.setInitialParamsAndAnimate(
        animations: animationIn.withDelay(-minDelay),
        completion: { [weak self] in

          self?.queuedAnimation = nil
          if childView.superview == container {
            childView.frame = originalFrame
            self?.childView = childView
          }
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
