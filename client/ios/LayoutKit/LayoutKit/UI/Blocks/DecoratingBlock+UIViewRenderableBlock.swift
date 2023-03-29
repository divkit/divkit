import CoreGraphics
import Foundation
import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

extension DecoratingBlock {
  static func makeBlockView() -> BlockView {
    DecoratingView()
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    guard let view = view as? DecoratingView else { return false }
    return view.childView.map(child.canConfigureBlockView) ?? false
  }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! DecoratingView
    let model = DecoratingView.Model(
      child: child,
      backgroundColor: backgroundColor,
      highlightedBackgroundColor: highlightedBackgroundColor,
      actions: actions,
      actionAnimation: actionAnimation,
      doubleTapActions: doubleTapActions,
      longTapActions: longTapActions,
      analyticsURL: analyticsURL,
      boundary: boundary,
      border: border,
      childAlpha: childAlpha,
      blurEffect: blurEffect,
      paddings: paddings,
      source: Variable { [weak self] in self },
      visibilityActions: visibilityActions,
      tooltips: tooltips,
      accessibility: accessibilityElement
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

extension Optional where Wrapped == NonEmpty<[UserInterfaceAction]> {
  fileprivate var hasPayload: Bool {
    switch self {
    case let .some(actions):
      return !actions.filter { $0.payload != .empty }.isEmpty
    case .none:
      return false
    }
  }
}

extension Optional where Wrapped == LongTapActions {
  fileprivate var hasPayload: Bool {
    self?.hasPayload ?? false
  }
}

extension LongTapActions {
  fileprivate var hasPayload: Bool {
    switch self {
    case let .actions(actions):
      return !actions.filter { $0.payload != .empty }.isEmpty
    case .contextMenu:
      return false
    }
  }
}

private final class DecoratingView: UIControl, BlockViewProtocol, VisibleBoundsTrackingContainer,
  TooltipProtocol {
  enum HighlightState {
    case normal
    case highlighted
  }

  struct Model: ReferenceEquatable {
    let child: UIViewRenderable & AnyObject
    let backgroundColor: Color
    let highlightedBackgroundColor: Color?
    let actions: NonEmptyArray<UserInterfaceAction>?
    let actionAnimation: ActionAnimation?
    let doubleTapActions: NonEmptyArray<UserInterfaceAction>?
    let longTapActions: LongTapActions?
    let analyticsURL: URL?
    let boundary: BoundaryTrait
    let border: BlockBorder?
    let childAlpha: CGFloat
    let blurEffect: BlurEffect?
    let paddings: EdgeInsets
    let source: Variable<AnyObject?>
    let visibilityActions: [VisibilityAction]
    let tooltips: [BlockTooltip]
    let accessibility: AccessibilityElement?

    var hasResponsiveUI: Bool {
      actions.hasPayload || longTapActions.hasPayload || doubleTapActions.hasPayload
    }

    var shouldHandleTap: Bool {
      actions != nil || longTapActions != nil || doubleTapActions != nil || analyticsURL != nil
    }

    var shouldHandleLongTap: Bool {
      longTapActions != nil
    }

    var shouldHandleDoubleTap: Bool {
      doubleTapActions != nil
    }
  }

  private var model: Model!
  private weak var observer: ElementStateObserver?
  private(set) var childView: BlockView?

  private var contextMenuDelegate: NSObjectProtocol?

  private var visibilityActionPerformers: VisibilityActionPerformers?
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { childView.asArray() }
  var effectiveBackgroundColor: UIColor? { backgroundColor }

  private let tapRecognizer = UITapGestureRecognizer()
  private let doubleTapRecognizer = UITapGestureRecognizer()
  private let longPressRecognizer = UILongPressGestureRecognizer()

  override var isHighlighted: Bool {
    didSet {
      guard oldValue != isHighlighted else {
        return
      }

      updateContentHighlightState(animated: true)
    }
  }

  override var isEnabled: Bool {
    didSet {
      guard oldValue != isEnabled else {
        return
      }

      updateContentHighlightState(animated: false)
    }
  }

  private var highlightState: HighlightState {
    (isHighlighted || !isEnabled) && model.hasResponsiveUI
      ? .highlighted
      : .normal
  }

  private var borderLayer: CALayer? {
    didSet {
      oldValue?.removeFromSuperlayer()
      if let borderLayer = borderLayer {
        borderLayer.zPosition = 1000
        layer.addSublayer(borderLayer)
      }
    }
  }

  private var blurView: UIVisualEffectView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let blurView = blurView {
        blurView.isUserInteractionEnabled = false
        if let childView = childView {
          insertSubview(blurView, belowSubview: childView)
        } else {
          addSubview(blurView)
        }
      }
    }
  }

