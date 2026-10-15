#if os(iOS)
import UIKit
import VGSL

extension PinchToZoomBlock {
  public static func makeBlockView() -> BlockView {
    PinchToZoomView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is PinchToZoomView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! PinchToZoomView).configure(
      child: child,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      overlayView: overlayView,
      zoomObserver: zoomObserver
    )
  }
}

private final class PinchToZoomView: BlockView {
  private enum ZoomState {
    case idle
    case zooming
    case returning(UIViewPropertyAnimator)
    case interrupted
  }

  private var childView: BlockView!
  private weak var overlayView: UIView?
  private weak var zoomObserver: PinchToZoomObserver?
  private let shadeView = UIView()

  private var zoomState = ZoomState.idle
  private var configurationGeneration = 0

  private var scale: CGFloat = 1
  private var translation = CGPoint.zero
  private var translationToIgnore = CGPoint.zero
  private var originTranslation = CGPoint.zero

  var effectiveBackgroundColor: UIColor? { childView.backgroundColor }

  private var isZooming: Bool {
    childView.superview == shadeView
  }

  init() {
    super.init(frame: .zero)

    let pinchRecognizer = UIPinchGestureRecognizer(
      target: self,
      action: #selector(handlePinch(_:))
    )
    pinchRecognizer.delegate = self
    addGestureRecognizer(pinchRecognizer)

    let panRecognizer = UIPanGestureRecognizer(
      target: self,
      action: #selector(handlePan(_:))
    )
    panRecognizer.minimumNumberOfTouches = 2
    panRecognizer.maximumNumberOfTouches = 2
    panRecognizer.delegate = self
    addGestureRecognizer(panRecognizer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    if overlayView == nil {
      finishZoomImmediately()
    }

    let currentTransform = childView.transform
    childView.transform = .identity
    if isZooming, let overlayView {
      childView.frame = convert(bounds, to: overlayView)
      shadeView.frame = overlayView.frame
    } else {
      childView.frame = bounds
      shadeView.frame = .zero
    }
    childView.transform = currentTransform
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      configurationGeneration += 1
      finishZoomImmediately()
    }
  }

  override func didMoveToSuperview() {
    super.didMoveToSuperview()
    if superview == nil {
      configurationGeneration += 1
      finishZoomImmediately()
    }
  }

  func configure(
    child: Block,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    overlayView: UIView?,
    zoomObserver: PinchToZoomObserver?
  ) {
    configurationGeneration += 1
    let generation = configurationGeneration
    finishZoomImmediately()
    guard configurationGeneration == generation else {
      return
    }
    let contentView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    guard configurationGeneration == generation else {
      if contentView !== childView {
        contentView.removeFromSuperview()
      }
      return
    }
    childView = contentView
    self.overlayView = overlayView
    self.zoomObserver = zoomObserver
    setNeedsLayout()
  }

  private func updateTransform() {
    let scaleDelta = scale - 1
    childView.transform = CGAffineTransform(scaleX: scale, y: scale)
      .translatedBy(
        x: (translation.x - translationToIgnore.x + originTranslation.x * scaleDelta) / scale,
        y: (translation.y - translationToIgnore.y + originTranslation.y * scaleDelta) / scale
      )
    updateShadeColor()
  }

  private func resetTransform() {
    guard case .zooming = zoomState else {
      return
    }

    let contentView = childView
    scale = 1
    translation = .zero
    translationToIgnore = .zero
    originTranslation = .zero
    let animator = UIViewPropertyAnimator(duration: 0.25, curve: .easeInOut)
    animator.addAnimations { [weak self, weak contentView] in
      contentView?.transform = .identity
      self?.updateShadeColor()
    }
    animator.addCompletion { [weak self, weak contentView, weak shadeView, weak animator] _ in
      guard let self else {
        contentView?.removeFromSuperview()
        shadeView?.removeFromSuperview()
        return
      }
      guard case let .returning(currentAnimator) = self.zoomState,
            currentAnimator === animator else {
        return
      }
      self.finishZoomImmediately()
    }
    zoomState = .returning(animator)
    animator.startAnimation()
  }

  private func finishZoomImmediately() {
    let animator: UIViewPropertyAnimator?
    switch zoomState {
    case .idle, .interrupted:
      return
    case .zooming:
      zoomState = .interrupted
      animator = nil
    case let .returning(returnAnimator):
      zoomState = .idle
      animator = returnAnimator
    }

    scale = 1
    translation = .zero
    translationToIgnore = .zero
    originTranslation = .zero
    if animator?.state == .active {
      animator?.stopAnimation(true)
    }
    childView.transform = .identity
    addSubview(childView)
    childView.frame = bounds
    shadeView.removeFromSuperview()
    zoomObserver?.pinchToZoomDidEnd(in: childView)
  }

  private func updateShadeColor() {
    let alpha = 1.2 * (min(scale, 1.5) - 1)
    shadeView.backgroundColor = UIColor(white: 0, alpha: alpha)
  }

  @objc private func handlePinch(_ recognizer: UIPinchGestureRecognizer) {
    guard let overlayView else {
      finishZoomImmediately()
      return
    }

    if recognizer.state == .began {
      let generation = configurationGeneration
      finishZoomImmediately()
      guard configurationGeneration == generation else {
        zoomState = .interrupted
        return
      }
      if case .interrupted = zoomState {
        translationToIgnore = .zero
      }
      zoomState = .zooming
      translation = translationToIgnore
      let origin = recognizer.location(in: self)
      originTranslation = CGPoint(
        x: bounds.width / 2 - origin.x,
        y: bounds.height / 2 - origin.y
      )
      overlayView.addSubview(shadeView)
      shadeView.addSubview(childView)
    }

    switch recognizer.state {
    case .began, .changed:
      guard case .zooming = zoomState else {
        return
      }
      let previousScale = scale
      scale = clamp(recognizer.scale, min: minScale, max: maxScale)
      recognizer.scale = scale
      updateTransform()
      if recognizer.state == .began {
        zoomObserver?.pinchToZoomDidBegin(in: childView, scale: scale)
      } else if scale != previousScale {
        zoomObserver?.pinchToZoomDidChange(in: childView, scale: scale)
      }
    case .ended, .cancelled, .failed:
      if case .interrupted = zoomState {
        zoomState = .idle
        translationToIgnore = .zero
      } else {
        resetTransform()
      }
    default:
      break
    }
  }

  @objc private func handlePan(_ recognizer: UIPanGestureRecognizer) {
    guard overlayView != nil else {
      finishZoomImmediately()
      return
    }

    switch recognizer.state {
    case .began, .changed:
      switch zoomState {
      case .zooming:
        translation = recognizer.translation(in: self)
        updateTransform()
      case .idle:
        translationToIgnore = recognizer.translation(in: self)
      case .returning, .interrupted:
        break
      }
    case .ended, .cancelled, .failed:
      if case .idle = zoomState {
        translationToIgnore = .zero
      }
    default:
      break
    }
  }
}

extension PinchToZoomView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView]
  }
}

extension PinchToZoomView: UIGestureRecognizerDelegate {
  func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    gestureRecognizer.view == self && otherGestureRecognizer.view == self
  }
}

private let minScale: CGFloat = 1
private let maxScale: CGFloat = 4
#endif
