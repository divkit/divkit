#if os(iOS)
import Foundation
import UIKit
import VGSL

public final class TooltipContainerView: UIView, UIActionEventPerforming {
  private enum State {
    case notAppeared
    case appearing
    case waitingForAnimatedClose
    case visible
    case closing
    case closed
  }

  private let tooltip: DefaultTooltipManager.Tooltip
  private let handleAction: (LayoutKit.UIActionEvent) -> Void
  private let onCloseAction: Action
  private let getViewById: (BlockViewID) -> BlockView?

  private var state = State.notAppeared
  private var lastNonZeroBounds: CGRect?
  private var onVisibleBoundsChanged: Action?

  private var highlighted: (view: BlockView, snapshot: UIView)?
  private var highlightObserver: AncestorFrameObserver?

  private lazy var backgroundElement: UIAccessibilityElement? = {
    guard isModal, tooltip.params.closeByTapOutside else { return nil }

    let backgroundElement = ActivatableAccessibilityElement(
      activateAction: { [weak self] in
        self?.performTapOutsideActions() ?? false
      },
      accessibilityContainer: self
    )
    backgroundElement.accessibilityLabel = tooltip.params.backgroundAccessibilityDescription
    backgroundElement.accessibilityTraits = .button
    return backgroundElement
  }()

  private var acceptsInteraction: Bool {
    switch state {
    case .notAppeared, .appearing, .visible:
      true
    case .waitingForAnimatedClose, .closing, .closed:
      false
    }
  }

  var isModal: Bool {
    tooltip.params.mode == .modal
  }

  public init(
    tooltip: DefaultTooltipManager.Tooltip,
    handleAction: @escaping (LayoutKit.UIActionEvent) -> Void,
    onCloseAction: @escaping Action,
    getViewById: @escaping (BlockViewID) -> BlockView?
  ) {
    self.tooltip = tooltip
    self.handleAction = handleAction
    self.onCloseAction = onCloseAction
    self.getViewById = getViewById
    let tooltipView = tooltip.view
    let tooltipBounds = tooltipView.bounds
    onVisibleBoundsChanged = { [weak tooltipView] in
      tooltipView?.onVisibleBoundsChanged(from: .zero, to: tooltipBounds)
    }

    super.init(frame: .zero)

    if isModal {
      let tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
      addGestureRecognizer(tapRecognizer)
    }

    if let substrateView = tooltip.substrateView {
      addSubview(substrateView)

      if let topViewId = tooltip.bringToTopId,
         let topView = getViewById(BlockViewID(rawValue: topViewId)),
         let snapshot = createViewSnapshot(from: topView) {
        highlighted = (view: topView, snapshot: snapshot)
        highlightObserver = AncestorFrameObserver(view: topView) { [weak self] in
          self?.setNeedsLayout()
        }
        addSubview(snapshot)
      }
    }

    addSubview(tooltipView)

    if let backgroundElement {
      accessibilityElements = [tooltipView, backgroundElement]
    }
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError()
  }

  public override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    guard acceptsInteraction else { return isModal ? self : nil }