  init() {
    super.init(frame: .zero)
    isExclusiveTouch = true

    tapRecognizer.addTarget(self, action: #selector(handleTap))
    tapRecognizer.isEnabled = false
    addGestureRecognizer(tapRecognizer)

    doubleTapRecognizer.addTarget(self, action: #selector(handleTap))
    doubleTapRecognizer.numberOfTapsRequired = 2
    doubleTapRecognizer.isEnabled = false
    addGestureRecognizer(doubleTapRecognizer)

    longPressRecognizer.addTarget(self, action: #selector(handleLongPress))
    longPressRecognizer.isEnabled = false
    addGestureRecognizer(longPressRecognizer)

    tapRecognizer.require(toFail: doubleTapRecognizer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError("init(coder:) has not been implemented") }

  @objc private func handleTap(recognizer: UITapGestureRecognizer) {
    // Sometimes there are late touches that were performed before layout.
    // This breaks UX due to UIView reusing, so just skip them here.
    guard bounds.contains(recognizer.location(in: self)) else { return }
    model.analyticsURL.flatMap(AnalyticsUrlEvent.init(analyticsUrl:))?.sendFrom(self)
    let actions: [UserInterfaceAction]?
    if recognizer === doubleTapRecognizer {
      actions = model.doubleTapActions?.asArray()
    } else if recognizer === tapRecognizer {
      actions = model.actions?.asArray()
    } else {
      actions = []
    }
    actions?.perform(sendingFrom: self)
  }

  override func accessibilityActivate() -> Bool {
    guard let actions = model.actions?.asArray() else {
      return false
    }
    actions.perform(sendingFrom: self)
    return true
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    if result === self {
      return model.shouldHandleTap ? self : nil
    } else {
      return result
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let shouldMakeBorderLayer: Bool
    let boundary = model.boundary.makeInfo(for: bounds.size)
    shouldMakeBorderLayer = boundary.layer != nil
    layer.cornerRadius = boundary.radius
    layer.maskedCorners = boundary.corners
    layer.mask = boundary.layer

    if let border = model.border {
      if shouldMakeBorderLayer,
         let borderLayer = model.boundary.makeBorderLayer(for: bounds.size, border: border) {
        self.borderLayer = borderLayer
        layer.borderColor = nil
        layer.borderWidth = 0
      } else {
        borderLayer = nil
        layer.borderColor = border.color.cgColor
        layer.borderWidth = border.width
      }
    } else {
      borderLayer = nil
      layer.borderColor = nil
      layer.borderWidth = 0
    }

    blurView?.frame = bounds

    guard let view = childView else { return }

    let currentTransform = view.transform
    view.transform = .identity
    view.frame = bounds.inset(by: model.paddings)
    view.transform = currentTransform
  }

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard model != self.model || observer !== self.observer else {
      return
    }

    let shouldUpdateChildView = model.child !== self.model?.child || self.observer !== observer
    self.model = model
    self.observer = observer

    blurView = model.blurEffect.map { UIVisualEffectView(effect: UIBlurEffect(style: $0.cast())) }

    backgroundColor = model.backgroundColor.systemColor
    if shouldUpdateChildView {
      childView = model.child.reuse(
        childView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate,
        superview: self
      )
    }

    updateContentHighlightState(animated: false)

    applyAccessibility(model.accessibility)
    model.actions?
      .forEach { applyAccessibility($0.accessibilityElement) }

    if !model.visibilityActions.isEmpty {
      visibilityActionPerformers = VisibilityActionPerformers(
        visibilityCheckParams: model.visibilityActions
          .map { visibilityAction -> VisibilityCheckParam in
            VisibilityCheckParam(
              requiredVisibilityDuration: visibilityAction.requiredVisibilityDuration,
              targetPercentage: visibilityAction.targetPercentage,
              limiter: visibilityAction.limiter,
              action: { [unowned self] in
                UIActionEvent(
                  uiAction: visibilityAction.uiAction,
                  originalSender: self
                ).sendFrom(self)
              }
            )
          }
      )
    } else {
      visibilityActionPerformers = nil
    }

    tapRecognizer.isEnabled = model.shouldHandleTap
    doubleTapRecognizer.isEnabled = model.shouldHandleDoubleTap
    longPressRecognizer.isEnabled = model.shouldHandleLongTap

    if #available(iOS 13.0, *) {
      interactions.forEach(removeInteraction)
      contextMenuDelegate = nil
      if let longTapActions = model.longTapActions,
         case let .contextMenu(contextMenu) = longTapActions {
        let delegate = ContextMenuDelegate(contextMenu: contextMenu, view: self)
        contextMenuDelegate = delegate
        addInteraction(UIContextMenuInteraction(delegate: delegate))
      }
    }

    layer.masksToBounds = model.boundary.clipsToBounds

    setNeedsLayout()
  }

  private func updateContentHighlightState(animated: Bool) {
    updateContentBackgroundColor(animated: animated)

    guard let actionAnimation = model.actionAnimation(for: highlightState) else {
      return updateContentAlpha(animated: animated)
    }

    childView?.perform(actionAnimation, animated: animated)
  }

  private func updateContentAlpha(animated: Bool) {
    let alpha = highlightState.alpha
    let childAlpha = model.childAlpha
    let animations: Action = {
      self.childView?.alpha = alpha * childAlpha
    }

    if animated {
      UIView.animate(
        withDuration: contentAnimationDuration,
        animations: animations
      )
    } else {
      animations()
    }
  }

  private func updateContentBackgroundColor(animated: Bool) {
    let backgroundColor = model.backgroundColor(for: highlightState)

    let animations: Action = {
      self.backgroundColor = backgroundColor.systemColor
    }

    if animated {
      UIView.animate(
        withDuration: contentAnimationDuration,
        animations: animations
      )
    } else {
      animations()
    }
  }

  func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    passVisibleBoundsChanged(from: from, to: to)
    guard !model.visibilityActions.isEmpty else { return }
    visibilityActionPerformers?.onVisibleBoundsChanged(to: to, bounds: bounds)
  }

  func makeTooltipEvent(with info: TooltipInfo) -> TooltipEvent? {
    guard let tooltipModel = model.tooltips.first(where: { $0.id == info.id }) else { return nil }
    let tooltipView = tooltipModel.block.makeBlockView()
    let frame = tooltipModel.calculateFrame(targeting: bounds)
    tooltipView.frame = convert(frame, to: nil)
    return TooltipEvent(
      info: info,
      tooltipView: tooltipView,
      duration: tooltipModel.duration
    )
  }
}

extension DecoratingView {
  @objc func handleLongPress(recognizer: UIGestureRecognizer) {
    guard recognizer.state == .began else {
      return
    }

    if let longTapActions = model.longTapActions {
      cancelTracking(with: nil)
      switch longTapActions {
      case let .actions(actions):
        actions.forEach { $0.perform(sendingFrom: self) }
      case let .contextMenu(contextMenu):
        if #available(iOS 13.0, *) {} else {
          window?.rootViewController!.present(
            contextMenu.makeAlertController(sender: self),
            animated: true,
            completion: nil
          )
        }
      }
    }
  }
}

extension DecoratingView.HighlightState {
  var alpha: CGFloat {
    switch self {
    case .normal: return 1
    case .highlighted: return 0.5
    }
  }
}

extension DecoratingView.Model {
  fileprivate func backgroundColor(
    for highlightState: DecoratingView.HighlightState
  ) -> Color {
    switch highlightState {
    case .normal: return backgroundColor
    case .highlighted: return highlightedBackgroundColor ?? backgroundColor
    }
  }

