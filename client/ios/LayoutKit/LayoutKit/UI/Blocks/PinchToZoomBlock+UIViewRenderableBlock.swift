import UIKit

import CommonCorePublic

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
      overlayView: overlayView
    )
  }
}

private final class PinchToZoomView: BlockView {
  private var childView: BlockView!
  private var overlayView: UIView!
  private let shadeView = UIView()

  private var scale: CGFloat = 1
  private var translation = CGPoint.zero
  private var translationToIgnore = CGPoint.zero
  private var originTranslation = CGPoint.zero

  private var isZooming: Bool {
    childView.superview == shadeView
  }

  var effectiveBackgroundColor: UIColor? { childView.backgroundColor }

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

  func configure(
    child: Block,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    overlayView: UIView
  ) {
    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    self.overlayView = overlayView
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let currentTransform = childView.transform
    childView.transform = .identity
    if isZooming {
      childView.frame = convert(bounds, to: overlayView)
      shadeView.frame = overlayView.frame
    } else {
      childView.frame = bounds
      shadeView.frame = .zero
    }
    childView.transform = currentTransform
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
    scale = 1
    translation = .zero
    translationToIgnore = .zero
    originTranslation = .zero
    UIView.animate(
      duration: 0.25,
      animations: { [unowned self] in
        self.childView.transform = .identity
        self.updateShadeColor()
      },
      completion: { [weak self, weak childView = self.childView] _ in
        if let view = self {
          view.addSubview(view.childView)
          view.shadeView.removeFromSuperview()
        } else {
          childView?.removeFromSuperview()
        }
      }
    )
  }

  private func updateShadeColor() {
    let alpha = 1.2 * (min(scale, 1.5) - 1)
    shadeView.backgroundColor = UIColor(white: 0, alpha: alpha)
  }

  @objc private func handlePinch(_ recognizer: UIPinchGestureRecognizer) {
    if recognizer.state == .began {
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
      scale = clamp(recognizer.scale, min: minScale, max: maxScale)
      recognizer.scale = scale
      updateTransform()
    case .ended, .cancelled:
      resetTransform()
    default:
      break
    }
  }

  @objc private func handlePan(_ recognizer: UIPanGestureRecognizer) {
    switch recognizer.state {
    case .began, .changed:
      // ignore pan gesture if not zooming yet
      if isZooming {
        translation = recognizer.translation(in: self)
        updateTransform()
      } else {
        translationToIgnore = recognizer.translation(in: self)
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
