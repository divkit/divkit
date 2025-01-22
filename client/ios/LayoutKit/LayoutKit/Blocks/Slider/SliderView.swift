import UIKit
import VGSL

final class SliderView: BlockView, VisibleBoundsTrackingLeaf {
  private var sliderModel: SliderModel = .empty
  private var thumbAnimator: UIViewPropertyAnimator?
  private var firstThumbProgress: CGFloat = .zero {
    didSet {
      setNeedsLayout()
    }
  }

  private var secondThumbProgress: CGFloat = .zero {
    didSet {
      setNeedsLayout()
    }
  }

  func setSliderModel(
    _ sliderModel: SliderModel,
    observer: ElementStateObserver? = nil,
    overscrollDelegate: ScrollDelegate? = nil,
    renderingDelegate: RenderingDelegate? = nil
  ) {
    guard self.sliderModel != sliderModel else {
      return
    }
    self.sliderModel = sliderModel

    inactiveRangeTracks = inactiveRangeTracks.reused(
      with: sliderModel.ranges.map(\.inactiveTrack),
      attachTo: inactiveTrackView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )

    activeRangeTracks = activeRangeTracks.reused(
      with: sliderModel.ranges.map(\.activeTrack),
      attachTo: activeTrackView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )

    firstThumb = sliderModel.firstThumb.block.reuse(
      firstThumb,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )

    secondThumb = sliderModel.secondThumb?.block.reuse(
      secondThumb,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )

    marksView.configuration = sliderModel.marksConfiguration

    let clampedFirstThumbValue = Int(clampSliderValue(
      CGFloat(sliderModel.firstThumb.value.value),
      sliderModel: sliderModel
    ))
    let clampedSecondThumbValue =
      Int(clampSliderValue(
        CGFloat(sliderModel.secondThumb?.value.value ?? sliderModel.minValue),
        sliderModel: sliderModel
      ))

    self.sliderModel.firstThumb.value.value = clampedFirstThumbValue
    self.sliderModel.secondThumb?.value.value = clampedSecondThumbValue

    if recognizer.state != .began, recognizer.state != .changed {
      firstThumbProgress = CGFloat(clampedFirstThumbValue)
      secondThumbProgress = CGFloat(clampedSecondThumbValue)
    }

    setNeedsLayout()
  }

  private func setFirstThumbProgress(_ value: CGFloat) {
    if let thumbValue = updatedThumbsValue(
      oldValue: firstThumbProgress,
      newValue: value,
      thumbPosition: value < secondThumbProgress ? .left : .right
    ) {
      sliderModel.firstThumb.value.value = thumbValue
      setNeedsLayout()
    }
    firstThumbProgress = value
  }

  private func setSecondThumbProgress(_ value: CGFloat) {
    if sliderModel.secondThumb != nil,
       let thumbValue = updatedThumbsValue(
         oldValue: secondThumbProgress,
         newValue: value,
         thumbPosition: value < firstThumbProgress ? .left : .right
       ) {
      sliderModel.secondThumb?.value.value = thumbValue
      setNeedsLayout()
    }
    secondThumbProgress = value
  }

  var effectiveBackgroundColor: UIColor? {
    backgroundColor
  }

  private var pointWidth: CGFloat {
    let width = bounds.width - sliderModel.horizontalInset - max(
      abs(sliderModel.firstThumb.offsetX),
      abs(sliderModel.secondThumb?.offsetX ?? 0)
    )
    if sliderModel.valueRange == 0 {
      return width
    } else {
      return CGFloat(width) / CGFloat(sliderModel.valueRange)
    }
  }

  private var firstThumb: BlockView?
  private var secondThumb: BlockView?
  private var activeRangeTracks: [BlockView] = []
  private var inactiveRangeTracks: [BlockView] = []
  private lazy var inactiveTrackView = makeAndInsertView(UIView(), at: 0)
  private lazy var activeTrackView = makeAndInsertView(UIView(), at: 1)
  private lazy var marksView = makeAndInsertView(MarksView(), at: 2)

  private let recognizer = UILongPressGestureRecognizer()
  private var activeThumb: SliderThumbNumber = .first

