#if os(iOS)
import LayoutKit
import UIKit
import VGSL

@available(iOS 26, *)
extension LiquidGlassBlock {
  public static func makeBlockView() -> BlockView { LiquidGlassView() }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is LiquidGlassView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! LiquidGlassView).configure(
      child: child,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      effectStyle: effectStyle,
      isInteractive: isInteractive,
      tintColor: tintColor,
      cornerStyle: cornerStyle,
      uiStyleUpdater: uiStyleUpdater
    )
  }
}

@available(iOS 26, *)
private final class LiquidGlassView: UIVisualEffectView, BlockViewProtocol,
  VisibleBoundsTrackingContainer {
  private struct EffectParams: Equatable {
    let style: LiquidGlassBlock.EffectStyle
    let isInteractive: Bool?
    let tintColor: Color?
  }

  private var childView: BlockView?
  private var currentEffectParams: EffectParams?
  private var uiStyleUpdater: LiquidGlassBlock.UIStyleUpdater?
  private var systemCornerConfiguration: UICornerConfiguration?

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    childView.asArray()
  }

  init() {
    super.init(effect: UIGlassEffect())
    systemCornerConfiguration = cornerConfiguration
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError("init(coder:) has not been implemented") }

  override func layoutSubviews() {
    super.layoutSubviews()
    childView?.frame = bounds
  }

  func configure(
    child: Block,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    effectStyle: LiquidGlassBlock.EffectStyle,
    isInteractive: Bool?,
    tintColor: Color?,
    cornerStyle: LiquidGlassBlock.CornerStyle?,
    uiStyleUpdater: LiquidGlassBlock.UIStyleUpdater?
  ) {
    self.uiStyleUpdater = uiStyleUpdater
    // The glass material picks its own interface style depending on what is rendered beneath it.
    // That style is not exposed by UIKit, the only way to observe it is to read the trait
    // collection UIKit resolves a dynamic color against.
    contentView.backgroundColor = uiStyleUpdater.map { _ in
      UIColor(dynamicProvider: { [weak self] traitCollection in
        self?.reportUIStyle(traitCollection.userInterfaceStyle)
        return .clear
      })
    }

    // Every input of the effect has to be compared, not just the style: a reused view keeps the
    // effect object, so a tint or interactivity change coming from an expression would be lost.
    let effectParams = EffectParams(
      style: effectStyle,
      isInteractive: isInteractive,
      tintColor: tintColor
    )
    if currentEffectParams != effectParams {
      effect = makeGlassEffect(effectParams)
      currentEffectParams = effectParams
    }

    if let cornerStyle {
      cornerConfiguration = UICornerConfiguration(style: cornerStyle)
    } else if let systemCornerConfiguration {
      // Without the reset a reused view would keep the corners of the previous block, and an
      // expression could never drop the rounding once it had been applied.
      cornerConfiguration = systemCornerConfiguration
    }

    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: contentView
    )
    setNeedsLayout()
  }

  private func reportUIStyle(_ userInterfaceStyle: UIUserInterfaceStyle) {
    uiStyleUpdater?(userInterfaceStyle == .dark ? .dark : .light)
  }

  private func makeGlassEffect(_ params: EffectParams) -> UIVisualEffect {
    let glassEffect = switch params.style {
    case .regular:
      UIGlassEffect(style: .regular)
    case .clear:
      UIGlassEffect(style: .clear)
    }
    if let isInteractive = params.isInteractive {
      glassEffect.isInteractive = isInteractive
    }
    if let tintColor = params.tintColor {
      glassEffect.tintColor = tintColor.systemColor
    }
    return glassEffect
  }
}

@available(iOS 26, *)
extension UICornerConfiguration {
  fileprivate init(style: LiquidGlassBlock.CornerStyle) {
    switch style {
    case let .capsule(maximumRadius):
      self = .capsule(maximumRadius: maximumRadius)
    case let .corners(topLeft, topRight, bottomLeft, bottomRight):
      self = .corners(
        topLeftRadius: topLeft.map { .fixed($0) },
        topRightRadius: topRight.map { .fixed($0) },
        bottomLeftRadius: bottomLeft.map { .fixed($0) },
        bottomRightRadius: bottomRight.map { .fixed($0) }
      )
    }
  }
}
#endif
