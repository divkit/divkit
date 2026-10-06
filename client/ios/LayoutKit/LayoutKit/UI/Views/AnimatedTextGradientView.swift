#if os(iOS)
import UIKit
import VGSL

final class AnimatedTextGradientView: UIView {
  private struct GradientPoints: Equatable {
    let start: CGPoint
    let end: CGPoint
  }

  override class var layerClass: AnyClass { CAGradientLayer.self }

  static let animationKey = "animatedTextGradient"

  var model: TextBlock.GradientModel {
    didSet {
      guard model != oldValue else {
        return
      }
      applyGradient()
      updateRunningState(force: true)
    }
  }

  var textInsets: EdgeInsets = .zero {
    didSet {
      guard textInsets != oldValue else {
        return
      }
      setNeedsLayout()
    }
  }

  var isWithinVisibleBounds = true {
    didSet {
      guard isWithinVisibleBounds != oldValue else {
        return
      }
      updateRunningState()
    }
  }

  private(set) var isAnimationRunning = false

  private var gradientLayer: CAGradientLayer { layer as! CAGradientLayer }
  private let maskLayer = CALayer()
  private let clock: () -> TimeInterval
  private let notificationCenter: NotificationCenter
  private var observers: [NSObjectProtocol] = []
  private var applicationIsActive = UIApplication.shared.applicationState != .background

