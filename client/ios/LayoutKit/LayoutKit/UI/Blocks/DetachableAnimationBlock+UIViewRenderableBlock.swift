import UIKit

import CommonCorePublic

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
      if let childView = childView {
        addSubview(childView)
        animatedView = nil
      } else {
        animatedView = oldValue
        oldValue?.removeFromSuperview()
      }
    }
  }

  private var animatedView: BlockView?

  private var animationIn: [TransitioningAnimation]?
  private var animationOut: [TransitioningAnimation]?
  private var animationChange: ChangeBoundsTransition?
  private var queuedAnimation: DispatchWorkItem?

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
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
    guard let childView = childView,
          let animationChange = animationChange else {
      return
    }

    let finishFrame = convertFrame(to: container)
    self.childView = nil
    childView.frame = startFrame
    container.addSubview(childView)

    UIView.animate(
      withDuration: animationChange.duration.value,
      delay: animationChange.delay.value,
      options: [animationChange.timingFunction.cast()],
      animations: {
        childView.center = finishFrame.center
        childView.transform = CGAffineTransform(
          scaleX: finishFrame.width / startFrame.width,
          y: finishFrame.height / startFrame.height
        )
      },
      completion: { [weak self] _ in
        guard childView.superview == container else {
          return
        }
        childView.transform = .identity
        self?.childView = childView
      }
    )
  }

  public func removeWithAnimation(in container: UIView) {
    guard let childView = childView else {
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
    guard let childView = childView,
          let animationIn = animationIn else {
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
          guard childView.superview == container else {
            return
          }
          childView.frame = originalFrame
          self?.childView = childView
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