  fileprivate func actionAnimation(
    for highlightState: DecoratingView.HighlightState
  ) -> [TransitioningAnimation]? {
    switch highlightState {
    case .normal:
      return actionAnimation?.touchUp
        .map { $0.modifyingFade(childAlpha: childAlpha) }
    case .highlighted:
      return actionAnimation?.touchDown
        .map { $0.modifyingFade(childAlpha: childAlpha) }
    }
  }
}

extension TransitioningAnimation {
  fileprivate func modifyingFade(childAlpha: CGFloat) -> Self {
    switch kind {
    case .fade:
      return TransitioningAnimation(
        kind: .fade,
        start: start * childAlpha,
        end: end * childAlpha,
        duration: duration,
        delay: delay,
        timingFunction: timingFunction
      )
    case .scaleXY, .translationX, .translationY:
      return self
    }
  }
}

private let contentAnimationDuration = UIStyles.AnimationDuration.touchHighlighting

extension ContextMenu {
  fileprivate func makeAlertController(
    sender: UIResponder
  ) -> UIAlertController {
    let alert = UIAlertController(title: title, message: nil, preferredStyle: .actionSheet)
    items.forEach {
      let action = $0.action
      let style: UIAlertAction.Style = $0.isDestructive ? .destructive : .default
      alert.addAction(UIAlertAction(
        title: $0.text,
        style: style,
        handler: { [action] _ in
          action.perform(sendingFrom: sender)
        }
      ))
    }

    alert.addAction(UIAlertAction(title: cancelTitle, style: .cancel, handler: nil))
    return alert
  }
}

extension BlurEffect {
  fileprivate func cast() -> UIBlurEffect.Style {
    switch self {
    case .light: return .light
    case .dark: return .dark
    }
  }
}
