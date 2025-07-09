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
      pressStartActions: pressStartActions,
      pressEndActions: pressEndActions,
      hoverStartActions: hoverStartActions,
      hoverEndActions: hoverEndActions,
      boundary: boundary,
      border: border,
      childAlpha: childAlpha,
      blurEffect: blurEffect,
      paddings: paddings,
      source: Variable { [weak self] in self },
      visibilityParams: visibilityParams,
      tooltips: tooltips,
      accessibility: accessibilityElement,
      reuseId: reuseId,
      path: path,
      isFocused: isFocused,
      captureFocusOnAction: captureFocusOnAction
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
    let pressStartActions: NonEmptyArray<UserInterfaceAction>?
    let pressEndActions: NonEmptyArray<UserInterfaceAction>?
    let hoverStartActions: NonEmptyArray<UserInterfaceAction>?
    let hoverEndActions: NonEmptyArray<UserInterfaceAction>?
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
    let path: UIElementPath?
    let isFocused: Bool?
    let captureFocusOnAction: Bool

    var hasResponsiveUI: Bool {
      actions.hasPayload || longTapActions.hasPayload || doubleTapActions.hasPayload
    }

    var shouldHandleTap: Bool {
      actions != nil || longTapActions != nil || doubleTapActions != nil
    }

    var shouldHandleLongTap: Bool {
      longTapActions != nil
    }

    var shouldHandleDoubleTap: Bool {
      doubleTapActions != nil
    }

    var shouldHandlePress: Bool {
      pressStartActions != nil || pressEndActions != nil
    }

    var shouldHandleHover: Bool {
      hoverStartActions != nil || hoverEndActions != nil
    }

    var shouldHandleAnyAction: Bool {
      shouldHandleTap ||
        shouldHandleLongTap ||
        shouldHandleDoubleTap ||
        shouldHandlePress ||
        shouldHandleHover
    }
  }

  fileprivate var model: Model!
  private weak var observer: ElementStateObserver?
  private weak var renderingDelegate: RenderingDelegate?
  private(set) var childView: BlockView?

  private var contextMenuDelegate: NSObjectProtocol?

  private var animationStartTime: Date?
  private let animationMinimalDuration: TimeInterval = 0.125

  private var visibilityActionPerformers: VisibilityActionPerformers?
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { childView.asArray() }
  var effectiveBackgroundColor: UIColor? { backgroundColor }

  private var hasFocused = false
  private var isViewOnWindow: Bool { window != nil }

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

  private var hoverRecognizer: UIHoverGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      hoverRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  override var isHighlighted: Bool {
    didSet {
      guard oldValue != isHighlighted else {
        return
      }

      updateHighlightState(animated: true)
    }
  }

  override var isEnabled: Bool {
    didSet {
      guard oldValue != isEnabled else {
        return
      }

      updateHighlightState(animated: false)
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
    guard model.shouldHandleAnyAction else {
      tapRecognizer?.isEnabled = false
      doubleTapRecognizer?.isEnabled = false
      longPressRecognizer?.isEnabled = false
      hoverRecognizer?.isEnabled = false
      return
    }

    if tapRecognizer == nil {
      tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
    }
    tapRecognizer?.cancelsTouchesInView = false

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

    if model.shouldHandleHover, hoverRecognizer == nil {
      hoverRecognizer = UIHoverGestureRecognizer(
        target: self,
        action: #selector(handleHover)
      )
    }

    tapRecognizer?.isEnabled = model.shouldHandleTap
    doubleTapRecognizer?.isEnabled = model.shouldHandleDoubleTap
    longPressRecognizer?.isEnabled = model.shouldHandleLongTap
    hoverRecognizer?.isEnabled = model.shouldHandleHover
  }

  private func checkTouchableArea() {
    guard tapRecognizer != nil || doubleTapRecognizer != nil || longPressRecognizer != nil else {
      return
    }
    guard bounds.width > 0 && bounds.height > 0 else { return }
    if bounds.width < 44 || bounds.height < 44 {
      renderingDelegate?.reportRenderingError(
        message: "Touchable view is too small: \(bounds.size), \(model.child)",
        isWarning: true,
        path: model.path ?? UIElementPath("")
      )
    }
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError("init(coder:) has not been implemented") }

  @objc private func handleTap(recognizer: UITapGestureRecognizer) {
    // Sometimes there are late touches that were performed before layout.
    // This breaks UX due to UIView reusing, so just skip them here.
    guard bounds.contains(recognizer.location(in: self)) else { return }
    captureFocusIfNeeded()
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
    captureFocusIfNeeded()
    actions.perform(sendingFrom: self)
    return true
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    if result === self {
      return model.shouldHandleAnyAction ? self : nil
    } else {
      return result
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let shouldMakeBorderLayer: Bool
    let boundary = model.boundary.makeInfo(for: bounds.size)
    shouldMakeBorderLayer = boundary.layer != nil || model.border?.style
      .shouldMakeBorderLayer == true
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

    checkTouchableArea()

    guard let view = childView else { return }

    let currentTransform = view.transform
    view.transform = .identity
    view.frame = bounds.inset(by: model.paddings)
    view.transform = currentTransform
  }

  override func didMoveToWindow() {
    updateVoiceOverFocus()
  }

  override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesBegan(touches, with: event)
    model.pressStartActions?.asArray().perform(sendingFrom: self)
  }

  override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesEnded(touches, with: event)
    model.pressEndActions?.asArray().perform(sendingFrom: self)
  }

  override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
    super.touchesCancelled(touches, with: event)
    model.pressEndActions?.asArray().perform(sendingFrom: self)
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

    applyAccessibilityFromScratch(model.accessibility)
    model.actions?
      .forEach { applyAccessibility($0.accessibilityElement) }

    hasFocused = oldModel?.isFocused == false && model.isFocused == true
    updateVoiceOverFocus()

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

    interactions.forEach(removeInteraction)
    contextMenuDelegate = nil
    if let longTapActions = model.longTapActions,
       case let .contextMenu(contextMenu) = longTapActions {
      let delegate = ContextMenuDelegate(contextMenu: contextMenu, view: self)
      contextMenuDelegate = delegate
      addInteraction(UIContextMenuInteraction(delegate: delegate))
    }

    layer.masksToBounds = model.boundary.clipsToBounds

    setNeedsLayout()
  }

  private func updateHighlightState(animated: Bool) {
    updateContentBackgroundColor(animated: animated)

    guard let actionAnimation = model.actionAnimation(for: highlightState) else {
      return updateContentAlpha(animated: animated)
    }

    let startAnimation = DispatchWorkItem { [weak self] in
      self?.perform(actionAnimation, animated: animated) {
        self?.animationStartTime = nil
      }
      self?.animationStartTime = Date()
    }

    if let animationStartTime {
      let remainingTime: TimeInterval = max(
        0.0, animationMinimalDuration - Date().timeIntervalSince(animationStartTime)
      )

      DispatchQueue.main.asyncAfter(
        deadline: DispatchTime.now() + remainingTime,
        execute: startAnimation
      )

    } else {
      startAnimation.perform()
    }
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

  private func onVisibleBoundsChangedInternal(from: CGRect, to: CGRect) {
    passVisibleBoundsChanged(from: from, to: to)

    if model.visibilityParams != nil {
      visibilityActionPerformers?.onVisibleBoundsChanged(to: to, bounds: bounds)
    }
  }

  func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    if let child = childView as? DelayedVisibilityActionView {
      child.visibilityAction = { [weak self] in

        guard let self else { return }
        onVisibleBoundsChangedInternal(from: from, to: to)
      }
    } else {
      onVisibleBoundsChangedInternal(from: from, to: to)
    }
  }

  func makeTooltipEvent(with info: TooltipInfo) -> TooltipEvent? {
    guard let tooltipModel = model.tooltips.first(where: { $0.id == info.id }), let window else {
      return nil
    }
    let tooltipView = tooltipModel.block.makeBlockView()
    tooltipView.frame = tooltipModel.calculateFrame(
      targeting: convert(bounds, to: nil),
      constrainedBy: window.bounds
    )
    return TooltipEvent(
      info: info,
      params: tooltipModel.params,
      tooltipView: tooltipView,
      tooltipAnchorView: self
    )
  }

  private func updateVoiceOverFocus() {
    guard hasFocused, isViewOnWindow else { return }

    UIAccessibility.post(notification: .layoutChanged, argument: self)
  }

  private func captureFocusIfNeeded() {
    if model.captureFocusOnAction {
      observer?.clearFocus()
    }
  }

  @objc private func handleHover(recognizer: UIHoverGestureRecognizer) {
    switch recognizer.state {
    case .began:
      model.hoverStartActions?.asArray().perform(sendingFrom: self)
    case .ended, .cancelled, .failed:
      model.hoverEndActions?.asArray().perform(sendingFrom: self)
    default:
      break
    }
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

    captureFocusIfNeeded()

    if let longTapActions = model.longTapActions {
      cancelTracking(with: nil)
      switch longTapActions {
      case let .actions(actions):
        actions.forEach { $0.perform(sendingFrom: self) }
      case .contextMenu:
        break
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

extension BlurEffect {
  fileprivate func cast() -> UIBlurEffect.Style {
    switch self {
    case .light: .light
    case .dark: .dark
    }
  }
}

extension BlockBorder.Style {
  fileprivate var shouldMakeBorderLayer: Bool {
    switch self {
    case .dashed: true
    case .solid: false
    }
  }
}
