#if os(iOS)
import Foundation
import UIKit
import VGSL

public final class TooltipContainerView: UIView, UIActionEventPerforming {
  private let tooltip: DefaultTooltipManager.Tooltip
  private let handleAction: (LayoutKit.UIActionEvent) -> Void
  private let onCloseAction: Action
  private let getViewById: (BlockViewID) -> BlockView?

  private var isClosing = false
  private var lastNonZeroBounds: CGRect?
  private var onVisibleBoundsChanged: Action?

  private var highlighted: (view: BlockView, snapshot: UIView)?

  private lazy var backgroundElement: UIAccessibilityElement? = {
    guard tooltip.params.closeByTapOutside else { return nil }

    let backgroundElement = ActivatableAccessibilityElement(
      activateAction: weakify(self, in: type(of: self).performTapOutsideActions),
      accessibilityContainer: self
    )
    backgroundElement.accessibilityLabel = tooltip.params.backgroundAccessibilityDescription
    backgroundElement.accessibilityTraits = .button
    return backgroundElement
  }()

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

      if let highlighted, let window {
        let frameInWindow = highlighted.view.convert(highlighted.view.bounds, to: window)
        highlighted.snapshot.frame = convert(frameInWindow, from: window)
      }
    }

    backgroundElement?.accessibilityFrameInContainerSpace = bounds

    onVisibleBoundsChanged?()
    onVisibleBoundsChanged = nil
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    handleAction(event)
  }

  public func close(animated: Bool) {
    guard !isClosing else { return }
    isClosing = true
    tooltip.view.onVisibleBoundsChanged(from: tooltip.view.bounds, to: .zero)
    tooltip.view.layoutIfNeeded()

    if let substrateView = tooltip.substrateView {
      substrateView.onVisibleBoundsChanged(
        from: substrateView.bounds,
        to: .zero
      )
    }

    if animated {
      if let substrateView = tooltip.substrateView {
        let duration = tooltip.params.animationOut?.map(\.duration)
          .max() ?? defaultAnimationDuration
        let animation = TransitioningAnimation(
          kind: .fade,
          start: 1,
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
          self.removeFromSuperview()
          self.onCloseAction()
        }
      } else {
        removeFromParentAnimated {
          self.onCloseAction()
        }
      }
    } else {
      removeFromSuperview()
      onCloseAction()
    }
  }

  func animateAppear() {
    if let substrateView = tooltip.substrateView {
      let duration = tooltip.params.animationIn?.map(\.duration).max() ?? defaultAnimationDuration
      let animation = TransitioningAnimation(
        kind: .fade,
        start: 0,
        end: 1,
        duration: duration,
        delay: 0,
        timingFunction: .easeInEaseOut
      )
      substrateView.setInitialParamsAndAnimate(animations: [animation])
    }

    if let animationIn = tooltip.params.animationIn {
      setInitialParamsAndAnimate(animations: animationIn)
    }
  }

  @objc private func handleTap(_ sender: UITapGestureRecognizer) {
    let point = sender.location(in: self)
    if !isPointInsideTooltip(point) {
      performTapOutsideActions()
    }
  }

  private func performTapOutsideActions() {
    let uiActionEvents = tooltip.params.tapOutsideActions.map {
      UIActionEvent(uiAction: $0, originalSender: self)
    }
    perform(uiActionEvents: uiActionEvents, from: self)

    if tooltip.params.closeByTapOutside {
      close(animated: true)
    }
  }

  private func isPointInsideTooltip(_ point: CGPoint, event: UIEvent? = nil) -> Bool {
    tooltip.view.point(inside: tooltip.view.convert(point, from: self), with: event)
  }

  private func createViewSnapshot(from view: UIView) -> UIView? {
    guard let snapshotView = view.snapshotView(afterScreenUpdates: true) else {
      return nil
    }

    snapshotView.frame = view.bounds
    snapshotView.isUserInteractionEnabled = false
    snapshotView.isAccessibilityElement = view.isAccessibilityElement
    snapshotView.accessibilityLabel = view.accessibilityLabel
    snapshotView.accessibilityHint = view.accessibilityHint
    snapshotView.accessibilityTraits = view.accessibilityTraits
    snapshotView.accessibilityValue = view.accessibilityValue
    return snapshotView
  }
}

private final class ActivatableAccessibilityElement: UIAccessibilityElement {
  private let activateAction: Action

  init(
    activateAction: @escaping Action,
    accessibilityContainer container: Any
  ) {
    self.activateAction = activateAction
    super.init(accessibilityContainer: container)
  }

  override func accessibilityActivate() -> Bool {
    activateAction()
    return true
  }

  override func accessibilityPerformEscape() -> Bool {
    activateAction()
    return true
  }
}

private let defaultAnimationDuration: CGFloat = 0.3
#endif