    if !isPointInsideTooltip(point, event: event), !isModal {
      if tooltip.params.closeByTapOutside {
        DispatchQueue.main.async {
          self.close(animated: true)
        }
      }
      return nil
    } else {
      let result = super.hitTest(point, with: event)
      if result === tooltip.view {
        DispatchQueue.main.async {
          self.close(animated: true)
        }
        return nil
      }

      return result
    }
  }

  public override func layoutSubviews() {
    super.layoutSubviews()

    if let lastNonZeroBounds,
       lastNonZeroBounds != bounds {
      close(animated: true)
    }

    if bounds != .zero {
      lastNonZeroBounds = bounds
    }

    if let substrateView = tooltip.substrateView {
      substrateView.frame = bounds
      updateHighlightedSnapshotFrame()
    }

    backgroundElement?.accessibilityFrameInContainerSpace = bounds

    onVisibleBoundsChanged?()
    onVisibleBoundsChanged = nil
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    handleAction(event)
  }

  public func close(animated: Bool) {
    accessibilityElements = nil

    switch state {
    case .appearing:
      if animated, tooltip.params.animationIn?.isEmpty == false {
        state = .waitingForAnimatedClose
      } else {
        performClose(animated: animated)
      }
    case .waitingForAnimatedClose:
      if !animated {
        performClose(animated: false)
      }
    case .notAppeared, .visible:
      performClose(animated: animated)
    case .closing, .closed:
      break
    }
  }

  func animateAppear() {
    guard case .notAppeared = state else { return }

    let animationIn = tooltip.params.animationIn.flatMap { $0.isEmpty ? nil : $0 }
    state = animationIn != nil ? .appearing : .visible

    if let substrateView = tooltip.substrateView {
      let duration = animationIn?.map(\.duration).max() ?? defaultAnimationDuration
      let animation = TransitioningAnimation(
        kind: .fade,
        start: 0,
        end: 1,
        duration: duration,
        delay: 0,
        timingFunction: .easeInEaseOut
      )
      substrateView.setInitialParamsAndAnimate(animations: [animation]) { [weak self] in
        if animationIn == nil {
          self?.completeAppear()
        }
      }
    }

    if let animationIn {
      setInitialParamsAndAnimate(animations: animationIn) { [weak self] in
        self?.completeAppear()
      }
    }
  }

  private func completeAppear() {
    switch state {
    case .appearing:
      state = .visible
    case .waitingForAnimatedClose:
      performClose(animated: true)
    case .notAppeared,
         .visible,
         .closing,
         .closed:
      break
    }
  }

  private func performClose(animated: Bool) {
    let isAppearing = if case .appearing = state { true } else { false }
    let substrateStartOpacity: CGFloat? = if let substrateView = tooltip.substrateView {
      Self.substrateStartOpacity(
        presentationOpacity: substrateView.layer.presentation()?.opacity,
        modelOpacity: substrateView.alpha,
        isAppearing: isAppearing
      )
    } else {
      nil
    }
    state = .closing
    highlightObserver = nil
    tooltip.view.onVisibleBoundsChanged(from: tooltip.view.bounds, to: .zero)
    tooltip.view.layoutIfNeeded()

    if let substrateView = tooltip.substrateView {
      substrateView.onVisibleBoundsChanged(
        from: substrateView.bounds,
        to: .zero
      )
    }
    tooltip.notifyContentDidClose()

    if animated {
      if let substrateView = tooltip.substrateView {
        let duration = tooltip.params.animationOut?.map(\.duration)
          .max() ?? defaultAnimationDuration
        let animation = TransitioningAnimation(
          kind: .fade,
          start: substrateStartOpacity ?? substrateView.alpha,
          end: 0,
          duration: duration,
          delay: 0,
          timingFunction: .easeInEaseOut
        )
        substrateView.setInitialParamsAndAnimate(animations: [animation]) {
          substrateView.removeFromSuperview()
        }
      }

      if let animationOut = tooltip.params.animationOut {
        setInitialParamsAndAnimate(animations: animationOut) {
          self.completeClose()
        }
      } else {
        removeFromParentAnimated {
          self.completeClose()
        }
      }
    } else {
      completeClose()
    }
  }

  private func completeClose() {
    guard case .closing = state else { return }
    state = .closed
    removeFromSuperview()
    onCloseAction()
  }

  static func substrateStartOpacity(
    presentationOpacity: Float?,
    modelOpacity: CGFloat,
    isAppearing: Bool
  ) -> CGFloat {
    CGFloat(presentationOpacity ?? (isAppearing ? 0 : Float(modelOpacity)))
  }

  @objc private func handleTap(_ sender: UITapGestureRecognizer) {
    guard acceptsInteraction else { return }

    let point = sender.location(in: self)
    if !isPointInsideTooltip(point) {
      _ = performTapOutsideActions()
    }
  }

  private func performTapOutsideActions() -> Bool {
    guard acceptsInteraction else { return false }

    let uiActionEvents = tooltip.params.tapOutsideActions.map {
      UIActionEvent(uiAction: $0, originalSender: self)
    }
    perform(uiActionEvents: uiActionEvents, from: self)

    if tooltip.params.closeByTapOutside {
      close(animated: true)
    }
    return true
  }

  private func isPointInsideTooltip(_ point: CGPoint, event: UIEvent? = nil) -> Bool {
    tooltip.view.point(inside: tooltip.view.convert(point, from: self), with: event)
  }

  private func updateHighlightedSnapshotFrame() {
    guard let highlighted, let window, highlighted.view.window != nil else { return }

    let frameInWindow = highlighted.view.convert(highlighted.view.bounds, to: window)
    highlighted.snapshot.frame = convert(frameInWindow, from: window)
    highlighted.snapshot.isHidden = false
  }

  private func createViewSnapshot(from view: UIView) -> UIView? {
    guard let snapshotView = view.snapshotView(afterScreenUpdates: true) else {
      return nil
    }

    snapshotView.frame = view.bounds
    snapshotView.isHidden = true
    snapshotView.isUserInteractionEnabled = false
    snapshotView.isAccessibilityElement = view.isAccessibilityElement
    snapshotView.accessibilityLabel = view.accessibilityLabel
    snapshotView.accessibilityHint = view.accessibilityHint
    snapshotView.accessibilityTraits = view.accessibilityTraits
    snapshotView.accessibilityValue = view.accessibilityValue
    return snapshotView
  }
}

extension DefaultTooltipManager.Tooltip {
  func notifyContentDidClose() {
    (view as? TooltipContentClosing)?.tooltipDidClose()
    (substrateView as? TooltipContentClosing)?.tooltipDidClose()
  }
}

private final class ActivatableAccessibilityElement: UIAccessibilityElement {
  private let activateAction: () -> Bool

  init(
    activateAction: @escaping () -> Bool,
    accessibilityContainer container: Any
  ) {
    self.activateAction = activateAction
    super.init(accessibilityContainer: container)
  }

  override func accessibilityActivate() -> Bool {
    activateAction()
  }

  override func accessibilityPerformEscape() -> Bool {
    activateAction()
  }
}

private let defaultAnimationDuration: CGFloat = 0.3
#endif
