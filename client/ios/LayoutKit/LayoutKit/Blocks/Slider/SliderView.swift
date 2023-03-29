import UIKit

import CommonCorePublic

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

    let newCount = sliderModel.maxValue - sliderModel.minValue + 1

    if marks.count > newCount {
      marks.suffix(from: newCount + 1).forEach {
        $0.active?.removeFromSuperview()
        $0.inactive?.removeFromSuperview()
      }
    } else {
      marks.append(contentsOf: Array(
        repeating: (active: nil, inactive: nil),
        times: UInt(max(0, newCount - marks.count))
      ))
    }

    let configureMark: (BlockView?, SliderModel.MarkModel?) -> BlockView? = {
      let blockView = $1?.block.reuse(
        $0,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate,
        superview: self
      )
      blockView?.frame.size = CGSize(
        width: $1?.block.intrinsicContentWidth ?? 0,
        height: $1?.block.intrinsicContentHeight(forWidth: .infinity) ?? 0
      )
      blockView?.isHidden = true
      return blockView
    }
    self.marks = marks.map {
      (
        configureMark($0.active, sliderModel.activeMarkModel),
        configureMark($0.inactive, sliderModel.inactiveMarkModel)
      )
    }

    inactiveTrack = sliderModel.inactiveTrack.reuse(
      inactiveTrack,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: sliderBackgroundView
    )

    activeTrack = sliderModel.activeTrack.reuse(
      activeTrack,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: sliderBackgroundView
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

    let clampedFirstThumbValue = Int(clampSliderValue(
      CGFloat(sliderModel.firstThumb.value),
      sliderModel: sliderModel
    ))
    let clampedSecondThumbValue =
      Int(clampSliderValue(
        CGFloat(sliderModel.secondThumb?.value ?? sliderModel.minValue),
        sliderModel: sliderModel
      ))

    self.sliderModel.firstThumb.$value.setValue(
      clampedFirstThumbValue,
      responder: nil
    )
    self.sliderModel.secondThumb?.$value.setValue(
      clampedSecondThumbValue,
      responder: nil
    )

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
      sliderModel.firstThumb.$value.setValue(thumbValue, responder: self)
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
      sliderModel.secondThumb?.$value.setValue(thumbValue, responder: self)
      setNeedsLayout()
    }
    secondThumbProgress = value
  }

  var effectiveBackgroundColor: UIColor? {
    backgroundColor
  }

  private var pointWidth: CGFloat {
    let stripeCount = sliderModel.maxValue - sliderModel.minValue
    let width = bounds.width - sliderModel.horizontalInset - max(
      abs(sliderModel.firstThumb.offsetX),
      abs(sliderModel.secondThumb?.offsetX ?? 0)
    )
    if stripeCount == 0 {
      return width
    } else {
      return CGFloat(width) / CGFloat(stripeCount)
    }
  }

  private var firstThumb: BlockView?
  private var secondThumb: BlockView?
  private var activeTrack: BlockView?
  private var inactiveTrack: BlockView?
  private var marks: [(active: BlockView?, inactive: BlockView?)] = []
  private lazy var sliderBackgroundView: UIView = {
    let view = UIView()
    self.insertSubview(view, at: 0)
    return view
  }()

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

  @objc private func handleTap(_ recognizer: UILongPressGestureRecognizer) {
    let location = recognizer.location(in: self)

    let currentValue = clamp(
      (location.x - sliderModel.horizontalInset / 2) / pointWidth,
      min: 0.0,
      max: CGFloat(sliderModel.maxValue - sliderModel.minValue)
    ) + CGFloat(sliderModel.minValue)

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
      if let firstThumb = firstThumb,
         let secondThumb = secondThumb {
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
      layoutIfNeeded()
      animateActiveThumb(
        to: currentValue.rounded(.toNearestOrAwayFromZero),
        from: currentValue,
        completion: { updateProgress($0) }
      )
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

    defer {
      configureMarks(
        firstThumbValue: Int(clampSliderValue(
          CGFloat(sliderModel.firstThumb.value),
          sliderModel: sliderModel
        )),
        secondThumbValue: sliderModel.secondThumb.flatMap {
          Int(clampSliderValue(CGFloat($0.value), sliderModel: sliderModel))
        },
        minValue: sliderModel.minValue,
        maxValue: sliderModel.maxValue
      )
    }

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

    sliderBackgroundView.frame = CGRect(
      x: 0,
      y: 0,
      width: bounds
        .width - max(
          abs(sliderModel.firstThumb.offsetX),
          abs(sliderModel.secondThumb?.offsetX ?? 0)
        ),
      height: sliderModel.sliderHeight
    )
    sliderBackgroundView.center = bounds.center
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

    configureTracks(
      progressFirstThumb: firstThumbProgress,
      progressSecondThumb: sliderModel
        .secondThumb != nil ? secondThumbProgress : CGFloat(sliderModel.minValue)
    )
  }

  private func configureMarks(
    firstThumbValue: Int,
    secondThumbValue: Int?,
    minValue: Int,
    maxValue: Int
  ) {
    if let secondThumbValue {
      let leftThumb = min(firstThumbValue, secondThumbValue)
      let rightThumb = max(firstThumbValue, secondThumbValue)
      makeMarks(
        from: minValue,
        to: leftThumb - 1,
        style: .inactive
      )
      makeMarks(
        from: leftThumb,
        to: rightThumb,
        style: .active
      )
      makeMarks(
        from: rightThumb + 1,
        to: maxValue,
        style: .inactive
      )
    } else {
      makeMarks(
        from: minValue,
        to: firstThumbValue,
        style: .active
      )
      makeMarks(
        from: firstThumbValue + 1,
        to: maxValue,
        style: .inactive
      )
    }
  }

  private func makeMarks(from startIndex: Int, to endIndex: Int, style: MarkStyle) {
    guard startIndex <= endIndex else { return }
    let thumbInsetY = (sliderModel.sliderTopTextPadding - sliderModel.sliderBottomTextPadding) / 2
    for ind in (startIndex - sliderModel.minValue)...(endIndex - sliderModel.minValue) {
      let visibleMark: BlockView?
      let invisibleMark: BlockView?
      let visibleMarkModel: SliderModel.MarkModel?
      switch style {
      case .active:
        visibleMark = marks[ind].active
        invisibleMark = marks[ind].inactive
        visibleMarkModel = sliderModel.activeMarkModel
      case .inactive:
        visibleMark = marks[ind].inactive
        invisibleMark = marks[ind].active
        visibleMarkModel = sliderModel.inactiveMarkModel
      }
      visibleMark?.isHidden = false
      visibleMark?.frame.origin
        .x = CGFloat(ind) * pointWidth +
        (
          sliderModel.horizontalInset - (visibleMarkModel?.size.width ?? 0)
        ) / 2
      visibleMark?.frame.center.y = bounds.center
        .movingY(by: thumbInsetY).y
      invisibleMark?.isHidden = true
    }
  }

  private func configureTracks(
    progressFirstThumb: CGFloat,
    progressSecondThumb: CGFloat
  ) {
    let makeModifiedSliderViewBounds: (CGFloat) -> CGRect = { [self] trackHeight in
      sliderBackgroundView.bounds.insetBy(
        dx: sliderModel.horizontalInset / 2,
        dy: (
          sliderModel.sliderHeight - trackHeight
        ) / 2
      )
    }

    inactiveTrack?.frame = makeModifiedSliderViewBounds(
      sliderModel.inactiveTrack
        .intrinsicContentHeight(
          forWidth: .infinity
        )
    )

    activeTrack?.frame = modified(makeModifiedSliderViewBounds(
      sliderModel.activeTrack
        .intrinsicContentHeight(
          forWidth: .infinity
        )
    )) {
      $0.origin
        .x += (min(progressFirstThumb, progressSecondThumb) - CGFloat(sliderModel.minValue)) *
        pointWidth
      $0.size.width = abs(progressFirstThumb - progressSecondThumb) * pointWidth
    }
  }

  private func configureThumb(
    thumbView: BlockView?,
    progress: CGFloat,
    thumbModel: SliderModel.ThumbModel?
  ) {
    guard let thumbView = thumbView,
          let thumbModel = thumbModel else {
      return
    }

    thumbView.frame = CGRect(origin: CGPoint(
      x: (progress - CGFloat(sliderModel.minValue)) * pointWidth
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

  return (thumbPosition == .right ? newValueIntegerPart : newValueIntegerPart + 1)
}

private enum ThumbPosition {
  case left
  case right
}

private enum MarkStyle {
  case active
  case inactive
}

private let animationDuration = 0.3
