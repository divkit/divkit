import Foundation
import UIKit
import VGSL

public final class TooltipContainerView: UIView, UIActionEventPerforming {
  private let tooltip: DefaultTooltipManager.Tooltip
  private let handleAction: (LayoutKit.UIActionEvent) -> Void
  private let onCloseAction: Action

  private var isClosing = false
  private var lastNonZeroBounds: CGRect?
  private var onVisibleBoundsChanged: Action?

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

  public init(
    tooltip: DefaultTooltipManager.Tooltip,
    handleAction: @escaping (LayoutKit.UIActionEvent) -> Void,
    onCloseAction: @escaping Action
  ) {
    self.tooltip = tooltip
    self.handleAction = handleAction
    self.onCloseAction = onCloseAction
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

    addSubview(tooltipView)

    if let backgroundElement {
      accessibilityElements = [tooltipView, backgroundElement]
    }
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError()
  }

  var isModal: Bool {
    tooltip.params.mode == .modal
  }

  func animateAppear() {
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

  public override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    if !isPointInsideTooltip(point, event: event), !isModal {
      if tooltip.params.closeByTapOutside {
        DispatchQueue.main.async {
          self.close()
        }
      }
      return nil
    } else {
      return super.hitTest(point, with: event)
    }
  }

  public override func layoutSubviews() {
    super.layoutSubviews()

    if let lastNonZeroBounds,
       lastNonZeroBounds != bounds {
      close()
    }

    if bounds != .zero {
      lastNonZeroBounds = bounds
    }

    backgroundElement?.accessibilityFrameInContainerSpace = bounds

    onVisibleBoundsChanged?()
    onVisibleBoundsChanged = nil
  }

  public func perform(uiActionEvent event: LayoutKit.UIActionEvent, from _: AnyObject) {
    handleAction(event)
  }

  public func close() {
    guard !isClosing else { return }
    isClosing = true
    tooltip.view.onVisibleBoundsChanged(from: tooltip.view.bounds, to: .zero)

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
  }

  private func performTapOutsideActions() {
    let uiActionEvents = tooltip.params.tapOutsideActions.map {
      UIActionEvent(uiAction: $0, originalSender: self)
    }
    perform(uiActionEvents: uiActionEvents, from: self)

    if tooltip.params.closeByTapOutside {
      close()
    }
  }

  private func isPointInsideTooltip(_ point: CGPoint, event: UIEvent? = nil) -> Bool {
    tooltip.view.point(inside: tooltip.view.convert(point, from: self), with: event)
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
