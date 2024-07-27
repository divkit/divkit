import CoreGraphics
import Foundation
import UIKit

import VGSL

extension DecoratingBlock {
  static func makeBlockView() -> BlockView {
    DecoratingView()
  }

  func isBestViewForReuse(_ view: BlockView) -> Bool {
    guard let view = view as? DecoratingView,
          let sourceBlock = view.model.source.value as? DecoratingBlock else {
      return false
    }

    return isPreceded(by: sourceBlock) || sourceBlock == self
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
      visibilityParams: visibilityParams,
      tooltips: tooltips,
      accessibility: accessibilityElement,
      reuseId: reuseId
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

extension DecoratingBlock {
  private func isPreceded(by block: DecoratingBlock) -> Bool {
    guard let selfPath = self.childPath,
          let blockPath = block.childPath else { return false }

    return selfPath == blockPath
  }
}

extension NonEmpty<[UserInterfaceAction]>? {
  fileprivate var hasPayload: Bool {
    switch self {
    case let .some(actions):
      !actions.filter { $0.payload != .empty }.isEmpty
    case .none:
      false
    }
  }
}

extension LongTapActions? {
  fileprivate var hasPayload: Bool {
    self?.hasPayload ?? false
  }
}

extension LongTapActions {
  fileprivate var hasPayload: Bool {
    switch self {
    case let .actions(actions):
      !actions.filter { $0.payload != .empty }.isEmpty
    case .contextMenu:
      false
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
    let visibilityParams: VisibilityParams?
    let tooltips: [BlockTooltip]
    let accessibility: AccessibilityElement?
    let reuseId: String?

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

  fileprivate var model: Model!
  private weak var observer: ElementStateObserver?
  private weak var renderingDelegate: RenderingDelegate?
  private(set) var childView: BlockView?

  private var contextMenuDelegate: NSObjectProtocol?

  private var visibilityActionPerformers: VisibilityActionPerformers?
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { childView.asArray() }
  var effectiveBackgroundColor: UIColor? { backgroundColor }

  private var tapRecognizer: UITapGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      tapRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var doubleTapRecognizer: UITapGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      doubleTapRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var longPressRecognizer: UILongPressGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      longPressRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

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
      if let borderLayer {
        borderLayer.zPosition = 1000
        layer.addSublayer(borderLayer)
      }
    }
  }

  private var blurView: UIVisualEffectView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let blurView {
        blurView.isUserInteractionEnabled = false
        if let childView {
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
  }

  private func configureRecognizers() {
    guard model.shouldHandleTap else {
      tapRecognizer?.isEnabled = false
      doubleTapRecognizer?.isEnabled = false
      longPressRecognizer?.isEnabled = false
      return
    }

    if tapRecognizer == nil {
      tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
    }

    if model.shouldHandleDoubleTap, doubleTapRecognizer == nil {
      let doubleTapRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
      doubleTapRecognizer.numberOfTapsRequired = 2
      self.doubleTapRecognizer = doubleTapRecognizer
      tapRecognizer?.require(toFail: doubleTapRecognizer)
    }

    if model.shouldHandleLongTap, longPressRecognizer == nil {
      longPressRecognizer = UILongPressGestureRecognizer(
        target: self,
        action: #selector(handleLongPress)
      )
    }

    tapRecognizer?.isEnabled = model.shouldHandleTap
    doubleTapRecognizer?.isEnabled = model.shouldHandleDoubleTap
    longPressRecognizer?.isEnabled = model.shouldHandleLongTap
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError("init(coder:) has not been implemented") }

  @objc private func handleTap(recognizer: UITapGestureRecognizer) {
    // Sometimes there are late touches that were performed before layout.
    // This breaks UX due to UIView reusing, so just skip them here.
    guard bounds.contains(recognizer.location(in: self)) else { return }
    model.analyticsURL.flatMap(AnalyticsUrlEvent.init(analyticsUrl:))?.sendFrom(self)
    let actions: [UserInterfaceAction]? = if recognizer === doubleTapRecognizer {
      model.doubleTapActions?.asArray()
    } else if recognizer === tapRecognizer {
      model.actions?.asArray()
    } else {
      []
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
    let oldModel = self.model

    if self.model?.tooltips.isEmpty ?? true, !model.tooltips.isEmpty {
      renderingDelegate?.tooltipAnchorViewAdded(anchorView: self)
    } else if self.model?.tooltips.isEmpty == false, model.tooltips.isEmpty {
      renderingDelegate?.tooltipAnchorViewRemoved(anchorView: self)
    }

    let shouldUpdateChildView = model.child !== self.model?.child || self.observer !== observer
    self.model = model
    self.observer = observer
    self.renderingDelegate = renderingDelegate

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

    updateContentBackgroundColor(animated: false)
    updateContentAlpha(animated: false)

    applyAccessibility(model.accessibility)
    model.actions?
      .forEach { applyAccessibility($0.accessibilityElement) }

    if oldModel?.visibilityParams != model.visibilityParams {
      if let visibilityParams = model.visibilityParams {
        visibilityActionPerformers = VisibilityActionPerformers(
          visibilityParams: visibilityParams,
          actionSender: self
        )
      } else {
        visibilityActionPerformers = nil
      }
    }

    configureRecognizers()

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

    perform(actionAnimation, animated: animated)
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

    if model.visibilityParams != nil {
      visibilityActionPerformers?.onVisibleBoundsChanged(to: to, bounds: bounds)
    }
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

  deinit {
    if model?.tooltips.isEmpty == false {
      renderingDelegate?.tooltipAnchorViewRemoved(anchorView: self)
    }
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

extension DecoratingView: TooltipAnchorView {
  var tooltips: [BlockTooltip] { model.tooltips }
}

extension DecoratingView.HighlightState {
  var alpha: CGFloat {
    switch self {
    case .normal: 1
    case .highlighted: 0.5
    }
  }
}

extension DecoratingView.Model {
  fileprivate func backgroundColor(
    for highlightState: DecoratingView.HighlightState
  ) -> Color {
    switch highlightState {
    case .normal: backgroundColor
    case .highlighted: highlightedBackgroundColor ?? backgroundColor
    }
  }

  fileprivate func actionAnimation(
    for highlightState: DecoratingView.HighlightState
  ) -> [TransitioningAnimation]? {
    switch highlightState {
    case .normal:
      actionAnimation?.touchUp
    case .highlighted:
      actionAnimation?.touchDown
    }
  }
}

private let contentAnimationDuration = UIStyles.AnimationDuration.touchHighlighting

extension ContextMenu {
  fileprivate func makeAlertController(
    sender: UIResponder
  ) -> UIAlertController {
    let alert = UIAlertController(title: title, message: nil, preferredStyle: .actionSheet)
    for item in items {
      let action = item.action
      let style: UIAlertAction.Style = item.isDestructive ? .destructive : .default
      alert.addAction(UIAlertAction(
        title: item.text,
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
    case .light: .light
    case .dark: .dark
    }
  }
}