  init() {
    super.init(frame: .zero)
    recognizer.addTarget(self, action: #selector(handleTap))
    isExclusiveTouch = true
    recognizer.minimumPressDuration = 0
    addGestureRecognizer(recognizer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private func makeAndInsertView<T: UIView>(_ view: T, at position: Int) -> T {
    view.clipsToBounds = true
    self.insertSubview(view, at: position)
    return view
  }

  @objc private func handleTap(_ recognizer: UILongPressGestureRecognizer) {
    let location = recognizer.location(in: self)

    var currentValue: CGFloat = 0
    switch sliderModel.layoutDirection {
    case .leftToRight:
      currentValue = clamp(
        (location.x - sliderModel.horizontalInset / 2) / pointWidth,
        min: 0.0,
        max: CGFloat(sliderModel.valueRange)
      ) + CGFloat(sliderModel.minValue)
    case .rightToLeft:
      currentValue = CGFloat(sliderModel.maxValue) - clamp(
        (location.x - sliderModel.horizontalInset / 2) / pointWidth,
        min: 0.0,
        max: CGFloat(sliderModel.valueRange)
      )
    @unknown default:
      assertionFailure("Unknown layoutDirection (UserInterfaceLayoutDirection)")
    }

    let updateProgress: (CGFloat) -> Void = { [self] value in
      if activeThumb == .first {
        setFirstThumbProgress(value)
      } else {
        setSecondThumbProgress(value)
      }
    }
    switch recognizer.state {
    case .began:
      thumbAnimator?.stopAnimation(true)
      if let firstThumb,
         let secondThumb {
        if abs(firstThumb.frame.origin.x - location.x) <
          abs(secondThumb.frame.origin.x - location.x) {
          activeThumb = .first
        } else {
          activeThumb = .second
        }
      } else {
        activeThumb = .first
      }
      updateProgress(currentValue)
    case .changed:
      thumbAnimator?.stopAnimation(true)
      updateProgress(currentValue)
    case .cancelled, .ended, .failed, .possible:
      thumbAnimator?.stopAnimation(true)
      animateActiveThumb(
        to: currentValue.rounded(.toNearestOrAwayFromZero),
        from: currentValue,
        completion: { updateProgress($0) }
      )
      layoutIfNeeded()
    @unknown default: break
    }
  }

  private func animateActiveThumb(
    to newValue: Double,
    from oldValue: Double,
    completion: @escaping (Double) -> Void
  ) {
    thumbAnimator = .runningPropertyAnimator(
      withDuration: animationDuration * 2 * abs(newValue - oldValue),
      delay: 0,
      options: [.allowUserInteraction, .curveEaseIn],
      animations: { [self] in
        switch activeThumb {
        case .first:
          configureThumb(
            thumbView: firstThumb,
            progress: newValue,
            thumbModel: sliderModel.firstThumb
          )
          configureTracks(
            progressFirstThumb: newValue,
            progressSecondThumb: sliderModel.secondThumb != nil
              ? secondThumbProgress
              : CGFloat(sliderModel.minValue)
          )
        case .second:
          configureThumb(
            thumbView: secondThumb,
            progress: newValue,
            thumbModel: sliderModel.secondThumb
          )
          configureTracks(
            progressFirstThumb: firstThumbProgress,
            progressSecondThumb: newValue
          )
        }
      },
      completion: { position in
        switch position {
        case .end:
          completion(newValue)
        case .current, .start:
          break
        @unknown default:
          break
        }
      }
    )
  }

  override func layoutSubviews() {
    let firstThumbProgress = clampSliderValue(firstThumbProgress, sliderModel: sliderModel)
    let secondThumbProgress = clampSliderValue(secondThumbProgress, sliderModel: sliderModel)

    guard thumbAnimator?.state != .active else {
      return
    }

    super.layoutSubviews()

    configureThumb(
      thumbView: firstThumb,
      progress: firstThumbProgress,
      thumbModel: sliderModel.firstThumb
    )
    configureThumb(
      thumbView: secondThumb,
      progress: secondThumbProgress,
      thumbModel: sliderModel.secondThumb
    )

    marksView.configuration.horizontalInset = sliderModel.horizontalInset
    configureSliderView(marksView)

    configureSliderView(inactiveTrackView, with: sliderModel.horizontalInset)
    configureSliderView(activeTrackView, with: sliderModel.horizontalInset)

    configureRangeViews(
      inactiveRangeTracks,
      with: sliderModel.ranges,
      in: inactiveTrackView,
      isActive: false
    )
    configureRangeViews(
      activeRangeTracks,
      with: sliderModel.ranges,
      in: activeTrackView,
      isActive: true
    )

    configureTracks(
      progressFirstThumb: firstThumbProgress,
      progressSecondThumb: sliderModel
        .secondThumb != nil ? secondThumbProgress : CGFloat(sliderModel.minValue)
    )
  }

  private func configureRangeViews(
    _ rangeViews: [UIView],
    with models: [SliderModel.RangeModel],
    in parentView: UIView,
    isActive: Bool
  ) {
    for (view, model) in zip(rangeViews, models) {
      let rangeOfValues = CGFloat(model.end - model.start) / CGFloat(sliderModel.valueRange)
      let rangeWidth = parentView.frame
        .width * rangeOfValues - (model.margins.left + model.margins.right)
      let rangeHeight = isActive ? model.activeTrack
        .intrinsicContentHeight(forWidth: rangeWidth) : model.inactiveTrack
        .intrinsicContentHeight(forWidth: rangeWidth)
      view.frame = CGRect(
        x: CGFloat(model.start - sliderModel.minValue) / CGFloat(sliderModel.valueRange) *
          inactiveTrackView.frame.width + model.margins.left,
        y: (sliderModel.sliderHeight - rangeHeight) / 2,
        width: rangeWidth,
        height: rangeHeight
      )
    }
  }

  private func configureSliderView(_ view: UIView, with horizontalInset: CGFloat = 0) {
    view.frame = CGRect(
      x: horizontalInset / 2,
      y: 0,
      width: bounds
        .width - max(
          abs(sliderModel.firstThumb.offsetX),
          abs(sliderModel.secondThumb?.offsetX ?? 0)
        ) - horizontalInset,
      height: sliderModel.sliderHeight
    )
    view.center = bounds.center
      .movingX(
        by:
        (
          abs(sliderModel.firstThumb.offsetX) > abs(sliderModel.secondThumb?.offsetX ?? 0)
        )
          ? sliderModel.firstThumb.offsetX > 0 ?
          -(sliderModel.firstThumb.offsetX / 2)
          : sliderModel.firstThumb.offsetX / 2
          : sliderModel.firstThumb.offsetX > 0 ?
          -(sliderModel.secondThumb?.offsetX ?? 0) / 2
          : (sliderModel.secondThumb?.offsetX ?? 0) / 2
      )
  }

  private func configureTracks(
    progressFirstThumb: CGFloat,
    progressSecondThumb: CGFloat
  ) {
    let progressFirstThumbWidth =
      (min(progressFirstThumb, progressSecondThumb) - CGFloat(sliderModel.minValue)) * pointWidth
    let progressSecondThumbWidth =
      (max(progressFirstThumb, progressSecondThumb) - CGFloat(sliderModel.minValue)) * pointWidth
    let (originX, progressWidth) = {
      switch sliderModel.layoutDirection {
      case .leftToRight:
        return (progressFirstThumbWidth, progressSecondThumbWidth)
      case .rightToLeft:
        return (
          inactiveTrackView.frame.width - progressSecondThumbWidth,
          inactiveTrackView.frame.width - progressFirstThumbWidth
        )
      @unknown default:
        assertionFailure("Unknown layoutDirection (UserInterfaceLayoutDirection)")
        return (0, 0)
      }
    }()

    activeTrackView.cutView(originX: originX)

    activeTrackView.frame = CGRect(
      x: inactiveTrackView.frame.minX,
      y: inactiveTrackView.frame.minY,
      width: progressWidth,
      height: sliderModel.sliderHeight
    )

    marksView.firstThumbProgress = firstThumbProgress
    marksView.secondThumbProgress = secondThumbProgress
  }

  private func configureThumb(
    thumbView: BlockView?,
    progress: CGFloat,
    thumbModel: SliderModel.ThumbModel?
  ) {
    guard let thumbView,
          let thumbModel else {
      return
    }

    var thumbProgress: CGFloat = 0
    switch sliderModel.layoutDirection {
    case .leftToRight:
      thumbProgress = progress - CGFloat(sliderModel.minValue)
    case .rightToLeft:
      thumbProgress = CGFloat(sliderModel.maxValue) - progress
    @unknown default:
      assertionFailure("Unknown layoutDirection (UserInterfaceLayoutDirection)")
    }

    thumbView.frame = CGRect(origin: CGPoint(
      x: thumbProgress * pointWidth
        + (sliderModel.horizontalInset - thumbModel.size.width) / 2,
      y: 0
    ), size: CGSize(
      width: thumbModel.size.width,
      height: thumbModel.size.height
    ))
    thumbView.frame.center.y = bounds.center.y
  }

  private func clampSliderValue(_ value: CGFloat, sliderModel: SliderModel) -> CGFloat {
    clamp(value, min: CGFloat(sliderModel.minValue), max: CGFloat(sliderModel.maxValue))
  }
}

private func updatedThumbsValue(
  oldValue: CGFloat,
  newValue: CGFloat,
  thumbPosition: ThumbPosition
) -> Int? {
  guard oldValue != newValue else {
    return nil
  }
  let newValueIntegerPart = Int(newValue.rounded(.down))
  if newValue.isApproximatelyEqualTo(newValue.rounded(.toNearestOrAwayFromZero)) {
    return Int(newValue.rounded(.toNearestOrAwayFromZero))
  }

  return thumbPosition == .right ? newValueIntegerPart : newValueIntegerPart + 1
}

private enum ThumbPosition {
  case left
  case right
}

private let animationDuration = 0.3

extension UIView {
  fileprivate func cutView(originX: CGFloat) {
    let shapeLayer = CAShapeLayer()
    let path = CGMutablePath()
    let rect = CGRect(
      x: self.bounds.origin.x,
      y: self.bounds.origin.y,
      width: originX,
      height: self.bounds.height
    )
    path.addRect(rect)
    path.addRect(bounds)
    shapeLayer.path = path
    shapeLayer.fillRule = CAShapeLayerFillRule.evenOdd
    layer.mask = shapeLayer
  }
}
