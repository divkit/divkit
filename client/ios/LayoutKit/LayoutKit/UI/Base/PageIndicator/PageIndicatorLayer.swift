import UIKit

import CommonCore

final class ScrollPageIndicatorLayer: CALayer {
  @NSManaged var currentIndexPosition: CGFloat
  var numberOfPages = 0
  var configuration = PageIndicatorConfiguration(
    highlightedColor: .clear,
    normalColor: .clear,
    highlightingScale: 0,
    disappearingScale: 0,
    pageSize: .zero,
    pageCornerRadius: 0,
    animation: .scale,
    itemPlacement: .fixed(spaceBetweenCenters: 0)
  ) {
    didSet {
      _cachedParams = nil
    }
  }

  private var _cachedParams: PageIndicatorLayerParams?
  private var params: PageIndicatorLayerParams {
    if let value = _cachedParams { return value }
    let result = PageIndicatorLayerParams(
      numberOfPages: numberOfPages,
      itemPlacement: configuration.itemPlacement,
      position: currentIndexPosition,
      boundsSize: bounds.size
    )
    _cachedParams = result
    return result
  }

  override init() {
    super.init()
  }

  override init(layer: Any) {
    super.init(layer: layer)

    guard let pageIndicatorLayer = layer as? ScrollPageIndicatorLayer else {
      assertionFailure("unknown layer inside init(layer: Any)")
      return
    }

    numberOfPages = pageIndicatorLayer.numberOfPages
    currentIndexPosition = pageIndicatorLayer.currentIndexPosition
    configuration = pageIndicatorLayer.configuration
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func draw(in ctx: CGContext) {
    ctx.clip(to: params.visibleRect)

    let animator = IndicatorStateAnimator(
      configuration: configuration,
      boundsWidth: bounds.size.width,
      numberOfPages: numberOfPages
    )

    for index in params.renderRange {
      let state = IndicatorState(
        index: index,
        currentPosition: currentIndexPosition,
        params: params,
        numberOfPages: numberOfPages
      )
      makeInactiveIndicator(for: state, animator: animator).render(in: ctx)
    }
    makeActiveIndicator(animator: animator)?.render(in: ctx)
  }

  override func action(forKey key: String) -> CAAction? {
    guard Self.isAnimationKeySupported(key) else {
      return super.action(forKey: key)
    }

    guard let presentation = presentation() else {
      return super.action(forKey: key)
    }

    let animation = CABasicAnimation(keyPath: key)
    animation.fromValue = presentation.value(forKey: key)
    return animation
  }

  override class func needsDisplay(forKey key: String) -> Bool {
    guard isAnimationKeySupported(key) else {
      return super.needsDisplay(forKey: key)
    }

    return true
  }

  private class func isAnimationKeySupported(_ key: String) -> Bool {
    key == #keyPath(currentIndexPosition)
  }
}

extension ScrollPageIndicatorLayer {
  fileprivate func makeInactiveIndicator(
    for state: IndicatorState,
    animator: IndicatorStateAnimator
  ) -> Indicator {
    let rect = indicatorRect(at: state.index)
    let scale = indicatorScale(for: state, animator: animator)

    return Indicator(
      rect: rect.withScaledSize(scale),
      cornerRadius: configuration.pageCornerRadius * scale,
      color: animator.indicatorColor(for: state).cgColor
    )
  }

  fileprivate func makeActiveIndicator(animator: IndicatorStateAnimator) -> Indicator? {
    let roundedIndex = currentIndexPosition.rounded(.down)
    let progress = currentIndexPosition - roundedIndex

    guard let offsets = animator.activeIndicatorOffsets(progress: progress) else {
      return nil
    }

    var rect = indicatorRect(at: Int(roundedIndex))
    rect.size.width += offsets.widthOffset
    rect.center.x += offsets.xOffset

    return Indicator(
      rect: rect,
      cornerRadius: configuration.pageCornerRadius,
      color: configuration.highlightedColor.cgColor
    )
  }

  fileprivate func indicatorRect(at index: Int) -> CGRect {
    let xPosition = CGFloat(index - params.head) + 0.5
    let x = xPosition * params.itemWidth + params.offsetX
    let y = params.visibleRect.center.y
    let center = CGPoint(x: x, y: y)
    let width: CGFloat
    if case let .stretch(spacing, _) = configuration.itemPlacement {
      width = params.itemWidth - spacing
    } else {
      width = configuration.pageSize.width
    }
    return CGRect(center: center, size: CGSize(width: width, height: configuration.pageSize.height))
  }

  fileprivate func indicatorScale(
    for state: IndicatorState,
    animator: IndicatorStateAnimator
  ) -> CGFloat {
    switch state.kind {
    case .normal:
      return configuration.disappearingScale.interpolated(to: 1, progress: state.progress)
    case .highlighted:
      return animator.highlightedIndicatorScale(for: state)
    }
  }
}

private struct Indicator {
  let rect: CGRect
  let cornerRadius: CGFloat
  let color: CGColor

  func render(in ctx: CGContext) {
    let path = CGPath(
      roundedRect: rect,
      cornerWidth: min(cornerRadius, rect.width / 2),
      cornerHeight: min(cornerRadius, rect.height / 2),
      transform: nil
    )
    ctx.addPath(path)
    ctx.setFillColor(color)
    ctx.fillPath()
  }
}