  init(
    model: TextBlock.GradientModel,
    clock: @escaping () -> TimeInterval = CACurrentMediaTime,
    notificationCenter: NotificationCenter = .default
  ) {
    self.model = model
    self.clock = clock
    self.notificationCenter = notificationCenter
    super.init(frame: .zero)
    isUserInteractionEnabled = false
    layer.mask = maskLayer
    applyGradient()
    let center = notificationCenter
    observers = [
      center.addObserver(
        forName: UIApplication.didEnterBackgroundNotification,
        object: nil,
        queue: .main
      ) { [weak self] _ in
        self?.applicationIsActive = false
        self?.updateRunningState(force: true)
      },
      center.addObserver(
        forName: UIApplication.didBecomeActiveNotification,
        object: nil,
        queue: .main
      ) { [weak self] _ in
        self?.applicationIsActive = true
        self?.updateRunningState(force: true)
      },
    ]
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  deinit {
    observers.forEach(notificationCenter.removeObserver)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    updateRunningState()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    maskLayer.frame = bounds
    CATransaction.commit()
    updateRunningState(force: true)
  }

  func setGlyphMask(_ image: UIImage) {
    maskLayer.contents = image.cgImage
    maskLayer.contentsScale = image.scale
  }

  private func applyGradient() {
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    defer {
      CATransaction.commit()
    }

    switch model.gradient {
    case let .linear(gradient):
      gradientLayer.type = .axial
      gradientLayer.colors = [gradient.startColor.cgColor] + gradient.colors.map(\.cgColor)
        + [gradient.endColor.cgColor]
      gradientLayer.locations = ([0] + gradient.locations.map { ($0 + 1) / 3 } + [1])
        .map { NSNumber(value: Double($0)) }
    case let .radial(gradient):
      gradientLayer.type = .radial
      gradientLayer.colors = [gradient.centerColor.cgColor]
        + gradient.intermediatePoints.map(\.color.cgColor) + [gradient.outerColor.cgColor]
      gradientLayer.locations = ([0] + gradient.intermediatePoints.map(\.location) + [1])
        .map { NSNumber(value: Double($0)) }
    case .box:
      gradientLayer.colors = nil
      gradientLayer.locations = nil
    }
  }

  private func gradientPoints(at phase: CGFloat) -> GradientPoints {
    let area = bounds.inset(by: UIEdgeInsets(
      top: textInsets.top,
      left: textInsets.left,
      bottom: textInsets.bottom,
      right: textInsets.right
    ))
    guard bounds.width > 0, bounds.height > 0, area.width > 0, area.height > 0 else {
      return GradientPoints(start: .zero, end: .zero)
    }

    switch model.gradient {
    case let .linear(gradient):
      let points = AnimatedTextGradientGeometry.linearPoints(
        direction: gradient.direction,
        bounds: area,
        phase: phase
      )
      let span = CGPoint(x: points.1.x - points.0.x, y: points.1.y - points.0.y)
      return GradientPoints(
        start: unitPoint(CGPoint(x: points.0.x - span.x, y: points.0.y - span.y)),
        end: unitPoint(CGPoint(x: points.1.x + span.x, y: points.1.y + span.y))
      )
    case let .radial(gradient):
      let center = AnimatedTextGradientGeometry.radialCenter(gradient, bounds: area)
      let radius = max(
        AnimatedTextGradientGeometry.radialRadius(gradient, bounds: area, center: center),
        .ulpOfOne
      )
      let translatedCenter = CGPoint(
        x: center.x + area.width * (2 * phase - 1),
        y: center.y
      )
      return GradientPoints(
        start: unitPoint(translatedCenter),
        end: unitPoint(CGPoint(x: translatedCenter.x + radius, y: translatedCenter.y + radius))
      )
    case .box:
      return GradientPoints(start: .zero, end: .zero)
    }
  }

  private func unitPoint(_ point: CGPoint) -> CGPoint {
    CGPoint(x: point.x / bounds.width, y: point.y / bounds.height)
  }

  private func makeAnimation(duration: TimeInterval) -> CAAnimationGroup {
    let from = gradientPoints(at: 0)
    let to = gradientPoints(at: 1)
    let timingFunction = CAMediaTimingFunction(name: .linear)

    let startPointAnimation = CABasicAnimation(keyPath: #keyPath(CAGradientLayer.startPoint))
    startPointAnimation.fromValue = from.start
    startPointAnimation.toValue = to.start
    startPointAnimation.duration = duration
    startPointAnimation.timingFunction = timingFunction

    let endPointAnimation = CABasicAnimation(keyPath: #keyPath(CAGradientLayer.endPoint))
    endPointAnimation.fromValue = from.end
    endPointAnimation.toValue = to.end
    endPointAnimation.duration = duration
    endPointAnimation.timingFunction = timingFunction

    let animation = CAAnimationGroup()
    animation.animations = [startPointAnimation, endPointAnimation]
    animation.duration = duration
    animation.repeatCount = .infinity
    animation.timingFunction = timingFunction
    animation.isRemovedOnCompletion = false

    let now = clock()
    let cycleStart = now - now.truncatingRemainder(dividingBy: duration)
    animation.beginTime = gradientLayer.convertTime(cycleStart, from: nil)
    return animation
  }

  private func setRestingGradientPoints() {
    let restingPoints = gradientPoints(at: 0.5)
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    gradientLayer.startPoint = restingPoints.start
    gradientLayer.endPoint = restingPoints.end
    CATransaction.commit()
  }

  private func updateRunningState(force: Bool = false) {
    let shouldRun = window != nil
      && isWithinVisibleBounds
      && applicationIsActive
      && (model.animation?.duration ?? 0) > 0
      && bounds.width > 0
      && bounds.height > 0
    guard force || shouldRun != isAnimationRunning else {
      return
    }

    isAnimationRunning = shouldRun
    gradientLayer.removeAnimation(forKey: Self.animationKey)
    setRestingGradientPoints()

    guard shouldRun, let animation = model.animation else {
      return
    }
    let duration = Double(animation.duration) / 1000
    gradientLayer.add(
      makeAnimation(duration: duration),
      forKey: Self.animationKey
    )
  }
}

enum AnimatedTextGradientGeometry {
  static func linearPoints(
    direction: Gradient.Linear.Direction, bounds: CGRect, phase: CGFloat
  ) -> (CGPoint, CGPoint) {
    let vector: CGPoint
    switch direction {
    case let .angle(degrees):
      let radians = degrees * .pi / 180
      vector = CGPoint(x: cos(radians), y: -sin(radians))
    case let .relative(from, to):
      let dx = (to.x - from.x) * bounds.width
      let dy = (to.y - from.y) * bounds.height
      let length = hypot(dx, dy)
      vector = length > 0 ? CGPoint(x: dx / length, y: dy / length) : CGPoint(x: 1, y: 0)
    }
    let distance = abs(bounds.width * vector.x) + abs(bounds.height * vector.y)
    let translation = distance * (2 * phase - 1)
    return (
      CGPoint(
        x: bounds.midX + vector.x * (translation - distance / 2),
        y: bounds.midY + vector.y * (translation - distance / 2)
      ),
      CGPoint(
        x: bounds.midX + vector.x * (translation + distance / 2),
        y: bounds.midY + vector.y * (translation + distance / 2)
      )
    )
  }

  static func radialCenter(_ gradient: Gradient.Radial, bounds: CGRect) -> CGPoint {
    func coordinate(
      _ value: Gradient.Radial.CenterPoint,
      origin: CGFloat,
      size: CGFloat
    ) -> CGFloat {
      switch value {
      case let .relative(fraction): origin + size * fraction
      case let .absolute(offset): origin + CGFloat(offset)
      }
    }
    return CGPoint(
      x: coordinate(gradient.centerX, origin: bounds.minX, size: bounds.width),
      y: coordinate(gradient.centerY, origin: bounds.minY, size: bounds.height)
    )
  }

  static func radialRadius(
    _ gradient: Gradient.Radial,
    bounds: CGRect,
    center: CGPoint
  ) -> CGFloat {
    let nearX = min(abs(center.x - bounds.minX), abs(center.x - bounds.maxX))
    let nearY = min(abs(center.y - bounds.minY), abs(center.y - bounds.maxY))
    let farX = max(abs(center.x - bounds.minX), abs(center.x - bounds.maxX))
    let farY = max(abs(center.y - bounds.minY), abs(center.y - bounds.maxY))
    switch gradient.end {
    case let .absolute(radius): return CGFloat(radius)
    case let .relativeToSize(point):
      return hypot(
        bounds.minX + point.x * bounds.width - center.x,
        bounds.minY + point.y * bounds.height - center.y
      )
    case let .relativeToBorders(border):
      switch border {
      case .nearestSide: return min(nearX, nearY)
      case .farthestSide: return max(farX, farY)
      case .nearestCorner: return hypot(nearX, nearY)
      case .farthestCorner: return hypot(farX, farY)
      }
    }
  }
}
#endif
